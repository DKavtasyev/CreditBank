package ru.neoflex.neostudy.gateway.requester;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import ru.neoflex.neostudy.common.exception.*;

import java.net.URI;
import java.util.Objects;

import static ru.neoflex.neostudy.gateway.requester.Requester.CONNECTION_ERROR_TO_MS_DEAL;

/**
 * Сервис, осуществляющий перенаправление запросов для работы с документами на кредит к другим микросервисам.
 */
@SuppressWarnings({"DuplicatedCode", "GrazieInspection"}) // Дубликат кода, который невозможно объединить; некорректное замечание проверки правописания по словосочетанию "по указанному".
@Service
@RequiredArgsConstructor
public class DocumentsRequestService {
	private final Requester requester;
	
	/**
	 * Отсылает запрос на создание документов по указанному URL-адресу.
	 * @param uri URL-адрес, по которому отправляется запрос.
	 * @throws StatementNotFoundException выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 * не найден в базе данных.
	 * @throws InternalMicroserviceException если при запросе возникла ошибка или МС Deal недоступен.
	 * @throws DocumentSignatureException документ не может быть создан, если не получены все данные для оформления
	 * кредита.
	 */
	public void requestCreatingDocuments(URI uri) throws StatementNotFoundException, InternalMicroserviceException, DocumentSignatureException {
		try {
			sendRequest(uri);
		}
		catch (RestClientException e) {
			throw new InternalMicroserviceException(CONNECTION_ERROR_TO_MS_DEAL, e);
		}
	}
	
	/**
	 * Отправляет запрос в МС Deal на формирование кода ПЭП и подписание документов.
	 * @param uri URL-адрес, по которому отправляется запрос.
	 * @throws StatementNotFoundException выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 * не найден в базе данных.
	 * @throws InternalMicroserviceException если при запросе возникла ошибка или МС Deal недоступен.
	 * @throws DocumentSignatureException если документ не создан.
	 */
	public void requestSignatureOfDocuments(URI uri) throws StatementNotFoundException, InternalMicroserviceException, DocumentSignatureException {
		try {
			sendRequest(uri);
		}
		catch (RestClientException e) {
			throw new InternalMicroserviceException(CONNECTION_ERROR_TO_MS_DEAL, e);
		}
	}
	
	/**
	 * Отправляет запрос в МС Deal на проверку подписи документа.
	 * @param uri URL-адрес, по которому отправляется запрос. В параметре {@code code} передаётся код подписи документа.
	 * @throws StatementNotFoundException выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 * не найден в базе данных.
	 * @throws InternalMicroserviceException если при запросе возникла ошибка или МС Deal недоступен.
	 * @throws SignatureVerificationFailedException если проверка подписи завершилась неудачно:
	 * <ul>
	 *     <li>Документ подделан или изменён.</li>
	 *     <li>Подпись недействительна.</li>
	 *     <li>Подпись не передана или имеет неверный формат.</li>
	 * </ul>
	 * @throws DocumentSignatureException если не выполнены условия для проверки подписи:
	 * <ul>
	 *     <li>Документ ранее не был создан.</li>
	 *     <li>Документ ранее не был подписан.</li>
	 * </ul>
	 */
	public void requestVerifyingSesCode(URI uri) throws StatementNotFoundException, InternalMicroserviceException, SignatureVerificationFailedException, DocumentSignatureException {
		RequestEntity<Void> requestEntity = requester.getRequestEntity(uri);
		
		try {
			sendRequestToVerifySesCode(requestEntity);
		}
		catch (RestClientException e) {
			throw new InternalMicroserviceException(CONNECTION_ERROR_TO_MS_DEAL, e);
		}
	}
	
	/**
	 * Отправляет запрос в МС Deal на проверку подписи документа.
	 * @param requestEntity сформированный для выполнения запроса объект {@code RequestEntity}. В нём указан URL-адрес,
	 * тип ожидаемого ответа.
	 * @throws SignatureVerificationFailedException если проверка подписи завершилась неудачно:
	 * <ul>
	 *     <li>Документ подделан или изменён.</li>
	 *     <li>Подпись недействительна.</li>
	 *     <li>Подпись не передана или имеет неверный формат.</li>
	 * </ul>
	 * @throws StatementNotFoundException выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 * не найден в базе данных.
	 * @throws DocumentSignatureException если не выполнены условия для проверки подписи:
	 * <ul>
	 *     <li>Документ ранее не был создан.</li>
	 *     <li>Документ ранее не был подписан.</li>
	 * </ul>
	 * @throws InternalMicroserviceException если при запросе возникла ошибка или МС Deal недоступен.
	 */
	// https://github.com/spring-projects/spring-framework/issues/21321 - при использовании статуса ответа 401 для случая неудачной проверки подписи теряется тело ответа
	private void sendRequestToVerifySesCode(RequestEntity<Void> requestEntity) throws SignatureVerificationFailedException, StatementNotFoundException, DocumentSignatureException, InternalMicroserviceException {
		try {
			ParameterizedTypeReference<ExceptionDetails> responseType = new ParameterizedTypeReference<>() {};
			requester.request(requestEntity, responseType);
		}
		catch (HttpClientErrorException e) {
			ExceptionDetails exceptionDetails = e.getResponseBodyAs(ExceptionDetails.class);
			Objects.requireNonNull(exceptionDetails);
			if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(400))) {
				throw new SignatureVerificationFailedException(exceptionDetails.getMessage());
			}
			else if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
				throw new StatementNotFoundException(exceptionDetails.getMessage());
			}
			else if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(422))) {
				throw new DocumentSignatureException(exceptionDetails.getMessage());
			}
			else if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(500))) {
				throw new InternalMicroserviceException(exceptionDetails.getMessage());
			}
		}
	}
	
	/**
	 * Отправляет запрос в МС Deal по указанному URL-адресу.
	 * @param uri URL-адрес, по которому отправляется запрос.
	 * @throws StatementNotFoundException выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 * не найден в базе данных.
	 * @throws DocumentSignatureException не выполнены предусловия для выполнения действий с документом.
	 * @throws InternalMicroserviceException если при запросе возникла ошибка или МС Deal недоступен.
	 */
	private void sendRequest(URI uri) throws StatementNotFoundException, DocumentSignatureException, InternalMicroserviceException {
		try {
			RequestEntity<Void> requestEntity = requester.getRequestEntity(uri);
			ParameterizedTypeReference<Void> responseType = new ParameterizedTypeReference<>() {};
			requester.request(requestEntity, responseType);
		}
		catch (HttpClientErrorException e) {
			ExceptionDetails exceptionDetails = e.getResponseBodyAs(ExceptionDetails.class);
			Objects.requireNonNull(exceptionDetails);
			if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
				throw new StatementNotFoundException(exceptionDetails.getMessage());
			}
			else if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(422))) {
				throw new DocumentSignatureException(exceptionDetails.getMessage());
			}
			else if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(500))) {
				throw new InternalMicroserviceException(exceptionDetails.getMessage());
			}
		}
	}
}
