package ru.neoflex.neostudy.statement.requester;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.exception.ExceptionDetails;
import ru.neoflex.neostudy.common.exception.InvalidPassportDataException;
import ru.neoflex.neostudy.common.exception.InvalidPreScoreParametersException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DealRequester {
	private static final String DEAL_OFFERS_URL = "http://localhost:8082/deal/statement";
	private static final String DEAL_APPLY_OFFER_URL = "http://localhost:8082/deal/offer/select";
	
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;
	
	public List<LoanOfferDto> requestLoanOffers(LoanStatementRequestDto loanStatementRequestDto) throws InvalidPassportDataException, JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		RequestEntity<LoanStatementRequestDto> requestEntity = RequestEntity
				.post(URI.create(DEAL_OFFERS_URL))
				.headers(headers)
				.body(loanStatementRequestDto);
		
		ResponseEntity<String> responseEntity;
		List<LoanOfferDto> offers = new ArrayList<>();
		try {
			responseEntity = restTemplate.exchange(requestEntity, String.class);
			offers = objectMapper.readValue(responseEntity.getBody(), objectMapper.getTypeFactory().constructCollectionType(List.class, LoanOfferDto.class));
		}
		catch (HttpClientErrorException e) {
			if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(400))){
				ExceptionDetails exceptionDetails = objectMapper.readValue(e.getResponseBodyAsString(), ExceptionDetails.class);
				throw new InvalidPassportDataException(exceptionDetails.getMessage());
			}
		}
		return offers;
	}
	
	public void sendChosenOffer(LoanOfferDto loanOfferDto) throws StatementNotFoundException, JsonProcessingException, InvalidPreScoreParametersException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		RequestEntity<LoanOfferDto> requestEntity = RequestEntity
				.post(DEAL_APPLY_OFFER_URL)
				.headers(headers)
				.body(loanOfferDto);
		
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
