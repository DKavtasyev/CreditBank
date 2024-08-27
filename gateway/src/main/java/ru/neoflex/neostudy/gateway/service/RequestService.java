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
import ru.neoflex.neostudy.common.util.UrlBuilder;
import ru.neoflex.neostudy.gateway.requester.AdminRequestService;
import ru.neoflex.neostudy.gateway.requester.DocumentsRequestService;
import ru.neoflex.neostudy.gateway.requester.StatementRequestService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;

/**
 * Сервис верхнего уровня, осуществляющий маршрутизацию запросов к микросервисам.
 */
@Service
@RequiredArgsConstructor
public class RequestService {
	private final StatementRequestService statementRequestService;
	private final DocumentsRequestService documentsRequestService;
	private final AdminRequestService adminRequestService;
	public static final String SCHEME = "http";
	
	@Value("${app.rest.request.statement.host}")
	private String statementHost;
	@Value("${app.rest.request.statement.port}")
	private String statementPort;
	@Value("${app.rest.request.statement.create-loan-statement-path}")
	private String createLoanStatementPath;
	@Value("${app.rest.request.statement.apply-offer-path}")
	private String applyOfferPath;
	
	@Value("${app.rest.request.deal.host}")
	private String dealHost;
	@Value("${app.rest.request.deal.port}")
	private String dealPort;
	@Value("${app.rest.request.deal.calculate-credit-path}")
	private String calculateCreditPath;
	@Value("${app.rest.request.deal.deny-offer-path}")
	private String denyOfferPath;
	@Value("${app.rest.request.deal.create-documents-path}")
	private String createDocumentPath;
	@Value("${app.rest.request.deal.sign-document-path}")
	private String signDocumentPath;
	@Value("${app.rest.request.deal.verify-signature-path}")
	private String verifySignaturePath;
	@Value("${app.rest.request.deal.update-statement-status-path}")
	private String updateStatementStatusPath;
	@Value("${app.rest.request.deal.get-statement-path}")
	private String getStatementPath;
	@Value("${app.rest.request.deal.get-all-statements-path}")
	private String getAllStatementsPath;
	
	/**
	 * Перенаправляет запрос с пользовательскими данными {@code LoanStatementRequestDto} в микросервис statement.
	 * Возвращает List, полученный в теле ответа от МС statement, содержащий четыре кредитных предложения.
	 * @param loanStatementRequest данные запроса кредита от пользователя.
	 * @return список доступных предложений кредита.
	 * @throws InvalidUserDataException      если данные из пользовательского запроса кредита не прошли прескоринг или
	 *                                       паспортные данные из LoanStatementRequestDto не совпадают с паспортными данными клиента из базы данных.
	 * @throws InternalMicroserviceException при запросе возникла ошибка или один из микросервисов оказался недоступен.
	 */
	public List<LoanOfferDto> createLoanStatementRequest(LoanStatementRequestDto loanStatementRequest) throws InvalidUserDataException, InternalMicroserviceException {
		URI uri = UrlBuilder.builder()
				.init(SCHEME, statementHost, statementPort)
				.addPath(createLoanStatementPath)
				.build();
		return statementRequestService.requestLoanOffers(loanStatementRequest, uri);
	}
	
	/**
	 * Перенаправляет запрос с выбранным пользователем кредитным предложением в микросервис statement.
	 * @param loanOffer выбранное пользователем кредитное предложение.
	 * @throws StatementNotFoundException    выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 *                                       не найден в базе данных (ответ от МС statement пришёл с кодом 404 Not found).
	 * @throws InternalMicroserviceException при запросе возникла ошибка или один из микросервисов оказался недоступен.
	 */
	public void applyOfferRequest(LoanOfferDto loanOffer) throws StatementNotFoundException, InternalMicroserviceException {
		URI uri = UrlBuilder.builder()
				.init(SCHEME, statementHost, statementPort)
				.addPath(applyOfferPath)
				.build();
		statementRequestService.sendChosenOffer(loanOffer, uri);
	}
	
	/**
	 * Перенаправляет запрос на завершение регистрации кредита в МС deal.
	 * @param finishingRegistrationRequestDto пользовательские данные для завершения оформления кредита.
	 * @param statementId                     идентификатор заявки пользователя {@code Statement}.
	 * @throws StatementNotFoundException    выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 *                                       не найден в базе данных (ответ от МС statement пришёл с кодом 404 Not found).
	 * @throws InternalMicroserviceException при запросе возникла ошибка или один из микросервисов оказался недоступен.
	 * @throws LoanRefusalException          если данные от пользователя не прошли скоринг (ответ от МС deal пришёл со статусом
	 *                                       406 Not acceptable).
	 * @throws InvalidPreApproveException    если предварительное одобрение кредита для данной заявки на кредит отсутствует
	 *                                       или недействительно (ответ от МС deal пришёл со статусом 428 Precondition required).
	 */
	public void finishRegistrationRequest(FinishingRegistrationRequestDto finishingRegistrationRequestDto, UUID statementId) throws StatementNotFoundException, InternalMicroserviceException, LoanRefusalException, InvalidPreApproveException {
		URI uri = UrlBuilder.builder()
				.init(SCHEME, dealHost, dealPort)
				.addPath(calculateCreditPath, statementId.toString())
				.build();
		statementRequestService.requestForFinishRegistration(finishingRegistrationRequestDto, uri);
	}
	
	/**
	 * Перенаправляет запрос с отказом пользователя от кредита в МС deal.
	 * @param statementId идентификатор заявки пользователя {@code Statement}.
	 * @throws StatementNotFoundException    выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 *                                       не найден в базе данных (ответ от МС statement пришёл с кодом 404 Not found).
	 * @throws InternalMicroserviceException при запросе возникла ошибка или один из микросервисов оказался недоступен.
	 */
	public void denyOfferRequest(UUID statementId) throws StatementNotFoundException, InternalMicroserviceException {
		URI uri = UrlBuilder.builder()
				.init(SCHEME, dealHost, dealPort)
				.addPath(denyOfferPath, statementId.toString())
				.build();
		statementRequestService.sendOfferDenial(uri);
	}
	
	/**
	 * Перенаправляет запрос на создание документов на кредит в МС deal.
	 * @param statementId идентификатор заявки пользователя {@code Statement}.
	 * @throws StatementNotFoundException    выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 *                                       не найден в базе данных (ответ от МС statement пришёл с кодом 404 Not found).
	 * @throws InternalMicroserviceException при запросе возникла ошибка или один из микросервисов оказался недоступен.
	 */
	public void createDocuments(UUID statementId) throws StatementNotFoundException, InternalMicroserviceException, DocumentSignatureException {
		URI uri = UrlBuilder.builder()
				.init(SCHEME, dealHost, dealPort)
				.addPath(createDocumentPath, statementId.toString())
				.build();
		documentsRequestService.requestCreatingDocuments(uri);
	}
	
	/**
	 * Перенаправляет запрос на формирование кода ПЭП и подписание документов в МС deal.
	 * @param statementId идентификатор заявки пользователя {@code Statement}.
	 * @throws StatementNotFoundException    выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 *                                       не найден в базе данных (ответ от МС statement пришёл с кодом 404 Not found).
	 * @throws InternalMicroserviceException при запросе возникла ошибка или один из микросервисов оказался недоступен.
	 */
	public void signDocuments(UUID statementId) throws StatementNotFoundException, InternalMicroserviceException, DocumentSignatureException {
		URI uri = UrlBuilder.builder()
				.init(SCHEME, dealHost, dealPort)
				.addPath(signDocumentPath, statementId.toString())
				.build();
		documentsRequestService.requestSignatureOfDocuments(uri);
	}
	
	/**
	 * Перенаправляет запрос на проверку кода подписи документов в МС deal.
	 * @param statementId идентификатор заявки пользователя {@code Statement}.
	 * @param signature   подпись в формате {@code String}.
	 * @throws SignatureVerificationFailedException если проверка на подлинность показала, что документ не является
	 *                                              подлинным или подпись для проверки не была передана (HTTP-код 401 Not authorized).
	 * @throws StatementNotFoundException           выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 *                                              не найден в базе данных (ответ от МС statement пришёл с кодом 404 Not found).
	 * @throws InternalMicroserviceException        при запросе возникла ошибка или один из микросервисов оказался недоступен.
	 */
	public void verifySesCode(UUID statementId, String signature) throws SignatureVerificationFailedException, StatementNotFoundException, InternalMicroserviceException, DocumentSignatureException {
		URI uri = UrlBuilder.builder()
				.init(SCHEME, dealHost, dealPort)
				.addPath(verifySignaturePath, statementId.toString())
				.addQueryParameter("code", signature)
				.build();
		documentsRequestService.requestVerifyingSesCode(uri);
	}
	
	/**
	 * Перенаправляет запрос на изменение статуса заявки на кредит {@code Statement} в МС deal.
	 * @param statementId идентификатор заявки пользователя {@code Statement}.
	 * @param status      значение статуса в виде enum типа {@code ApplicationStatus}.
	 * @throws StatementNotFoundException    выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 *                                       не найден в базе данных (ответ от МС statement пришёл с кодом 404 Not found).
	 * @throws InternalMicroserviceException при запросе возникла ошибка или один из микросервисов оказался недоступен.
	 */
	public void updateStatementStatus(UUID statementId, ApplicationStatus status) throws StatementNotFoundException, InternalMicroserviceException {
		URI uri = UrlBuilder.builder()
				.init(SCHEME, dealHost, dealPort)
				.addPath(updateStatementStatusPath, statementId.toString())
				.build();
		adminRequestService.sendStatementStatus(status, uri);
	}
	
	/**
	 * Перенаправляет запрос в МС deal и возвращает объект {@code Statement} по идентификатору {@code statementId}.
	 * @param statementId идентификатор заявки пользователя {@code Statement}.
	 * @return объект {@code Statement} из базы данных.
	 * @throws StatementNotFoundException    выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 *                                       не найден в базе данных (ответ от МС statement пришёл с кодом 404 Not found).
	 * @throws InternalMicroserviceException при запросе возникла ошибка или один из микросервисов оказался недоступен.
	 */
	public Statement getStatement(UUID statementId) throws StatementNotFoundException, InternalMicroserviceException {
		URI uri = UrlBuilder.builder()
				.init(SCHEME, dealHost, dealPort)
				.addPath(getStatementPath, statementId.toString())
				.build();
		return adminRequestService.requestStatement(uri);
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
		URI uri = UrlBuilder.builder()
				.init(SCHEME, dealHost, dealPort)
				.addPath(getAllStatementsPath)
				.addQueryParameter("page", pageAsString)
				.build();
		return adminRequestService.requestAllStatements(uri);
	}
}
