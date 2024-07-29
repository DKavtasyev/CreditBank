package ru.neoflex.neostudy.gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.neostudy.common.dto.FinishingRegistrationRequestDto;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.exception.*;
import ru.neoflex.neostudy.gateway.controller.annotations.StatementRestControllerInterface;
import ru.neoflex.neostudy.gateway.service.RequestService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class StatementRestController implements StatementRestControllerInterface {
	private final RequestService requestService;
	
	
	@Override
	public ResponseEntity<List<LoanOfferDto>> createLoanStatement(LoanStatementRequestDto loanStatementRequest) throws InvalidUserDataException, InternalMicroserviceException {
		List<LoanOfferDto> offers = requestService.createLoanStatementRequest(loanStatementRequest);
		return ResponseEntity.status(HttpStatus.OK).body(offers);
	}
	
	@Override
	public ResponseEntity<Void> applyOffer(LoanOfferDto loanOffer) throws StatementNotFoundException, InternalMicroserviceException {
		requestService.applyOfferRequest(loanOffer);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@Override
	public ResponseEntity<Void> finishRegistration(FinishingRegistrationRequestDto finishingRegistrationRequestDto, UUID statementId) throws StatementNotFoundException, InternalMicroserviceException, LoanRefusalException, InvalidPreApproveException {
		requestService.finishRegistrationRequest(finishingRegistrationRequestDto, statementId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@Override
	public ResponseEntity<Void> denyOffer(UUID statementId) throws StatementNotFoundException, InternalMicroserviceException {
		requestService.denyOfferRequest(statementId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
