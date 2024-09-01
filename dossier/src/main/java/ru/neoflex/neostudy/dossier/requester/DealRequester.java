package ru.neoflex.neostudy.dossier.requester;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.common.exception.dto.ExceptionDetails;
import ru.neoflex.neostudy.common.util.UrlBuilder;

import java.net.URI;
import java.util.UUID;

/**
 * Сервис, осуществляющий запросы к МС Deal.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class DealRequester {
	@Value("${app.rest.request.deal.host}")
	private String dealHost;
	@Value("${app.rest.request.deal.port}")
	private String dealPort;
	@Value("${app.rest.request.deal.status-path}")
	private String statusPath;
	
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;
	
	/**
	 * Отправляет PUT-запрос в МС deal с идентификатором запроса в URL-адресе, с переданным в аргументах метода статусом,
	 * представляющим собой значение enum типа {@code ApplicationStatus}.
	 * @param statementId идентификатор заявки пользователя {@code Statement}.
	 * @param status значение статуса в виде enum типа {@code ApplicationStatus}, отсылаемое в теле запроса.
	 * @throws StatementNotFoundException выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 * не найден в базе данных (если от микросервиса deal получен ответ со статусом 404 Not found).
	 * @throws InternalMicroserviceException если при запросе возникла ошибка или МС deal недоступен.
	 */
	public void sendStatementStatus(UUID statementId, ApplicationStatus status) throws StatementNotFoundException, InternalMicroserviceException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		URI url = UrlBuilder.builder()
				.init("http", dealHost, dealPort)
				.addPath(statusPath, statementId.toString())
				.build();
		
		RequestEntity<ApplicationStatus> requestEntity = RequestEntity
				.put(url)
				.headers(headers)
				.body(status);
		
		try {
			sendRequest(statementId, requestEntity);
			log.info("Request to {} sent, content: {}", url, status);
		}
		catch (JsonProcessingException e) {
			throw new InternalMicroserviceException("Can't deserialize value", e);
		}
	}
	
	/**
	 * Производит запрос в соответствии с переданным RequestEntity.
	 * @param statementId идентификатор заявки пользователя {@code Statement}.
	 * @param requestEntity сформированный объект запроса {@code RequestEntity}.
	 * @throws JsonProcessingException если не удалось десериализовать значение из тела принятого ответа.
	 * @throws StatementNotFoundException выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 * не найден в базе данных (если от микросервиса deal получен ответ со статусом 404 Not found).
	 * @throws InternalMicroserviceException если при запросе возникла ошибка или МС deal недоступен.
	 */
	private void sendRequest(UUID statementId, RequestEntity<ApplicationStatus> requestEntity) throws JsonProcessingException, StatementNotFoundException, InternalMicroserviceException {
		try {
			restTemplate.exchange(requestEntity, String.class);
		}
		catch (HttpClientErrorException e) {
			if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))){
				ExceptionDetails exceptionDetails = objectMapper.readValue(e.getResponseBodyAsString(), ExceptionDetails.class);
				log.warn(exceptionDetails.getMessage());
				throw new StatementNotFoundException(String.format(exceptionDetails.getMessage(), statementId));
			}
			else {
				log.error("Error sending request, cause: {}", e.getMessage());
				throw new InternalMicroserviceException("Microservice error", e);
			}
		}
		catch (RestClientException e) {
			throw new InternalMicroserviceException("Connection error to MS deal", e);
		}
	}
}
