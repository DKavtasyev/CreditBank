package ru.neoflex.neostudy.statement.requester;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.util.DtoInitializer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DealRequestServiceTest {
	
	@Mock
	private Requester requester;
	
	@InjectMocks
	private DealRequestService dealRequestService;
	
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
			ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.NOT_FOUND).body(expectedOffers);
			when(requester.request(any(LoanStatementRequestDto.class), any(), anyString())).thenReturn(response);
			List<LoanOfferDto> actualOffers = dealRequestService.requestLoanOffers(loanStatementRequestDto);
			assertThat(actualOffers).isSameAs(expectedOffers);
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
		void requestLoanOffers_whenSendLoanStatementRequestDto_thenReceiveAndReturnLoanOffers() throws Exception {
			dealRequestService.sendChosenOffer(loanOfferDto);
			verify(requester, times(1)).request(any(), any(), anyString());
		}
	}
}