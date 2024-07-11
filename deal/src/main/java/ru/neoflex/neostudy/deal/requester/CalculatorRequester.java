package ru.neoflex.neostudy.deal.requester;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.neoflex.neostudy.common.dto.CreditDto;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.dto.ScoringDataDto;
import ru.neoflex.neostudy.common.exception.ExceptionDetails;
import ru.neoflex.neostudy.common.exception.LoanRefusalException;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalculatorRequester {
	private static final String OFFERS_URL = "http://localhost:8081/calculator/offers";
	private static final String CREDIT_URL = "http://localhost:8081/calculator/calc";
	
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;
	
	public List<LoanOfferDto> requestLoanOffers(LoanStatementRequestDto loanStatementRequestDto) {
		ParameterizedTypeReference<List<LoanOfferDto>> responseType = new ParameterizedTypeReference<>() {};
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		RequestEntity<LoanStatementRequestDto> requestEntity = RequestEntity
				.post(URI.create(OFFERS_URL))
				.headers(headers)
				.body(loanStatementRequestDto);
		
		ResponseEntity<List<LoanOfferDto>> responseEntity = restTemplate.exchange(requestEntity, responseType);
		return responseEntity.getBody();
	}
	
	public CreditDto requestCalculatedLoanTerms(ScoringDataDto scoringDataDto) throws LoanRefusalException, JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		RequestEntity<ScoringDataDto> requestEntity = RequestEntity
				.post(URI.create(CREDIT_URL))
				.headers(headers)
				.body(scoringDataDto);
		
		ResponseEntity<String> responseEntity;
		CreditDto creditDto = null;
		try {
			responseEntity = restTemplate.exchange(requestEntity, String.class);
			creditDto = objectMapper.readValue(responseEntity.getBody(), CreditDto.class);
		}
		catch (HttpClientErrorException e) {
			if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(406))){
				ExceptionDetails exceptionDetails = objectMapper.readValue(e.getResponseBodyAsString(), ExceptionDetails.class);
				throw new LoanRefusalException(exceptionDetails.getMessage());
			}
		}
		return creditDto;
	}
}
