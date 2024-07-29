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

import static java.util.Objects.isNull;

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
	
	/**
	 * Перенаправляет запрос с пользовательскими данными {@code LoanStatementRequestDto} в микросервис statement.
	 * Возвращает List, полученный в ответе от МС statement, содержащий четыре кредитных предложения.
	 * @param loanStatementRequest данные запроса кредита от пользователя.
	 * @return список доступных предложений кредита.
	 * @throws InvalidUserDataException если данные из пользовательского запроса кредита не прошли прескоринг или
	 * паспортные данные из LoanStatementRequestDto не совпадают с паспортными данными клиента из базы данных.
	 * @throws InternalMicroserviceException при запросе возникла ошибка или один из микросервисов оказался недоступен.
	 */
	public List<LoanOfferDto> createLoanStatementRequest(LoanStatementRequestDto loanStatementRequest) throws InvalidUserDataException, InternalMicroserviceException {
		return requester.requestLoanOffers(loanStatementRequest, URI.create(createLoanStatementUrl));
	}
	
	/**
	 * Перенаправляет запрос с выбранным пользователем кредитным предложением в микросервис statement.
	 * @param loanOffer выбранное пользователем кредитное предложение.
	 * @throws StatementNotFoundException выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 * не найден в базе данных (ответ от МС statement пришёл с кодом 404 Not found).
	 * @throws InternalMicroserviceException при запросе возникла ошибка или один из микросервисов оказался недоступен.
	 */
	public void applyOfferRequest(LoanOfferDto loanOffer) throws StatementNotFoundException, InternalMicroserviceException {
		requester.sendChosenOffer(loanOffer, URI.create(applyOfferUrl));
	}
	
	/**
	 * Перенаправляет запрос на завершение регистрации кредита в МС deal.
	 * @param finishingRegistrationRequestDto пользовательские данные для завершения оформления кредита.
	 * @param statementId идентификатор заявки пользователя {@code Statement}.
	 * @throws StatementNotFoundException выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 * не найден в базе данных (ответ от МС statement пришёл с кодом 404 Not found).
	 * @throws InternalMicroserviceException при запросе возникла ошибка или один из микросервисов оказался недоступен.
	 * @throws LoanRefusalException если данные от пользователя не прошли скоринг (ответ от МС deal пришёл со статусом
	 * 406 Not acceptable).
	 * @throws InvalidPreApproveException если предварительное одобрение кредита для данной заявки на кредит отсутствует
	 * или недействительно (ответ от МС deal пришёл со статусом 428 Precondition required).
	 */
	public void finishRegistrationRequest(FinishingRegistrationRequestDto finishingRegistrationRequestDto, UUID statementId) throws StatementNotFoundException, InternalMicroserviceException, LoanRefusalException, InvalidPreApproveException {
		URI uri = UrlFormatter.substituteUrlValue(calculateCreditUrl, statementId.toString());
		requester.sendFinishRegistrationRequest(finishingRegistrationRequestDto, uri);
	}
	
	/**
	 * Перенаправляет запрос с отказом пользователя от кредита в МС deal.
	 * @param statementId идентификатор заявки пользователя {@code Statement}.
	 * @throws StatementNotFoundException выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 * не найден в базе данных (ответ от МС statement пришёл с кодом 404 Not found).
	 * @throws InternalMicroserviceException при запросе возникла ошибка или один из микросервисов оказался недоступен.
	 */
	public void denyOfferRequest(UUID statementId) throws StatementNotFoundException, InternalMicroserviceException {
		URI uri = UrlFormatter.substituteUrlValue(denyOfferUrl, statementId.toString());
		requester.sendOfferDenial(uri);
	}
	
	/**
	 * Перенаправляет запрос на создание документов на кредит в МС deal.
	 * @param statementId идентификатор заявки пользователя {@code Statement}.
	 * @throws StatementNotFoundException выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 * не найден в базе данных (ответ от МС statement пришёл с кодом 404 Not found).
	 * @throws InternalMicroserviceException при запросе возникла ошибка или один из микросервисов оказался недоступен.
	 */
	public void createDocuments(UUID statementId) throws StatementNotFoundException, InternalMicroserviceException {
		URI uri = UrlFormatter.substituteUrlValue(createDocumentsUrl, statementId.toString());
		requester.requestCreatingDocuments(uri);
	}
	
	/**
	 * Перенаправляет запрос на формирование кода ПЭП и подписание документов в МС deal.
	 * @param statementId идентификатор заявки пользователя {@code Statement}.
	 * @throws StatementNotFoundException выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 * не найден в базе данных (ответ от МС statement пришёл с кодом 404 Not found).
	 * @throws InternalMicroserviceException при запросе возникла ошибка или один из микросервисов оказался недоступен.
	 */
	public void signDocuments(UUID statementId) throws StatementNotFoundException, InternalMicroserviceException {
		URI uri = UrlFormatter.substituteUrlValue(signDocumentsUrl, statementId.toString());
		requester.requestSignatureOfDocuments(uri);
	}
	
	/**
	 * Перенаправляет запрос на проверку кода подписи документов в МС deal.
	 * @param statementId идентификатор заявки пользователя {@code Statement}.
	 * @param signature подпись в формате {@code String}.
	 * @throws SignatureVerificationFailedException выбрасывается в случае использования некорректного кода подписи.
	 * @throws StatementNotFoundException выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 * не найден в базе данных (ответ от МС statement пришёл с кодом 404 Not found).
	 * @throws InternalMicroserviceException при запросе возникла ошибка или один из микросервисов оказался недоступен.
	 */
	public void verifySesCode(UUID statementId, String signature) throws SignatureVerificationFailedException, StatementNotFoundException, InternalMicroserviceException {
		URI uri = UrlFormatter.substituteUrlValue(verifySignatureUrl, statementId.toString());
		uri = UrlFormatter.addQueryParameter(uri.toString(), "code", signature);
		requester.requestVerifyingSesCode(uri);
	}
	
	/**
	 * Перенаправляет запрос на изменение статуса заявки на кредит {@code Statement} в МС deal.
	 * @param statementId идентификатор заявки пользователя {@code Statement}.
	 * @param status значение статуса в виде enum типа {@code ApplicationStatus}.
	 * @throws StatementNotFoundException выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 * не найден в базе данных (ответ от МС statement пришёл с кодом 404 Not found).
	 * @throws InternalMicroserviceException при запросе возникла ошибка или один из микросервисов оказался недоступен.
	 */
	public void updateStatementStatus(UUID statementId, ApplicationStatus status) throws StatementNotFoundException, InternalMicroserviceException {
		URI uri = UrlFormatter.substituteUrlValue(updateStatementStatusUrl, statementId.toString());
		requester.sendStatementStatus(status, uri);
	}
	
	/**
	 * Перенаправляет запрос в МС deal и возвращает объект {@code Statement} по идентификатору {@code statementId}.
	 * @param statementId идентификатор заявки пользователя {@code Statement}.
	 * @return объект {@code Statement} из базы данных.
	 * @throws StatementNotFoundException выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 * не найден в базе данных (ответ от МС statement пришёл с кодом 404 Not found).
	 * @throws InternalMicroserviceException при запросе возникла ошибка или один из микросервисов оказался недоступен.
	 */
	public Statement getStatement(UUID statementId) throws StatementNotFoundException, InternalMicroserviceException {
		URI uri = UrlFormatter.substituteUrlValue(getStatementUrl, statementId.toString());
		return requester.requestStatement(uri);
	}
	
	/**
	 * Перенаправляет запрос в МС deal, возвращает список объектов {@code Statement} из базы данных постранично, по
	 * {@code pageSize} элементов на страницу.
	 * @param page номер страницы.
	 * @return {@code List<Statement>} с прочитанными из базы данных объектами {@code Statement}.
	 * @throws InternalMicroserviceException при запросе возникла ошибка или один из микросервисов оказался недоступен.
	 */
	public List<Statement> getAllStatements(Integer page) throws InternalMicroserviceException {
		String pageAsString = isNull(page) ? "" : String.valueOf(page);
		URI uri = UrlFormatter.addQueryParameter(getAllStatementsUrl, "page", pageAsString);
		return requester.requestAllStatements(uri);
	}
}
