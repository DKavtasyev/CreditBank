package ru.neoflex.neostudy.calculator.service;

import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.neoflex.neostudy.common.dto.*;
import ru.neoflex.neostudy.common.util.DtoInitializer;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class CalculatorServiceTest {
	
	@Autowired
	private CalculatorService service;
	@MockBean
	private MonthlyPaymentCalculatorService monthlyPaymentCalculatorService;
	@MockBean
	private PersonalRateCalculatorService personalRateCalculatorService;
	@MockBean
	private SchedulePaymentsCalculatorService schedulePaymentsCalculatorService;
	
	
	private ScoringDataDto scoringData;
	private static LoanStatementRequestDto loanStatementRequest;
	private static final BigDecimal RATE = new BigDecimal("0.12");
	
	@Nested
	@DisplayName("Тестирование метода CalculatorService:preScore()")
	class TestingPreScore {
		@BeforeAll
		static void initLoanStatementRequest() {
			loanStatementRequest = DtoInitializer.initLoanStatementRequest();
		}
		
		@Test
		void preScore_whenLoanStatementRequestReceived_thenReturnFourLoanOffers() {
			List<LoanOfferDto> expectedOffers = DtoInitializer.initOffers();
			when(monthlyPaymentCalculatorService.calculate(any(BigDecimal.class), anyInt(), any(BigDecimal.class))).thenReturn(
					new BigDecimal("172548.3667108814202625"),
					new BigDecimal("172054.5475593009795123"),
					new BigDecimal("179622.3528220476265425"),
					new BigDecimal("179105.9426200903968720"));
			List<LoanOfferDto> actualOffers = service.preScore(loanStatementRequest);
			ArgumentCaptor<BigDecimal> bigDecimalCaptor = ArgumentCaptor.forClass(BigDecimal.class);
			ArgumentCaptor<Integer> integerCaptor = ArgumentCaptor.forClass(Integer.class);
			ArgumentCaptor<BigDecimal> bigDecimalRateCaptor = ArgumentCaptor.forClass(BigDecimal.class);
			assertAll(() -> {
				verify(monthlyPaymentCalculatorService, times(4)).calculate(bigDecimalCaptor.capture(), integerCaptor.capture(), bigDecimalRateCaptor.capture());
				assertThat(actualOffers.size()).isEqualTo(4);
				assertThat(actualOffers).isEqualTo(expectedOffers);
				assertThat(actualOffers.get(0).equals(expectedOffers.get(0))).isTrue();
			});
		}
	}
	
	@Nested
	@DisplayName("Тестирование метода CalculatorService:score()")
	class TestingScore {
		@BeforeEach
		void initScoringData() {
			scoringData = DtoInitializer.initScoringData();
		}
		
		@Test
		void score_whenValidScoringDataReceived_thenReturnCreditDto() throws Exception {
			CreditDto expectedCredit = DtoInitializer.initCreditDto();
			when(personalRateCalculatorService.countPersonalRate(scoringData, RATE)).thenReturn(RATE);
			when(monthlyPaymentCalculatorService.calculate(scoringData.getAmount(), scoringData.getTerm(), RATE)).thenReturn(new BigDecimal("172548.3667108814202625"));
			when(schedulePaymentsCalculatorService.countInterestPayment(any(BigDecimal.class), any(BigDecimal.class), anyInt())).thenReturn(new BigDecimal("9863.0136986310000000"));
			
			ArgumentCaptor<PaymentScheduleElementDto> paymentScheduleCaptor = ArgumentCaptor.forClass(PaymentScheduleElementDto.class);
			ArgumentCaptor<BigDecimal> dailyRateCaptor = ArgumentCaptor.forClass(BigDecimal.class);
			
			CreditDto actualCredit = service.score(scoringData);
			assertAll(() -> {
				assertThat(actualCredit.getAmount().toString()).isEqualTo(expectedCredit.getAmount().toString());
				assertThat(actualCredit.getTerm()).isEqualTo(expectedCredit.getTerm());
				assertThat(actualCredit.getMonthlyPayment().toString()).isEqualTo(expectedCredit.getMonthlyPayment().toString());
				assertThat(actualCredit.getRate().toString()).isEqualTo(expectedCredit.getRate().toString());
				assertThat(actualCredit.getIsInsuranceEnabled()).isEqualTo(expectedCredit.getIsInsuranceEnabled());
				assertThat(actualCredit.getIsSalaryClient()).isEqualTo(expectedCredit.getIsSalaryClient());
				assertThat(actualCredit.getPaymentSchedule().get(0).equals(expectedCredit.getPaymentSchedule().get(0))).isTrue();
				verify(schedulePaymentsCalculatorService, times(1)).countPayment(paymentScheduleCaptor.capture(), anyList(), dailyRateCaptor.capture());
			});
		}
	}
}
