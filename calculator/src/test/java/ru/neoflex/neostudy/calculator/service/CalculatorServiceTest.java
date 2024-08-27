package ru.neoflex.neostudy.calculator.service;

import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.neoflex.neostudy.common.dto.*;
import ru.neoflex.neostudy.common.util.DtoInitializer;

import java.math.BigDecimal;
import java.time.LocalDate;
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
	
	@Value("${rate.base-rate}")
	public BigDecimal RATE;
	
	private ScoringDataDto scoringData;
	private static LoanStatementRequestDto loanStatementRequest;
	
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
					DtoInitializer.OFFER_0_MONTHLY_PAYMENT,
					DtoInitializer.OFFER_1_MONTHLY_PAYMENT,
					DtoInitializer.OFFER_2_MONTHLY_PAYMENT,
					DtoInitializer.OFFER_3_MONTHLY_PAYMENT);
			List<LoanOfferDto> actualOffers = service.preScore(loanStatementRequest);
			ArgumentCaptor<BigDecimal> bigDecimalCaptor = ArgumentCaptor.forClass(BigDecimal.class);
			ArgumentCaptor<Integer> integerCaptor = ArgumentCaptor.forClass(Integer.class);
			ArgumentCaptor<BigDecimal> bigDecimalRateCaptor = ArgumentCaptor.forClass(BigDecimal.class);
			assertAll(() -> {
				verify(monthlyPaymentCalculatorService, times(4)).calculate(bigDecimalCaptor.capture(), integerCaptor.capture(), bigDecimalRateCaptor.capture());
				assertThat(actualOffers).hasSize(4);
				assertThat(actualOffers).isEqualTo(expectedOffers);
				assertThat(actualOffers.getFirst()).isEqualTo(expectedOffers.getFirst());
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
			BigDecimal dailyRate = new BigDecimal("0.0003287671232877");
			PaymentScheduleElementDto expectedFirstPaymentScheduleElement = expectedCredit.getPaymentSchedule().getFirst();
			
			when(personalRateCalculatorService.countPersonalRate(scoringData, RATE, DtoInitializer.AGE)).thenReturn(RATE);
			when(personalRateCalculatorService.calculateDailyRate(RATE)).thenReturn(dailyRate);
			when(monthlyPaymentCalculatorService.calculate(scoringData.getAmount(), scoringData.getTerm(), RATE)).thenReturn(DtoInitializer.OFFER_0_MONTHLY_PAYMENT);
			when(schedulePaymentsCalculatorService.calculatePaymentScheduleElement(1, scoringData.getAmount(), dailyRate, DtoInitializer.OFFER_0_MONTHLY_PAYMENT, LocalDate.now())).thenReturn(expectedFirstPaymentScheduleElement);
			
			ArgumentCaptor<PaymentScheduleElementDto> paymentScheduleCaptor = ArgumentCaptor.forClass(PaymentScheduleElementDto.class);
			ArgumentCaptor<BigDecimal> dailyRateCaptor = ArgumentCaptor.forClass(BigDecimal.class);
			
			CreditDto actualCredit = service.score(scoringData);
			actualCredit.getPaymentSchedule().getFirst().setDate(DtoInitializer.DATE.plusMonths(1));
			assertAll(() -> {
				assertThat(actualCredit.getAmount()).isEqualTo(expectedCredit.getAmount());
				assertThat(actualCredit.getTerm()).isEqualTo(expectedCredit.getTerm());
				assertThat(actualCredit.getMonthlyPayment()).isEqualTo(expectedCredit.getMonthlyPayment());
				assertThat(actualCredit.getRate()).isEqualTo(expectedCredit.getRate());
				assertThat(actualCredit.getIsInsuranceEnabled()).isEqualTo(expectedCredit.getIsInsuranceEnabled());
				assertThat(actualCredit.getIsSalaryClient()).isEqualTo(expectedCredit.getIsSalaryClient());
				assertThat(actualCredit.getPaymentSchedule().getFirst()).isEqualTo(expectedFirstPaymentScheduleElement);
				verify(schedulePaymentsCalculatorService, times(1)).countPayment(paymentScheduleCaptor.capture(), anyList(), dailyRateCaptor.capture());
				verify(personalRateCalculatorService, times(1)).countPersonalRate(scoringData, RATE, DtoInitializer.AGE);
				verify(personalRateCalculatorService, times(1)).calculateDailyRate(RATE);
				verify(monthlyPaymentCalculatorService, times(1)).calculate(scoringData.getAmount(), scoringData.getTerm(), RATE);
				verify(schedulePaymentsCalculatorService, times(1)).calculatePaymentScheduleElement(1, scoringData.getAmount(), dailyRate, DtoInitializer.OFFER_0_MONTHLY_PAYMENT, LocalDate.now());
			});
		}
	}
}
