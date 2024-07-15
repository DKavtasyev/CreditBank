package ru.neoflex.neostudy.deal.service;

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
import ru.neoflex.neostudy.common.util.DtoInitializer;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.requester.CalculatorRequester;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PreScoringServiceTest {
	@Mock
	CalculatorRequester calculatorRequester;
	
	@InjectMocks
	PreScoringService preScoringService;
	
	LoanStatementRequestDto loanStatementRequestDto;
	List<LoanOfferDto> offers;
	Statement statement;
	
	@Nested
	@DisplayName("Тестирование метода PreScoringService:getOffers()")
	class TestingGetOffersMethod {
		@BeforeEach
		void init() {
			offers = DtoInitializer.initOffers().stream().sorted((s1, s2) -> s2.getTotalAmount().compareTo(s1.getTotalAmount())).toList();
			statement = new Statement();
		}
		
		@Test
		void getOffers() throws InternalMicroserviceException {
			UUID statementId = UUID.randomUUID();
			statement.setStatementId(statementId);
			when(calculatorRequester.requestLoanOffers(loanStatementRequestDto)).thenReturn(offers);
			List<LoanOfferDto> actualOffers = preScoringService.getOffers(loanStatementRequestDto, statement);
			assertAll(() -> {
				verify(calculatorRequester, times(1)).requestLoanOffers(loanStatementRequestDto);
				assertThat(actualOffers).isEqualTo(offers);
				assertThat(offers.stream().allMatch(offer -> offer.getStatementId().equals(statementId))).isTrue();
			});
		}
	}
}