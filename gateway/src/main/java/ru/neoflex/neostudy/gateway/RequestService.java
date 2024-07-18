package ru.neoflex.neostudy.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.dto.FinishingRegistrationRequestDto;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.InvalidPreScoreParametersException;
import ru.neoflex.neostudy.common.exception.InvalidUserDataException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.gateway.requester.StatementRequester;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestService {
	
	private final StatementRequester statementRequester;
	
	@Value("${app.rest.url.statement.create-loan-statement}")
	private String createLoanStatementUrl;
	@Value("${app.rest.url.statement.apply-offer}")
	private String applyOfferUrl;
	@Value("${app.rest.url.deal.calculate-credit}")
	private String calculateCreditUrl;
	@Value("${app.rest.url.deal.deny-offer}")
	private String denyOfferUrl;
	@Value("${app.rest.url.deal.create-documents}")
	private String createDocumentsUrl;
	@Value("${app.rest.url.deal.sign-documents}")
	private String signDocumentsUrl;
	@Value("${app.rest.url.deal.verify-signature}")
	private String verifySignatureUrl;
	@Value("${app.rest.url.deal.update-statement-status}")
	private String updateStatementStatusUrl;
	@Value("${app.rest.url.deal.get-statement}")
	private String getStatementUrl;
	@Value("${app.rest.url.deal.get-all-statements}")
	private String getAllStatementsUrl;
	
	public List<LoanOfferDto> createLoanStatementRequest(LoanStatementRequestDto loanStatementRequest) throws InvalidUserDataException, InternalMicroserviceException {
		return statementRequester.requestLoanOffers(loanStatementRequest, createLoanStatementUrl);
	}
	
	public void applyOfferRequest(LoanOfferDto loanOffer) throws StatementNotFoundException, InvalidPreScoreParametersException, JsonProcessingException {
		statementRequester.sendRequest(loanOffer, applyOfferUrl);
	}
	
	public void finishRegistrationRequest(FinishingRegistrationRequestDto finishingRegistrationRequestDto) {
	
	}
	
	public void denyOfferRequest(UUID statementId) {
	
	}
}
