package ru.neoflex.neostudy.gateway.requester;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.dto.FinishingRegistrationRequestDto;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.entity.Statement;
import ru.neoflex.neostudy.common.exception.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Requester {
	
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;
	
	public List<LoanOfferDto> requestLoanOffers(LoanStatementRequestDto loanStatementRequestDto, URI uri) throws InvalidUserDataException, InternalMicroserviceException {
		RequestEntity<LoanStatementRequestDto> requestEntity = getRequestEntityWithBody(loanStatementRequestDto, uri);
		
		ResponseEntity<String> responseEntity;
		List<LoanOfferDto> offers = new ArrayList<>();
		try {
			try {
				responseEntity = restTemplate.exchange(requestEntity, String.class);
				offers = objectMapper.readValue(responseEntity.getBody(), objectMapper.getTypeFactory().constructCollectionType(List.class, LoanOfferDto.class));
			}
			catch (HttpClientErrorException e) {
				if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(400))) {
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
	
	public void sendChosenOffer(LoanOfferDto loanOfferDto, URI uri) throws InternalMicroserviceException, StatementNotFoundException {
		RequestEntity<LoanOfferDto> requestEntity = getRequestEntityWithBody(loanOfferDto, uri);
		sendRequest(requestEntity);
	}
	
	public void sendFinishRegistrationRequest(FinishingRegistrationRequestDto finishingRegistrationRequestDto, URI uri) throws InternalMicroserviceException, StatementNotFoundException, LoanRefusalException, InvalidPreApproveException {
		RequestEntity<FinishingRegistrationRequestDto> requestEntity = getRequestEntityWithBody(finishingRegistrationRequestDto, uri);
		
		try {
			try {
				restTemplate.exchange(requestEntity, Void.class);
			}
			catch (HttpClientErrorException e) {
				if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
					ExceptionDetails exceptionDetails = objectMapper.readValue(e.getResponseBodyAsString(), ExceptionDetails.class);
					throw new StatementNotFoundException(exceptionDetails.getMessage());
				}
				else if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(406))) {
					ExceptionDetails exceptionDetails = objectMapper.readValue(e.getResponseBodyAsString(), ExceptionDetails.class);
					throw new LoanRefusalException(exceptionDetails.getMessage());
				}
				else if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(428))) {
					ExceptionDetails exceptionDetails = objectMapper.readValue(e.getResponseBodyAsString(), ExceptionDetails.class);
					throw new InvalidPreApproveException(exceptionDetails.getMessage());
				}
				else if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(500))) {
					ExceptionDetails exceptionDetails = objectMapper.readValue(e.getResponseBodyAsString(), ExceptionDetails.class);
					throw new InternalMicroserviceException(exceptionDetails.getMessage());
				}
			}
		}
		catch (RestClientException e) {
			throw new InternalMicroserviceException("Connection error to MS deal", e);
		}
		catch (JsonProcessingException e) {
			throw new InternalMicroserviceException("Can't deserialize value", e);
		}
	}
	
	public void sendOfferDenial(URI uri) throws StatementNotFoundException, InternalMicroserviceException {
		RequestEntity<Void> requestEntity = RequestEntity
				.get(uri)
				.build();
		
		sendRequest(requestEntity);
	}
	
	public void requestCreatingDocuments(URI uri) throws StatementNotFoundException, InternalMicroserviceException {
		RequestEntity<Void> requestEntity = getRequestEntity(uri);
		sendRequest(requestEntity);
	}
	
	public void requestSignatureOfDocuments(URI uri) throws StatementNotFoundException, InternalMicroserviceException {
		RequestEntity<Void> requestEntity = getRequestEntity(uri);
		sendRequest(requestEntity);
	}
	
	public void requestVerifyingSesCode(URI uri) throws StatementNotFoundException, InternalMicroserviceException, DocumentSigningException {
		RequestEntity<Void> requestEntity = getRequestEntity(uri);
		
		try {
			try {
				restTemplate.exchange(requestEntity, Void.class);
			}
			catch (HttpClientErrorException e) {
				if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
					ExceptionDetails exceptionDetails = objectMapper.readValue(e.getResponseBodyAsString(), ExceptionDetails.class);
					throw new StatementNotFoundException(exceptionDetails.getMessage());
				}
				else if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(406))) {
					ExceptionDetails exceptionDetails = objectMapper.readValue(e.getResponseBodyAsString(), ExceptionDetails.class);
					throw new DocumentSigningException(exceptionDetails.getMessage());
				}
				else if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(500))) {
					ExceptionDetails exceptionDetails = objectMapper.readValue(e.getResponseBodyAsString(), ExceptionDetails.class);
					throw new InternalMicroserviceException(exceptionDetails.getMessage());
				}
			}
		}
		catch (RestClientException e) {
			throw new InternalMicroserviceException("Connection error to MS deal", e);
		}
		catch (JsonProcessingException e) {
			throw new InternalMicroserviceException("Can't deserialize value", e);
		}
	}
	
	public void sendStatementStatus(ApplicationStatus status, URI uri) throws StatementNotFoundException, InternalMicroserviceException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		RequestEntity<ApplicationStatus> requestEntity = RequestEntity
				.put(uri)
				.headers(headers)
				.body(status);
		
		try {
			try {
				restTemplate.exchange(requestEntity, Void.class);
			}
			catch (HttpClientErrorException e) {
				if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
					ExceptionDetails exceptionDetails = objectMapper.readValue(e.getResponseBodyAsString(), ExceptionDetails.class);
					throw new StatementNotFoundException(exceptionDetails.getMessage());
				}
			}
		}
		catch (RestClientException e) {
			throw new InternalMicroserviceException("Connection error to MS deal", e);
		}
		catch (JsonProcessingException e) {
			throw new InternalMicroserviceException("Can't deserialize value", e);
		}
	}
	
	public Statement requestStatement(URI uri) throws StatementNotFoundException, InternalMicroserviceException {
		RequestEntity<Void> requestEntity = RequestEntity
				.get(uri)
				.build();
		
		ResponseEntity<String> responseEntity;
		Statement statement = null;
		try {
			try {
				responseEntity = restTemplate.exchange(requestEntity, String.class);
				statement = objectMapper.readValue(responseEntity.getBody(), Statement.class);
			}
			catch (HttpClientErrorException e) {
				if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
					ExceptionDetails exceptionDetails = objectMapper.readValue(e.getResponseBodyAsString(), ExceptionDetails.class);
					throw new StatementNotFoundException(exceptionDetails.getMessage());
				}
			}
		}
		catch (RestClientException e) {
			throw new InternalMicroserviceException("Connection error to MS deal", e);
		}
		catch (JsonProcessingException e) {
			throw new InternalMicroserviceException("Can't deserialize value", e);
		}
		return statement;
	}
	
	public List<Statement> requestAllStatements(URI uri) throws InternalMicroserviceException {
		RequestEntity<Void> requestEntity = RequestEntity
				.get(uri)
				.build();
		ParameterizedTypeReference<List<Statement>> responseType = new ParameterizedTypeReference<>() {};
		
		ResponseEntity<List<Statement>> responseEntity;
		try {
			responseEntity = restTemplate.exchange(requestEntity, responseType);
		}
		catch (RestClientException e) {
			throw new InternalMicroserviceException("Connection error to MS deal", e);
		}
		return responseEntity.getBody();
	}
	
	protected RequestEntity<Void> getRequestEntity(URI uri) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		return RequestEntity
				.post(uri)
				.headers(headers)
				.build();
	}
	
	private <T> RequestEntity<T> getRequestEntityWithBody(T t, URI uri) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		return RequestEntity
				.post(uri)
				.headers(headers)
				.body(t);
	}
	
	protected void sendRequest(RequestEntity<?> requestEntity) throws StatementNotFoundException, InternalMicroserviceException {
		
		try {
			try {
				restTemplate.exchange(requestEntity, Void.class);
			}
			catch (HttpClientErrorException e) {
				if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
					ExceptionDetails exceptionDetails = objectMapper.readValue(e.getResponseBodyAsString(), ExceptionDetails.class);
					throw new StatementNotFoundException(exceptionDetails.getMessage());
				}
				else if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(500))) {
					ExceptionDetails exceptionDetails = objectMapper.readValue(e.getResponseBodyAsString(), ExceptionDetails.class);
					throw new InternalMicroserviceException(exceptionDetails.getMessage());
				}
			}
		}
		catch (RestClientException e) {
			throw new InternalMicroserviceException("Connection error to MS", e);
		}
		catch (JsonProcessingException e) {
			throw new InternalMicroserviceException("Can't deserialize value", e);
		}
	}
}
