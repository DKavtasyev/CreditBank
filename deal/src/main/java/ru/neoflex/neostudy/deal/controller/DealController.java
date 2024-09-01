package ru.neoflex.neostudy.deal.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.constants.ChangeType;
import ru.neoflex.neostudy.common.dto.FinishingRegistrationRequestDto;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.exception.*;
import ru.neoflex.neostudy.deal.controller.annotations.DealControllerInterface;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.service.DataService;
import ru.neoflex.neostudy.deal.service.PreScoringService;
import ru.neoflex.neostudy.deal.service.ScoringService;
import ru.neoflex.neostudy.deal.service.kafka.KafkaService;

import java.util.List;
import java.util.UUID;

import static ru.neoflex.neostudy.common.constants.Theme.*;

@RestController
@RequiredArgsConstructor
public class DealController implements DealControllerInterface {
	private final PreScoringService preScoringService;
	private final ScoringService scoringService;
	private final DataService dataService;
	private final KafkaService kafkaService;
	
	@Override
	public ResponseEntity<List<LoanOfferDto>> createStatement(LoanStatementRequestDto loanStatementRequest, BindingResult bindingResult) throws InvalidPassportDataException, InternalMicroserviceException {
		if (bindingResult.hasErrors()) {
			throw new InternalMicroserviceException("MS deal: invalid input parameters of LoanStatementRequestDto");
		}
		Statement statement = dataService.prepareData(loanStatementRequest);
		List<LoanOfferDto> offers = preScoringService.getOffers(loanStatementRequest, statement);
		dataService.updateStatement(statement, ApplicationStatus.PREAPPROVAL, ChangeType.AUTOMATIC);
		return new ResponseEntity<>(offers, HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<Void> applyOffer(LoanOfferDto loanOffer) throws StatementNotFoundException, InternalMicroserviceException {
		Statement statement = dataService.applyOfferAndSave(loanOffer);
		kafkaService.sendKafkaMessage(statement, FINISH_REGISTRATION, null);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@Override
	public ResponseEntity<Void> calculateCredit(FinishingRegistrationRequestDto finishingRegistrationRequestDto, UUID statementId, BindingResult bindingResult) throws StatementNotFoundException, LoanRefusalException, InternalMicroserviceException, InvalidPreApproveException {
		Statement statement = dataService.findStatement(statementId);
		scoringService.scoreAndSaveCredit(finishingRegistrationRequestDto, statement);
		kafkaService.sendKafkaMessage(statement, CREATE_DOCUMENTS, null);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@Override
	public ResponseEntity<Void> denyOffer(UUID statementId) throws StatementNotFoundException, InternalMicroserviceException {
		Statement statement = dataService.denyOffer(statementId);
		kafkaService.sendKafkaMessage(statement, CLIENT_REJECTION, null);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
}
