package ru.neoflex.neostudy.gateway.requester;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.entity.Statement;
import ru.neoflex.neostudy.common.exception.*;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import static ru.neoflex.neostudy.gateway.requester.Requester.CONNECTION_ERROR_TO_MS_DEAL;

/**
 * Сервис, осуществляющий перенаправление запросов Администратора к микросервисам.
 */
@Service
@RequiredArgsConstructor
public class AdminRequestService {
	private final Requester requester;
	
	/**
	 * Отправляет запрос на изменение статуса заявки на значение, указанное в параметрах метода.
	 * @param status значение, на которое следует изменить статус заявки.
	 * @param uri url-адрес, по которому будет отправлен запрос.
	 * @throws StatementNotFoundException выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 * не найден в базе данных.
	 * @throws InternalMicroserviceException если при запросе возникла ошибка или МС Deal недоступен.
	 */
	public void sendStatementStatus(ApplicationStatus status, URI uri) throws StatementNotFoundException, InternalMicroserviceException {
		
		try {
			sendStatementStatusRequest(status, uri);
		}
		catch (RestClientException e) {
			throw new InternalMicroserviceException(CONNECTION_ERROR_TO_MS_DEAL, e);
		}
	}
	
	/**
	 * Отправляет запрос на изменение статуса заявки на значение статуса, указанное в параметрах метода.
	 * @param status значение, на которое следует изменить статус заявки.
	 * @param uri url-адрес, по которому будет отправлен запрос.
	 * @throws StatementNotFoundException выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 * не найден в базе данных.
	 */
	private void sendStatementStatusRequest(ApplicationStatus status, URI uri) throws StatementNotFoundException {
		try {
			RequestEntity<ApplicationStatus> requestEntity = requester.getRequestEntityWithBodyMethodPut(status, uri);
			ParameterizedTypeReference<Void> responseType = new ParameterizedTypeReference<>() {};
			requester.request(requestEntity, responseType);
		}
		catch (HttpClientErrorException e) {
			ExceptionDetails exceptionDetails = e.getResponseBodyAs(ExceptionDetails.class);
			Objects.requireNonNull(exceptionDetails);
			if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
				throw new StatementNotFoundException(exceptionDetails.getMessage());
			}
		}
	}
	
	/**
	 * Возвращает объект заявки на кредит Statement по её идентификатору statementId, указанному в url-адресе запроса.
	 * @param uri url-адрес, по которому осуществляется запрос заявки на кредит.
	 * @return запрошенная заявка на кредит Statement.
	 * @throws StatementNotFoundException выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 * не найден в базе данных.
	 * @throws InternalMicroserviceException если при запросе возникла ошибка или МС Deal недоступен.
	 */
	public Statement requestStatement(URI uri) throws StatementNotFoundException, InternalMicroserviceException {
		Statement statement;
		ParameterizedTypeReference<Statement> responseType = new ParameterizedTypeReference<>() {};
		try {
			statement = sendStatementRequest(uri, responseType);
		}
		catch (RestClientException e) {
			throw new InternalMicroserviceException(CONNECTION_ERROR_TO_MS_DEAL, e);
		}
		return statement;
	}
	
	/**
	 * Делает запрос в МС Deal и возвращает полученный по идентификатору statementId, указанному в url-адресе запроса,
	 * объект заявки на кредит Statement.
	 * @param uri url-адрес, по которому осуществляется запрос заявки на кредит.
	 * @param responseType объект ParameterizedTypeReference, содержащий в себе тип возвращаемого значения Statement.
	 * @return запрошенная заявка на кредит Statement.
	 * @throws StatementNotFoundException выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 * не найден в базе данных.
	 */
	private Statement sendStatementRequest(URI uri, ParameterizedTypeReference<Statement> responseType) throws StatementNotFoundException {
		Statement statement = null;
		try {
			RequestEntity<Void> requestEntity = requester.getRequestEntityMethodGet(uri);
			ResponseEntity<Statement> responseEntity = requester.request(requestEntity, responseType);
			statement = responseEntity.getBody();
		}
		catch (HttpClientErrorException e) {
			ExceptionDetails exceptionDetails = e.getResponseBodyAs(ExceptionDetails.class);
			Objects.requireNonNull(exceptionDetails);
			if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
				throw new StatementNotFoundException(exceptionDetails.getMessage());
			}
		}
		return statement;
	}
	
	/**
	 * Делает запрос в МС Deal и возвращает все содержащиеся в базе данных заявки на кредит Statement.
	 * @param uri url-адрес, по которому осуществляется запрос всех заявок.
	 * @return {@code List}, содержащий все заявки на кредит.
	 * @throws InternalMicroserviceException если при запросе возникла ошибка или МС Deal недоступен.
	 */
	public List<Statement> requestAllStatements(URI uri) throws InternalMicroserviceException {
		RequestEntity<Void> requestEntity = requester.getRequestEntityMethodGet(uri);
		ParameterizedTypeReference<List<Statement>> responseType = new ParameterizedTypeReference<>() {};
		
		List<Statement> statements;
		try {
			ResponseEntity<List<Statement>> responseEntity = requester.request(requestEntity, responseType);
			statements = responseEntity.getBody();
		}
		catch (RestClientException e) {
			throw new InternalMicroserviceException(CONNECTION_ERROR_TO_MS_DEAL, e);
		}
		return statements;
	}	
}
