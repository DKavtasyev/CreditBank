package ru.neoflex.neostudy.gateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.dto.FinishingRegistrationRequestDto;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.entity.Statement;
import ru.neoflex.neostudy.common.exception.*;
import ru.neoflex.neostudy.common.util.UrlFormatter;
import ru.neoflex.neostudy.gateway.requester.Requester;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestService {
	
	private final Requester requester;
	
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
		return requester.requestLoanOffers(loanStatementRequest, URI.create(createLoanStatementUrl));
	}
	
	public void applyOfferRequest(LoanOfferDto loanOffer) throws StatementNotFoundException, InternalMicroserviceException {
		requester.sendChosenOffer(loanOffer, URI.create(applyOfferUrl));
	}
	
	public void finishRegistrationRequest(FinishingRegistrationRequestDto finishingRegistrationRequestDto, UUID statementId) throws StatementNotFoundException, InternalMicroserviceException, LoanRefusalException {
		URI uri = UrlFormatter.substituteUrlValue(calculateCreditUrl, statementId.toString());
		requester.sendFinishRegistrationRequest(finishingRegistrationRequestDto, uri);
	}
	
	public void denyOfferRequest(UUID statementId) throws StatementNotFoundException, InternalMicroserviceException {
		URI uri = UrlFormatter.substituteUrlValue(denyOfferUrl, statementId.toString());
		requester.sendOfferDenial(uri);
	}
	
	public void createDocuments(UUID statementId) throws StatementNotFoundException, InternalMicroserviceException {
		URI uri = UrlFormatter.substituteUrlValue(createDocumentsUrl, statementId.toString());
		requester.requestCreatingDocuments(uri);
	}
	
	public void signDocuments(UUID statementId) throws StatementNotFoundException, InternalMicroserviceException {
		URI uri = UrlFormatter.substituteUrlValue(signDocumentsUrl, statementId.toString());
		requester.requestSignatureOfDocuments(uri);
	}
	
	public void verifySesCode(UUID statementId, String signature) throws DocumentSigningException, StatementNotFoundException, InternalMicroserviceException {
		URI uri = UrlFormatter.substituteUrlValue(verifySignatureUrl, statementId.toString());
		uri = UrlFormatter.addQueryParameter(uri.toString(), "code", signature);
		requester.requestVerifyingSesCode(uri);
	}
	
	public void updateStatementStatus(UUID statementId, ApplicationStatus status) throws StatementNotFoundException, InternalMicroserviceException {
		URI uri = UrlFormatter.substituteUrlValue(updateStatementStatusUrl, statementId.toString());
		requester.sendStatementStatus(status, uri);
	}
	
	public Statement getStatement(UUID statementId) throws StatementNotFoundException, InternalMicroserviceException {
		URI uri = UrlFormatter.substituteUrlValue(getStatementUrl, statementId.toString());
		return requester.requestStatement(uri);
	}
	
	public List<Statement> getAllStatements() throws InternalMicroserviceException {
		return requester.requestAllStatements(URI.create(getAllStatementsUrl));
	}
}
