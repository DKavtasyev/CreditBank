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
		
		dataService.applyOfferAndSave(loanOffer);
		kafkaService.sendFinishRegistrationRequest(loanOffer.getStatementId());
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@Override
	public ResponseEntity<Void> calculateCredit(FinishingRegistrationRequestDto finishingRegistrationRequestDto, UUID statementId, BindingResult bindingResult) throws StatementNotFoundException, LoanRefusalException, InternalMicroserviceException, InvalidPreApproveException {
		Statement statement = dataService.findStatement(statementId);
		scoringService.scoreAndSaveCredit(finishingRegistrationRequestDto, statement);
		kafkaService.sendCreatingDocumentsRequest(statement);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@Override
	public ResponseEntity<Void> denyOffer(UUID statementId) throws StatementNotFoundException, InternalMicroserviceException {
		Statement statement = dataService.denyOffer(statementId);
		kafkaService.sendDenial(statement, "Вы отказались от кредита.");
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
}
