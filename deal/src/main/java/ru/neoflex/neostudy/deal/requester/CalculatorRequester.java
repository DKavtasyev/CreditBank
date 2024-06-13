package ru.neoflex.neostudy.deal.requester;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.neoflex.neostudy.common.dto.CreditDto;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.dto.ScoringDataDto;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalculatorRequester
{
	public static final String OFFERS_URL = "http://localhost:8081/calculator/offers";
	public static final String CREDIT_URL = "http://localhost:8081/calculator/calc";
	
	private final RestTemplate restTemplate;
	
	public List<LoanOfferDto> requestLoanOffers(LoanStatementRequestDto loanStatementRequestDto)
	{
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
	
	public CreditDto requestCalculatedLoanTerms(ScoringDataDto scoringDataDto)
	{
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		RequestEntity<ScoringDataDto> requestEntity = RequestEntity
				.post(URI.create(CREDIT_URL))
				.headers(headers)
				.body(scoringDataDto);
		
		ResponseEntity<CreditDto> responseEntity = restTemplate.exchange(requestEntity, CreditDto.class);
		return responseEntity.getBody();
	}
}
