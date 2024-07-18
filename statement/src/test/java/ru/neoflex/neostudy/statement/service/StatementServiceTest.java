package ru.neoflex.neostudy.statement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.InvalidPassportDataException;
import ru.neoflex.neostudy.common.exception.InvalidPreScoreParametersException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.common.util.DtoInitializer;
import ru.neoflex.neostudy.statement.requester.DealRequester;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatementServiceTest {
	
	@Mock
	private DealRequester dealRequester;
	
	@InjectMocks
	StatementService statementService;
	
	private LoanStatementRequestDto loanStatementRequestDto;
	private LoanOfferDto loanOfferDto;
	
	@Nested
	@DisplayName("Тестирование метода StatementService:getLoanOffers()")
	class TestingGetLoanOffersMethod {
		
		@BeforeEach
		void init() {
			loanStatementRequestDto = DtoInitializer.initLoanStatementRequest();
		}
		
		@Test
		void getLoanOffers_whenGivenLoanStatementRequestDto_thenReturnListOfLoanOffersDto() throws Exception {
			List<LoanOfferDto> expectedOffers = DtoInitializer.initOffers();
			when(dealRequester.requestLoanOffers(loanStatementRequestDto)).thenReturn(expectedOffers);
			List<LoanOfferDto> actualOffers = statementService.getLoanOffers(loanStatementRequestDto);
			assertThat(actualOffers).isSameAs(expectedOffers);
		}
	}
	
	@Nested
	@DisplayName("Тестирование метода StatementService:applyChosenOffer()")
	class TestingApplyChosenOfferMethod {
		
		@BeforeEach
		void init() {
			loanOfferDto = DtoInitializer.initLoanOfferDto();
		}
		
		@Test
		void getLoanOffers_whenGivenLoanStatementRequestDto_thenReturnListOfLoanOffersDto() throws Exception {
			statementService.applyChosenOffer(loanOfferDto);
			verify(dealRequester, times(1)).sendChosenOffer(loanOfferDto);
		}
	}
}


