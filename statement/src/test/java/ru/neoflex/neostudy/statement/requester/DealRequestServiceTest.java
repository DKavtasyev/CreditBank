package ru.neoflex.neostudy.statement.requester;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.InvalidPassportDataException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.common.exception.dto.ExceptionDetails;
import ru.neoflex.neostudy.common.util.DtoInitializer;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class DealRequestServiceTest {
	
	@Autowired
	private DealRequestService dealRequestService;
	@MockBean
	private Requester requester;
	@Autowired
	private ObjectMapper objectMapper;
	
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
			loanStatementRequestDto = DtoInitializer.initLoanStatementRequest();
			expectedOffers = DtoInitializer.initOffers();
		}
		
		@Test
		void requestLoanOffers_whenLoanStatementRequestDtoIsGiven_thenReturnLoanOffers() throws Exception {
			RequestEntity<LoanStatementRequestDto> requestEntity = RequestEntity
					.post("")
					.body(loanStatementRequestDto);
			ParameterizedTypeReference<List<LoanOfferDto>> responseType = new ParameterizedTypeReference<>() {};
			
			ResponseEntity<List<LoanOfferDto>> response = ResponseEntity.status(HttpStatus.NOT_FOUND).body(expectedOffers);
			when(requester.getRequestEntity(loanStatementRequestDto, URI.create(DEAL_OFFERS_URL))).thenReturn(requestEntity);
			when(requester.request(requestEntity, responseType)).thenReturn(response);
			List<LoanOfferDto> actualOffers = dealRequestService.requestLoanOffers(loanStatementRequestDto);
			assertThat(actualOffers).isSameAs(expectedOffers);
		}
		
		@Test
		void requestLoanOffers_whenStatus400Received_thenThrowInvalidPassportDataException() throws IOException {
			RequestEntity<LoanStatementRequestDto> requestEntity = RequestEntity
					.post("")
					.body(loanStatementRequestDto);
			ParameterizedTypeReference<List<LoanOfferDto>> responseType = new ParameterizedTypeReference<>() {};
			when(requester.getRequestEntity(loanStatementRequestDto, URI.create(DEAL_OFFERS_URL))).thenReturn(requestEntity);
			
			ExceptionDetails exceptionDetails = new ExceptionDetails(400, "Bad Request", "details");
			
			byte[] body = objectMapper.writeValueAsString(exceptionDetails).getBytes();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpClientErrorException e = HttpClientErrorException.create(
					HttpStatus.BAD_REQUEST,
					HttpStatus.BAD_REQUEST.getReasonPhrase(),
					headers,
					body,
					StandardCharsets.UTF_8
			);
			HttpClientErrorException spyException = spy(e);
			doReturn(exceptionDetails).when(spyException).getResponseBodyAs(ExceptionDetails.class);
			when(requester.request(requestEntity, responseType)).thenThrow(spyException);
			
			Assertions.assertThatThrownBy(() -> dealRequestService.requestLoanOffers(loanStatementRequestDto)).isInstanceOf(InvalidPassportDataException.class);
		}
		
		@Test
		void requestLoanOffers_whenStatus500Received_thenThrowInternalMicroserviceException() throws IOException {
			RequestEntity<LoanStatementRequestDto> requestEntity = RequestEntity
					.post("")
					.body(loanStatementRequestDto);
			ParameterizedTypeReference<List<LoanOfferDto>> responseType = new ParameterizedTypeReference<>() {};
			when(requester.getRequestEntity(loanStatementRequestDto, URI.create(DEAL_OFFERS_URL))).thenReturn(requestEntity);
			
			ExceptionDetails exceptionDetails = new ExceptionDetails(500, "Internal Server Error", "details");
			
			byte[] body = objectMapper.writeValueAsString(exceptionDetails).getBytes();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpClientErrorException e = HttpClientErrorException.create(
					HttpStatus.INTERNAL_SERVER_ERROR,
					HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
					headers,
					body,
					StandardCharsets.UTF_8
			);
			HttpClientErrorException spyException = spy(e);
			doReturn(exceptionDetails).when(spyException).getResponseBodyAs(ExceptionDetails.class);
			when(requester.request(requestEntity, responseType)).thenThrow(spyException);
			
			Assertions.assertThatThrownBy(() -> dealRequestService.requestLoanOffers(loanStatementRequestDto)).isInstanceOf(InternalMicroserviceException.class);
		}
		
		@Test
		void requestLoanOffers_whenResponseBodyIsCorrupted_thenThrowInternalMicroserviceException() throws IOException {
			RequestEntity<LoanStatementRequestDto> requestEntity = RequestEntity
					.post("")
					.body(loanStatementRequestDto);
			ParameterizedTypeReference<List<LoanOfferDto>> responseType = new ParameterizedTypeReference<>() {};
			when(requester.getRequestEntity(loanStatementRequestDto, URI.create(DEAL_OFFERS_URL))).thenReturn(requestEntity);
			
			ExceptionDetails exceptionDetails = new ExceptionDetails(500, "Internal Server Error", "details");
			
			byte[] body = objectMapper.writeValueAsString(exceptionDetails).getBytes();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpClientErrorException e = HttpClientErrorException.create(
					HttpStatus.INTERNAL_SERVER_ERROR,
					HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
					headers,
					body,
					StandardCharsets.UTF_8
			);
			HttpClientErrorException spyException = spy(e);
			doThrow(RestClientException.class).when(spyException).getResponseBodyAs(ExceptionDetails.class);
			when(requester.request(requestEntity, responseType)).thenThrow(spyException);
			
			Assertions.assertThatThrownBy(() -> dealRequestService.requestLoanOffers(loanStatementRequestDto)).isInstanceOf(InternalMicroserviceException.class);
		}
	}
	
	@Nested
	@DisplayName("Тестирование метода DealRequester:sendChosenOffer()")
	class TestingSendChosenOffer {
		
		@BeforeEach
		void init() {
			loanOfferDto = DtoInitializer.initLoanOfferDto();
		}
		
		@Test
		void sendChosenOffer_whenSendLoanOfferDto_thenSuccess() throws Exception {
			RequestEntity<LoanOfferDto> requestEntity = RequestEntity
					.post("")
					.body(loanOfferDto);
			
			when(requester.getRequestEntity(loanOfferDto, URI.create(DEAL_APPLY_OFFER_URL))).thenReturn(requestEntity);
			
			dealRequestService.sendChosenOffer(loanOfferDto);
			verify(requester, times(1)).request(any(), any());
		}
		
		@Nested
		@DisplayName("Тестирование обработки исключений")
		class TestingExceptions {
			
			RequestEntity<LoanOfferDto> requestEntity;
			ParameterizedTypeReference<Void> responseType;
			ExceptionDetails exceptionDetails;
			
			@BeforeEach
			void init() {
				requestEntity = RequestEntity
						.post("")
						.body(loanOfferDto);
				responseType = new ParameterizedTypeReference<>() {};
				exceptionDetails = new ExceptionDetails(500, "Error Message", "details");
			}
			
			@Test
			void requestLoanOffers_whenStatus404Received_thenThrowStatementNotFoundException() throws IOException {
				when(requester.getRequestEntity(loanOfferDto, URI.create(DEAL_APPLY_OFFER_URL))).thenReturn(requestEntity);
				
				byte[] body = objectMapper.writeValueAsString(exceptionDetails).getBytes();
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				
				HttpClientErrorException e = HttpClientErrorException.create(
						HttpStatus.NOT_FOUND,
						HttpStatus.NOT_FOUND.getReasonPhrase(),
						headers,
						body,
						StandardCharsets.UTF_8
				);
				HttpClientErrorException spyException = spy(e);
				doReturn(exceptionDetails).when(spyException).getResponseBodyAs(ExceptionDetails.class);
				when(requester.request(requestEntity, responseType)).thenThrow(spyException);
				
				Assertions.assertThatThrownBy(() -> dealRequestService.sendChosenOffer(loanOfferDto)).isInstanceOf(StatementNotFoundException.class);
			}
			
			@Test
			void requestLoanOffers_whenStatus500Received_thenThrowInternalMicroserviceException() throws IOException {
				when(requester.getRequestEntity(loanOfferDto, URI.create(DEAL_APPLY_OFFER_URL))).thenReturn(requestEntity);
				
				byte[] body = objectMapper.writeValueAsString(exceptionDetails).getBytes();
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				
				HttpClientErrorException e = HttpClientErrorException.create(
						HttpStatus.INTERNAL_SERVER_ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
						headers,
						body,
						StandardCharsets.UTF_8
				);
				HttpClientErrorException spyException = spy(e);
				doReturn(exceptionDetails).when(spyException).getResponseBodyAs(ExceptionDetails.class);
				when(requester.request(requestEntity, responseType)).thenThrow(spyException);
				
				Assertions.assertThatThrownBy(() -> dealRequestService.sendChosenOffer(loanOfferDto)).isInstanceOf(InternalMicroserviceException.class);
			}
			
			@Test
			void requestLoanOffers_whenResponseBodyIsCorrupted_thenThrowInternalMicroserviceException() throws IOException {
				when(requester.getRequestEntity(loanOfferDto, URI.create(DEAL_APPLY_OFFER_URL))).thenReturn(requestEntity);
				
				byte[] body = objectMapper.writeValueAsString(exceptionDetails).getBytes();
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				
				HttpClientErrorException e = HttpClientErrorException.create(
						HttpStatus.INTERNAL_SERVER_ERROR,
						HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
						headers,
						body,
						StandardCharsets.UTF_8
				);
				HttpClientErrorException spyException = spy(e);
				doThrow(RestClientException.class).when(spyException).getResponseBodyAs(ExceptionDetails.class);
				when(requester.request(requestEntity, responseType)).thenThrow(spyException);
				
				Assertions.assertThatThrownBy(() -> dealRequestService.sendChosenOffer(loanOfferDto)).isInstanceOf(InternalMicroserviceException.class);
			}
		}
	}
}