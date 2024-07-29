package ru.neoflex.neostudy.calculator.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.neoflex.neostudy.common.constants.EmploymentPosition;
import ru.neoflex.neostudy.common.constants.EmploymentStatus;
import ru.neoflex.neostudy.common.constants.Gender;
import ru.neoflex.neostudy.common.constants.MaritalStatus;
import ru.neoflex.neostudy.common.dto.ScoringDataDto;
import ru.neoflex.neostudy.common.util.DtoInitializer;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class PersonalRateCalculatorServiceTest {
	private static final BigDecimal ONE_POINT = BigDecimal.valueOf(0.01);
	private static final BigDecimal TWO_POINTS = BigDecimal.valueOf(0.02);
	private static final BigDecimal THREE_POINTS = BigDecimal.valueOf(0.03);
	private static final BigDecimal SEVEN_POINTS = BigDecimal.valueOf(0.07);
	
	@Value("${rate.base-rate}")
	public BigDecimal RATE;
	
	@Autowired
	private PersonalRateCalculatorService personalRateCalculatorService;
	
	private ScoringDataDto scoringData;
	
	@Nested
	@DisplayName("Тестирование метода PersonalRateCalculatorService:countPersonalRate()")
	class TestingCountPersonalRate {
		@BeforeEach
		void initScoringData() {
			scoringData = DtoInitializer.initScoringData();
		}
		
		@Test
		void score_whenSelfEmployed_thenRateIsIncreasedByOne() {
			scoringData.getEmployment().setEmploymentStatus(EmploymentStatus.SELF_EMPLOYED);
			BigDecimal actualRate = personalRateCalculatorService.countPersonalRate(scoringData, RATE, DtoInitializer.AGE);
			assertThat(actualRate).isEqualTo(RATE.add(ONE_POINT));
		}
		
		@Test
		void score_whenBusinessOwner_thenRateIsIncreasedByTwo() {
			scoringData.getEmployment().setEmploymentStatus(EmploymentStatus.BUSINESS_OWNER);
			BigDecimal actualRate = personalRateCalculatorService.countPersonalRate(scoringData, RATE, DtoInitializer.AGE);
			assertThat(actualRate).isEqualTo(RATE.add(TWO_POINTS));
		}
		
		@Test
		void score_whenMiddleManager_thenRateIsDecreasedByTwo() {
			scoringData.getEmployment().setPosition(EmploymentPosition.MID_MANAGER);
			BigDecimal actualRate = personalRateCalculatorService.countPersonalRate(scoringData, RATE, DtoInitializer.AGE);
			assertThat(actualRate).isEqualTo(RATE.subtract(TWO_POINTS));
		}
		
		@Test
		void score_whenTopManager_thenRateIsDecreasedByThree() {
			scoringData.getEmployment().setPosition(EmploymentPosition.TOP_MANAGER);
			BigDecimal actualRate = personalRateCalculatorService.countPersonalRate(scoringData, RATE, DtoInitializer.AGE);
			assertThat(actualRate).isEqualTo(RATE.subtract(THREE_POINTS));
		}
		
		@Test
		void score_whenMarried_thenRateIsDecreasedByThree() {
			scoringData.setMaritalStatus(MaritalStatus.MARRIED);
			BigDecimal actualRate = personalRateCalculatorService.countPersonalRate(scoringData, RATE, DtoInitializer.AGE);
			assertThat(actualRate).isEqualTo(RATE.subtract(THREE_POINTS));
		}
		
		@Test
		void score_whenDivorced_thenRateIsIncreasedByOne() {
			scoringData.setMaritalStatus(MaritalStatus.DIVORCED);
			BigDecimal actualRate = personalRateCalculatorService.countPersonalRate(scoringData, RATE, DtoInitializer.AGE);
			assertThat(actualRate).isEqualTo(RATE.add(ONE_POINT));
		}
		
		@Test
		void score_whenFemaleAndAgeBetween32And60_thenRateIsDecreasedByThree() {
			scoringData.setGender(Gender.FEMALE);
			scoringData.setBirthdate(LocalDate.now().minusYears(33));
			BigDecimal actualRate = personalRateCalculatorService.countPersonalRate(scoringData, RATE, 33);
			assertThat(actualRate).isEqualTo(RATE.subtract(THREE_POINTS));
		}
		
		@Test
		void score_whenMaleAndAgeBetween30And55_thenRateIsDecreasedByThree() {
			scoringData.setGender(Gender.MALE);
			scoringData.setBirthdate(LocalDate.now().minusYears(33));
			BigDecimal actualRate = personalRateCalculatorService.countPersonalRate(scoringData, RATE, 33);
			assertThat(actualRate).isEqualTo(RATE.subtract(THREE_POINTS));
		}
		
		@Test
		void score_whenNonBinary_thenRateIsIncreasedBySeven() {
			scoringData.setGender(Gender.NON_BINARY);
			BigDecimal actualRate = personalRateCalculatorService.countPersonalRate(scoringData, RATE, DtoInitializer.AGE);
			assertThat(actualRate).isEqualTo(RATE.add(SEVEN_POINTS));
		}
		
		@Test
		void score_whenInsuranceEnabled_thenRateIsDecreasedByThree() {
			scoringData.setIsInsuranceEnabled(true);
			BigDecimal actualRate = personalRateCalculatorService.countPersonalRate(scoringData, RATE, DtoInitializer.AGE);
			assertThat(actualRate).isEqualTo(RATE.subtract(THREE_POINTS));
		}
		
		@Test
		void score_whenSalaryClient_thenRateIsDecreasedByOne() {
			scoringData.setIsSalaryClient(true);
			BigDecimal actualRate = personalRateCalculatorService.countPersonalRate(scoringData, RATE, DtoInitializer.AGE);
			assertThat(actualRate).isEqualTo(RATE.subtract(ONE_POINT));
		}
	}
	
	@Nested
	@DisplayName("Тестирование метода PersonalRateCalculatorService:calculateDailyRate()")
	class TestingCalculateDailyRate {
		
		@ParameterizedTest
		@CsvSource({"0.01, 0.0000273972602740",
				"0.12, 0.0003287671232877",
				"0.16, 0.0004383561643836"
		})
		void calculateDailyRateTest(BigDecimal argument, BigDecimal expectedValue) {
			BigDecimal actualValue = personalRateCalculatorService.calculateDailyRate(argument);
			assertThat(actualValue).isEqualTo(expectedValue);
		}
	}
}
