package ru.neoflex.neostudy.gateway.requester;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.exception.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatementRequester {
	
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;
	
	public List<LoanOfferDto> requestLoanOffers(LoanStatementRequestDto loanStatementRequestDto, String url) throws InvalidUserDataException, InternalMicroserviceException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		RequestEntity<LoanStatementRequestDto> requestEntity = RequestEntity
				.post(URI.create(url))
				.headers(headers)
				.body(loanStatementRequestDto);
		
		ResponseEntity<String> responseEntity;
		List<LoanOfferDto> offers = new ArrayList<>();
		try {
			try {
				responseEntity = restTemplate.exchange(requestEntity, String.class);
				offers = objectMapper.readValue(responseEntity.getBody(), objectMapper.getTypeFactory().constructCollectionType(List.class, LoanOfferDto.class));
			}
			catch (HttpClientErrorException e) {
				if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(400))){
					ExceptionDetails exceptionDetails = objectMapper.readValue(e.getResponseBodyAsString(), ExceptionDetails.class);
					throw new InvalidUserDataException(exceptionDetails.getMessage());
				}
				else if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(500))) {
					ExceptionDetails exceptionDetails = objectMapper.readValue(e.getResponseBodyAsString(), ExceptionDetails.class);
					throw new InternalMicroserviceException(exceptionDetails.getMessage());
				}
			}
		}
		catch (RestClientException e) {
			throw new InternalMicroserviceException("Connection error to MS statement", e);
		}
		catch (JsonProcessingException e) {
			throw new InternalMicroserviceException("Can't deserialize value", e);
		}
		return offers;
	}
	
	public void sendRequest(Object object, String url) throws StatementNotFoundException, JsonProcessingException, InvalidPreScoreParametersException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		RequestEntity<Object> requestEntity = RequestEntity
				.post(url)
				.headers(headers)
				.body(object);
		
		try {
			restTemplate.exchange(requestEntity, Void.class);
		}
		catch (HttpClientErrorException e) {
			if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(400))){
				ExceptionDetails exceptionDetails = objectMapper.readValue(e.getResponseBodyAsString(), ExceptionDetails.class);
				throw new InvalidPreScoreParametersException(exceptionDetails.getMessage());
			}
			else if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))){
				ExceptionDetails exceptionDetails = objectMapper.readValue(e.getResponseBodyAsString(), ExceptionDetails.class);
				throw new StatementNotFoundException(exceptionDetails.getMessage());
			}
		}
	}
}
