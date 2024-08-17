package ru.neoflex.neostudy.statement.requester;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.util.DtoInitializer;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
class RequesterTest {

	@Autowired
	private Requester requester;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	private ObjectMapper mapper;
	
	private MockRestServiceServer mockServer;
	private LoanStatementRequestDto loanStatementRequestDto;
	private List<LoanOfferDto> expectedOffers;
	private LoanOfferDto loanOfferDto;
	private static final String DEAL_OFFERS_URL = "http://localhost:8082/deal/statement";
	private static final String DEAL_APPLY_OFFER_URL = "http://localhost:8082/deal/offer/select";
	
	@Nested
	@DisplayName("Тестирование метода Requester:request()")
	class TestingRequestLoanOffers {
		
		@BeforeEach
		void init() {
			mockServer = MockRestServiceServer.createServer(restTemplate);
			loanStatementRequestDto = DtoInitializer.initLoanStatementRequest();
			loanOfferDto = DtoInitializer.initLoanOfferDto();
			expectedOffers = DtoInitializer.initOffers();
		}
		
		
		@Test
		void request_whenRequestLoanOfferDto_thenReturn200() throws Exception {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			RequestEntity<LoanOfferDto> requestEntity = RequestEntity
					.post(DEAL_APPLY_OFFER_URL)
					.headers(headers)
					.body(loanOfferDto);
			
			ParameterizedTypeReference<List<LoanOfferDto>> responseType = new ParameterizedTypeReference<>() {};
			
			mockServer.expect(ExpectedCount.once(),
							requestTo(new URI(DEAL_APPLY_OFFER_URL)))
					.andExpect(method(HttpMethod.POST))
					.andRespond(withSuccess());
			
			HttpStatusCode statusCode = requester.request(requestEntity, responseType).getStatusCode();
			assertThat(statusCode).isEqualTo(HttpStatusCode.valueOf(200));
		}
		
		@Test
		void request() throws Exception {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			RequestEntity<LoanStatementRequestDto> requestEntity = RequestEntity
					.post(DEAL_OFFERS_URL)
					.headers(headers)
					.body(loanStatementRequestDto);
			
			ParameterizedTypeReference<List<LoanOfferDto>> responseType = new ParameterizedTypeReference<>() {};
			String expectedResponse = mapper.writeValueAsString(expectedOffers);
			
			mockServer.expect(ExpectedCount.once(),
							requestTo(new URI(DEAL_OFFERS_URL)))
					.andExpect(method(HttpMethod.POST))
					.andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));
			
			List<LoanOfferDto> actualOffers = requester.request(requestEntity, responseType).getBody();
			assertThat(actualOffers).isEqualTo(expectedOffers);
		}
		
	}
	
	@Nested
	@DisplayName("Тестирование метода Requester:getRequestEntity()")
	class TestingGetRequestEntityMethod {
		
		@Test
		void getRequestEntity_whenValidInput_thenReturnBodyOfResponse() {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			RequestEntity<LoanStatementRequestDto> expectedRequestEntity = RequestEntity
					.post(DEAL_OFFERS_URL)
					.headers(headers)
					.body(loanStatementRequestDto);
			
			RequestEntity<LoanStatementRequestDto> actualRequestEntity = requester.getRequestEntity(loanStatementRequestDto, DEAL_OFFERS_URL);
			assertThat(actualRequestEntity).isEqualTo(expectedRequestEntity);
		}
	}
}



