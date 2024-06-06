package ru.neostudy.neoflex.calculator.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.webservices.server.WebServiceServerTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.neostudy.neoflex.calculator.constants.*;
import ru.neostudy.neoflex.calculator.dto.CreditDto;
import ru.neostudy.neoflex.calculator.dto.LoanOfferDto;
import ru.neostudy.neoflex.calculator.dto.LoanStatementRequestDto;
import ru.neostudy.neoflex.calculator.dto.ScoringDataDto;
import ru.neostudy.neoflex.calculator.exception.LoanRefusalException;
import ru.neostudy.neoflex.calculator.util.DtoInitializer;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application.yaml")
public class CalculatorServiceTest
{
	
	@Spy
	private static CalculatorService service;
	private static PercentConstants constants = new PercentConstants();
	private static MonthlyPaymentCalculatorService monthlyPaymentCalculatorService = new MonthlyPaymentCalculatorService();
	private static PersonalRateCalculatorService personalRateCalculatorService = new PersonalRateCalculatorService(constants);
	
	private static LoanStatementRequestDto loanStatementRequest;
	private ScoringDataDto scoringData;
	private static final BigDecimal RATE = new BigDecimal("0.12");
	
	private static final BigDecimal ONE_POINT = BigDecimal.valueOf(0.01);
	private static final BigDecimal TWO_POINTS = BigDecimal.valueOf(0.02);
	private static final BigDecimal THREE_POINTS = BigDecimal.valueOf(0.03);
	private static final BigDecimal SEVEN_POINTS = BigDecimal.valueOf(0.07);
	
	@BeforeAll
	static void initCalculatorService() throws NoSuchFieldException, IllegalAccessException
	{
		service = new CalculatorService(monthlyPaymentCalculatorService, personalRateCalculatorService, constants);
		Field baseRate = CalculatorService.class.getDeclaredField("BASE_RATE");
		baseRate.setAccessible(true);
		baseRate.set(service, RATE);
		baseRate.setAccessible(false);
	}
	
	@Nested
	@DisplayName("Тестирование метода CalculatorService:preScore()")
	class TestingPreScore
	{
		@BeforeAll
		static void initLoanStatementRequest()
		{
			loanStatementRequest = DtoInitializer.initLoanStatementRequest();
		}
		
		@Test
		void preScore_whenLoanStatementRequestReceived_thenReturnFourLoanOffers()
		{
			List<LoanOfferDto> expectedOffers = DtoInitializer.initOffers();
			List<LoanOfferDto> actualOffers = service.preScore(loanStatementRequest);
			assertThat(actualOffers).isEqualTo(expectedOffers);
		}
	}
	
	@Nested
	@DisplayName("Тестирование метода CalculatorService:score()")
	class TestingScore
	{
		@BeforeEach
		void initScoringData()
		{
			scoringData = DtoInitializer.initScoringData();
		}
		
		@Test
		void score_whenValidScoringDataReceived_thenReturnCreditDto() throws Exception
		{
			CreditDto expectedCredit = DtoInitializer.initCredit();
			CreditDto actualCredit = service.score(scoringData);
			assertThat(actualCredit).isEqualTo(expectedCredit);
		}
		
		@Test
		void score_whenAgeLessThanTwenty_thenThrowLoanStatementException()
		{
			scoringData.setBirthdate(LocalDate.now().minusYears(19).minusDays(364));
			assertThrows(LoanRefusalException.class, () -> service.score(scoringData));
		}
		
		@Test
		void score_whenAgeMoreThanSixtyFive_thenThrowLoanStatementException()
		{
			scoringData.setBirthdate(LocalDate.now().minusYears(66));
			assertThrows(LoanRefusalException.class, () -> service.score(scoringData));
		}
		
		@Test
		void score_whenInappropriateTotalWorkExperience_thenThrowLoanStatementException()
		{
			scoringData.getEmployment().setWorkExperienceTotal(17);
			assertThrows(LoanRefusalException.class, () -> service.score(scoringData));
		}
		
		@Test
		void score_whenInappropriateCurrentWorkExperience_thenThrowLoanStatementException()
		{
			scoringData.getEmployment().setWorkExperienceCurrent(2);
			assertThrows(LoanRefusalException.class, () -> service.score(scoringData));
		}
		
		@Test
		void score_whenInappropriateAmount_thenThrowLoanStatementException()
		{
			BigDecimal salary = scoringData.getEmployment().getSalary();
			scoringData.setAmount(salary.multiply(BigDecimal.valueOf(25.1)));
			assertThrows(LoanRefusalException.class, () -> service.score(scoringData));
		}
		
		@Test
		void score_whenUnemployed_thenThrowLoanStatementException()
		{
			scoringData.getEmployment().setEmploymentStatus(EmploymentStatus.UNEMPLOYED);
			assertThrows(LoanRefusalException.class, () -> service.score(scoringData));
		}
		
		@Test
		void score_whenSelfEmployed_thenRateIsIncreasedByOne() throws Exception
		{
			scoringData.getEmployment().setEmploymentStatus(EmploymentStatus.SELF_EMPLOYED);
			CreditDto actualCredit = service.score(scoringData);
			assertThat(actualCredit.getRate().compareTo(RATE.add(ONE_POINT)) == 0).isTrue();
		}
		
		@Test
		void score_whenBusinessOwner_thenRateIsIncreasedByTwo() throws Exception
		{
			scoringData.getEmployment().setEmploymentStatus(EmploymentStatus.BUSINESS_OWNER);
			CreditDto actualCredit = service.score(scoringData);
			assertThat(actualCredit.getRate().compareTo(RATE.add(TWO_POINTS)) == 0).isTrue();
		}
		
		@Test
		void score_whenMiddleManager_thenRateIsDecreasedByTwo() throws Exception
		{
			scoringData.getEmployment().setPosition(Position.MIDDLE_MANAGER);
			CreditDto actualCredit = service.score(scoringData);
			assertThat(actualCredit.getRate().compareTo(RATE.subtract(TWO_POINTS)) == 0).isTrue();
		}
		
		@Test
		void score_whenTopManager_thenRateIsDecreasedByThree() throws Exception
		{
			scoringData.getEmployment().setPosition(Position.TOP_MANAGER);
			CreditDto actualCredit = service.score(scoringData);
			assertThat(actualCredit.getRate().compareTo(RATE.subtract(THREE_POINTS)) == 0).isTrue();
		}
		
		@Test
		void score_whenMarried_thenRateIsDecreasedByThree() throws Exception
		{
			scoringData.setMaritalStatus(MaritalStatus.MARRIED);
			CreditDto actualCredit = service.score(scoringData);
			assertThat(actualCredit.getRate().compareTo(RATE.subtract(THREE_POINTS)) == 0).isTrue();
		}
		
		@Test
		void score_whenDivorced_thenRateIsIncreasedByOne() throws Exception
		{
			scoringData.setMaritalStatus(MaritalStatus.DIVORCED);
			CreditDto actualCredit = service.score(scoringData);
			assertThat(actualCredit.getRate().compareTo(RATE.add(ONE_POINT)) == 0).isTrue();
		}
		
		@Test
		void score_whenFemaleAndAgeBetween32And60_thenRateIsDecreasedByThree() throws Exception
		{
			scoringData.setGender(Gender.FEMALE);
			scoringData.setBirthdate(LocalDate.now().minusYears(33));
			CreditDto actualCredit = service.score(scoringData);
			assertThat(actualCredit.getRate().compareTo(RATE.subtract(THREE_POINTS)) == 0).isTrue();
		}
		
		@Test
		void score_whenMaleAndAgeBetween30And55_thenRateIsDecreasedByThree() throws Exception
		{
			scoringData.setGender(Gender.MALE);
			scoringData.setBirthdate(LocalDate.now().minusYears(33));
			CreditDto actualCredit = service.score(scoringData);
			assertThat(actualCredit.getRate().compareTo(RATE.subtract(THREE_POINTS)) == 0).isTrue();
		}
		
		@Test
		void score_whenNonBinary_thenRateIsIncreasedBySeven() throws Exception
		{
			scoringData.setGender(Gender.NON_BINARY);
			CreditDto actualCredit = service.score(scoringData);
			assertThat(actualCredit.getRate().compareTo(RATE.add(SEVEN_POINTS)) == 0).isTrue();
		}
		
		@Test
		void score_whenInsuranceEnabled_thenRateIsDecreasedByThree() throws Exception
		{
			scoringData.setIsInsuranceEnabled(true);
			CreditDto actualCredit = service.score(scoringData);
			assertThat(actualCredit.getRate().compareTo(RATE.subtract(THREE_POINTS)) == 0).isTrue();
		}
		
		@Test
		void score_whenSalaryClient_thenRateIsDecreasedByOne() throws Exception
		{
			scoringData.setIsSalaryClient(true);
			CreditDto actualCredit = service.score(scoringData);
			assertThat(actualCredit.getRate().compareTo(RATE.subtract(ONE_POINT)) == 0).isTrue();
		}
	}
}
