package ru.neoflex.neostudy.statement.requester;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.exception.*;
import ru.neoflex.neostudy.common.util.DtoInitializer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@SpringBootTest
class DealRequesterTest {
	
	@Autowired
	private DealRequester dealRequester;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ObjectMapper mapper;
	
	private MockRestServiceServer mockServer;
	private LoanStatementRequestDto loanStatementRequestDto;
	private List<LoanOfferDto> expectedOffers;
	private LoanOfferDto loanOfferDto;
	private static final String DEAL_OFFERS_URL = "http://localhost:8082/deal/statement";
	private static final String DEAL_APPLY_OFFER_URL = "http://localhost:8082/deal/offer/select";
	
	@Nested
	@DisplayName("Тестирование метода DealRequester:requestLoanOffers()")
	class TestingRequestLoanOffers {
		@BeforeEach
		void init() {
			mockServer = MockRestServiceServer.createServer(restTemplate);
			loanStatementRequestDto = DtoInitializer.initLoanStatementRequest();
			expectedOffers = DtoInitializer.initOffers();
		}
		
		@Test
		void requestLoanOffers_whenSendLoanStatementRequestDto_thenReceiveAndReturnLoanOffers() throws Exception {
			String expectedResponse = mapper.writeValueAsString(expectedOffers);
			
			mockServer.expect(ExpectedCount.once(),
							requestTo(new URI(DEAL_OFFERS_URL)))
					.andExpect(method(HttpMethod.POST))
					.andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));
			
			List<LoanOfferDto> actualOffers = dealRequester.requestLoanOffers(loanStatementRequestDto);
			assertThat(actualOffers).isEqualTo(expectedOffers);
		}
		
		@Test
		void requestLoanOffers_whenPassportDataIsInvalid_thenReceive400WithMessageAndThrowInvalidPassportDataException() throws Exception {
			int expectedStatus = 400;
			String expectedMessage = "Message";
			String expectedDetails = "Details";
			String expectedResponse = mapper.writeValueAsString(new ExceptionDetails(expectedStatus, expectedMessage, expectedDetails));
			
			mockServer.expect(ExpectedCount.once(),
							requestTo(new URI(DEAL_OFFERS_URL)))
					.andExpect(method(HttpMethod.POST))
					.andRespond(withBadRequest().body(expectedResponse));
			
			assertAll(() -> {
				Exception actualException = assertThrows(InvalidPassportDataException.class, () -> dealRequester.requestLoanOffers(loanStatementRequestDto));
				assertThat(actualException.getMessage()).isEqualTo(expectedMessage);
			});
		}
	}
	
	@Nested
	@DisplayName("Тестирование метода DealRequester:sendChosenOffer()")
	class TestingSendChosenOffer {
		@BeforeEach
		void init() {
			mockServer = MockRestServiceServer.createServer(restTemplate);
			loanOfferDto = DtoInitializer.initLoanOfferDto();
		}
		
		@Test
		void requestLoanOffers_whenSendLoanStatementRequestDto_thenReceiveAndReturnLoanOffers() throws Exception {
			mockServer.expect(ExpectedCount.once(),
							requestTo(new URI(DEAL_APPLY_OFFER_URL)))
					.andExpect(method(HttpMethod.POST))
					.andRespond(withSuccess());
			
			dealRequester.sendChosenOffer(loanOfferDto);
		}
		
		@Test
		void requestLoanOffers_whenStatementNotFound_thenReceive404WithMessageAndThrowStatementNotFoundException() throws Exception {
			int expectedStatus = 404;
			String expectedMessage = "Message";
			String expectedDetails = "Details";
			String expectedResponse = mapper.writeValueAsString(new ExceptionDetails(expectedStatus, expectedMessage, expectedDetails));
			
			mockServer.expect(ExpectedCount.once(),
							requestTo(new URI(DEAL_APPLY_OFFER_URL)))
					.andExpect(method(HttpMethod.POST))
					.andRespond(withResourceNotFound().body(expectedResponse));
			
			assertAll(() -> {
				Exception actualException = assertThrows(StatementNotFoundException.class, () -> dealRequester.sendChosenOffer(loanOfferDto));
				assertThat(actualException.getMessage()).isEqualTo(expectedMessage);
			});
		}
	}
}