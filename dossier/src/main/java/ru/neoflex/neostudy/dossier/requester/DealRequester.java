package ru.neoflex.neostudy.dossier.requester;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.exception.ExceptionDetails;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;

import java.net.URI;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class DealRequester {
	public static final String DEAL_STATUS_URL = "http://localhost:8082/deal/admin/statement/%s/status";
	
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;
	
	public void sendStatementStatus(UUID statementId, ApplicationStatus status) throws JsonProcessingException, StatementNotFoundException, InternalMicroserviceException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		String url = String.format(DEAL_STATUS_URL, statementId);
		
		RequestEntity<ApplicationStatus> requestEntity = RequestEntity
				.put(URI.create(url))
				.headers(headers)
				.body(status);
		
		try {
			restTemplate.exchange(requestEntity, String.class);
			log.info("Request to {} sent, content: {}", DEAL_STATUS_URL, status);
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
	}
}
