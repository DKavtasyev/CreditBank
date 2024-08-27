package ru.neoflex.neostudy.deal.requester;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.neoflex.neostudy.common.dto.CreditDto;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.dto.ScoringDataDto;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.LoanRefusalException;
import ru.neoflex.neostudy.common.exception.dto.ExceptionDetails;
import ru.neoflex.neostudy.common.util.DtoInitializer;
import ru.neoflex.neostudy.deal.controller.AdminController;
import ru.neoflex.neostudy.deal.controller.DealController;
import ru.neoflex.neostudy.deal.repository.ClientRepository;
import ru.neoflex.neostudy.deal.repository.StatementRepository;
import ru.neoflex.neostudy.deal.service.DataService;
import ru.neoflex.neostudy.deal.service.ScoringService;
import ru.neoflex.neostudy.deal.service.entity.ClientEntityService;
import ru.neoflex.neostudy.deal.service.kafka.KafkaService;
import ru.neoflex.neostudy.deal.service.kafka.MessageSenderKafka;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@SpringBootTest
@ActiveProfiles("test")
@MockBean(classes = {
		DealController.class, ScoringService.class, KafkaService.class,
		MessageSenderKafka.class, AdminController.class, DataService.class,
		ClientEntityService.class, StatementRepository.class, ClientRepository.class})
class CalculatorRequesterTest {
	
	@Autowired
	private CalculatorRequester calculatorRequester;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private ObjectMapper mapper;
	
	private MockRestServiceServer mockServer;
	private LoanStatementRequestDto loanStatementRequestDto;
	private List<LoanOfferDto> expectedOffers;
	private ScoringDataDto scoringDataDto;
	private CreditDto expectedCreditDto;
	private static final String CALCULATOR_OFFERS_URL = "http://localhost:8081/calculator/offers";
	private static final String CALCULATOR_CREDIT = "http://localhost:8081/calculator/calc";
	
	@Nested
	@DisplayName("Тестирование метода CalculatorRequester:requestLoanOffers()")
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
							requestTo(URI.create(CALCULATOR_OFFERS_URL)))
					.andExpect(header(HttpHeaders.CONTENT_TYPE, "application/json"))
					.andExpect(method(HttpMethod.POST))
					.andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));
			
			List<LoanOfferDto> actualOffers = calculatorRequester.requestLoanOffers(loanStatementRequestDto);
			assertThat(actualOffers).isEqualTo(expectedOffers);
		}
		
		@Test
		void requestLoanOffers_whenPassportDataIsInvalid_thenReceive400WithMessageAndThrowInvalidPassportDataException() throws Exception {
			int expectedStatus = 500;
			String expectedMessage = "Message";
			String expectedDetails = "Details";
			String expectedResponse = mapper.writeValueAsString(new ExceptionDetails(expectedStatus, expectedMessage, expectedDetails));
			
			mockServer.expect(ExpectedCount.once(),
							requestTo(URI.create(CALCULATOR_OFFERS_URL)))
					.andExpect(method(HttpMethod.POST))
					.andRespond(withServerError()
							.contentType(MediaType.APPLICATION_JSON)
							.body(expectedResponse));
			
			assertAll(() -> {
				Exception actualException = assertThrows(InternalMicroserviceException.class, () -> calculatorRequester.requestLoanOffers(loanStatementRequestDto));
				assertThat(actualException.getMessage()).isEqualTo(expectedMessage);
			});
		}
	}
	
	@Nested
	@DisplayName("Тестирование метода CalculatorRequester:requestCalculatedCredit()")
	class TestingSendChosenOffer {
		@BeforeEach
		void init() {
			mockServer = MockRestServiceServer.createServer(restTemplate);
			scoringDataDto = DtoInitializer.initScoringData();
			expectedCreditDto = DtoInitializer.initCreditDto();
		}
		
		@Test
		void requestCalculatedCredit_whenSendScoringDataDto_thenReceiveAndReturnCreditDto() throws Exception {
			String expectedResponse = mapper.writeValueAsString(expectedCreditDto);
			mockServer.expect(ExpectedCount.once(),
							requestTo(new URI(CALCULATOR_CREDIT)))
					.andExpect(method(HttpMethod.POST))
					.andRespond(withSuccess()
							.contentType(MediaType.APPLICATION_JSON)
							.body(expectedResponse));
			
			CreditDto actualCreditDto = calculatorRequester.requestCalculatedCredit(scoringDataDto);
			assertThat(actualCreditDto).isEqualTo(expectedCreditDto);
		}
		
		@Test
		void requestCalculatedCredit_whenLoanWasRefused_thenReceive406WithMessageAndThrowLoanRefusalException() throws Exception {
			int expectedStatus = 406;
			String expectedMessage = "Message";
			String expectedDetails = "Details";
			String expectedResponse = mapper.writeValueAsString(new ExceptionDetails(expectedStatus, expectedMessage, expectedDetails));
			
			mockServer.expect(ExpectedCount.once(),
							requestTo(new URI(CALCULATOR_CREDIT)))
					.andExpect(method(HttpMethod.POST))
					.andRespond(withStatus(HttpStatus.NOT_ACCEPTABLE)
							.contentType(MediaType.APPLICATION_JSON)
							.body(expectedResponse));
			
			assertAll(() -> {
				Exception actualException = assertThrows(LoanRefusalException.class, () -> calculatorRequester.requestCalculatedCredit(scoringDataDto));
				assertThat(actualException.getMessage()).isEqualTo(expectedMessage);
			});
		}
		
		@Test
		void requestCalculatedCredit_whenStatementNotFound_thenReceive404WithMessageAndThrowStatementNotFoundException() throws Exception {
			int expectedStatus = 500;
			String expectedMessage = "Message";
			String expectedDetails = "Details";
			String expectedResponse = mapper.writeValueAsString(new ExceptionDetails(expectedStatus, expectedMessage, expectedDetails));
			
			mockServer.expect(ExpectedCount.once(),
							requestTo(new URI(CALCULATOR_CREDIT)))
					.andExpect(method(HttpMethod.POST))
					.andRespond(withServerError()
							.contentType(MediaType.APPLICATION_JSON)
							.body(expectedResponse));
			
			assertAll(() -> {
				Exception actualException = assertThrows(InternalMicroserviceException.class, () -> calculatorRequester.requestCalculatedCredit(scoringDataDto));
				assertThat(actualException.getMessage()).isEqualTo(expectedMessage);
			});
		}
	}
}