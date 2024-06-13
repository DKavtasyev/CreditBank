package ru.neoflex.neostudy.calculator.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.neoflex.neostudy.common.dto.ScoringDataDto;
import ru.neoflex.neostudy.calculator.exception.LoanRefusalException;
import ru.neoflex.neostudy.common.util.DtoInitializer;
import ru.neoflex.neostudy.common.constants.EmploymentPosition;
import ru.neoflex.neostudy.common.constants.EmploymentStatus;
import ru.neoflex.neostudy.common.constants.Gender;
import ru.neoflex.neostudy.common.constants.MaritalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class PersonalRateCalculatorServiceTest
{
	private static final BigDecimal ONE_POINT = BigDecimal.valueOf(0.01);
	private static final BigDecimal TWO_POINTS = BigDecimal.valueOf(0.02);
	private static final BigDecimal THREE_POINTS = BigDecimal.valueOf(0.03);
	private static final BigDecimal SEVEN_POINTS = BigDecimal.valueOf(0.07);
	private static final BigDecimal RATE = new BigDecimal("0.12");
	
	@Autowired
	private PersonalRateCalculatorService personalRateCalculatorService;
	
	private ScoringDataDto scoringData;
	
	@BeforeEach
	void initScoringData()
	{
		scoringData = DtoInitializer.initScoringData();
	}
	
	@Test
	void score_whenAgeLessThanTwenty_thenThrowLoanStatementException()
	{
		scoringData.setBirthdate(LocalDate.now().minusYears(19).minusDays(364));
		assertThrows(LoanRefusalException.class, () -> personalRateCalculatorService.countPersonalRate(scoringData, RATE));
	}
	
	@Test
	void score_whenAgeMoreThanSixtyFive_thenThrowLoanStatementException()
	{
		scoringData.setBirthdate(LocalDate.now().minusYears(66));
		assertThrows(LoanRefusalException.class, () -> personalRateCalculatorService.countPersonalRate(scoringData, RATE));
	}
	
	@Test
	void score_whenInappropriateTotalWorkExperience_thenThrowLoanStatementException()
	{
		scoringData.getEmployment().setWorkExperienceTotal(17);
		assertThrows(LoanRefusalException.class, () -> personalRateCalculatorService.countPersonalRate(scoringData, RATE));
	}
	
	@Test
	void score_whenInappropriateCurrentWorkExperience_thenThrowLoanStatementException()
	{
		scoringData.getEmployment().setWorkExperienceCurrent(2);
		assertThrows(LoanRefusalException.class, () -> personalRateCalculatorService.countPersonalRate(scoringData, RATE));
	}
	
	@Test
	void score_whenInappropriateAmount_thenThrowLoanStatementException()
	{
		BigDecimal salary = scoringData.getEmployment().getSalary();
		scoringData.setAmount(salary.multiply(BigDecimal.valueOf(25.1)));
		assertThrows(LoanRefusalException.class, () -> personalRateCalculatorService.countPersonalRate(scoringData, RATE));
	}
	
	@Test
	void score_whenUnemployed_thenThrowLoanStatementException()
	{
		scoringData.getEmployment().setEmploymentStatus(EmploymentStatus.UNEMPLOYED);
		assertThrows(LoanRefusalException.class, () -> personalRateCalculatorService.countPersonalRate(scoringData, RATE));
	}
	
	@Test
	void score_whenSelfEmployed_thenRateIsIncreasedByOne() throws Exception
	{
		scoringData.getEmployment().setEmploymentStatus(EmploymentStatus.SELF_EMPLOYED);
		BigDecimal actualRate = personalRateCalculatorService.countPersonalRate(scoringData, RATE);
		assertThat(actualRate.compareTo(RATE.add(ONE_POINT)) == 0).isTrue();
	}
	
	@Test
	void score_whenBusinessOwner_thenRateIsIncreasedByTwo() throws Exception
	{
		scoringData.getEmployment().setEmploymentStatus(EmploymentStatus.BUSINESS_OWNER);
		BigDecimal actualRate = personalRateCalculatorService.countPersonalRate(scoringData, RATE);
		assertThat(actualRate.compareTo(RATE.add(TWO_POINTS)) == 0).isTrue();
	}
	
	@Test
	void score_whenMiddleManager_thenRateIsDecreasedByTwo() throws Exception
	{
		scoringData.getEmployment().setPosition(EmploymentPosition.MID_MANAGER);
		BigDecimal actualRate = personalRateCalculatorService.countPersonalRate(scoringData, RATE);
		assertThat(actualRate.compareTo(RATE.subtract(TWO_POINTS)) == 0).isTrue();
	}
	
	@Test
	void score_whenTopManager_thenRateIsDecreasedByThree() throws Exception
	{
		scoringData.getEmployment().setPosition(EmploymentPosition.TOP_MANAGER);
		BigDecimal actualRate = personalRateCalculatorService.countPersonalRate(scoringData, RATE);
		assertThat(actualRate.compareTo(RATE.subtract(THREE_POINTS)) == 0).isTrue();
	}
	
	@Test
	void score_whenMarried_thenRateIsDecreasedByThree() throws Exception
	{
		scoringData.setMaritalStatus(MaritalStatus.MARRIED);
		BigDecimal actualRate = personalRateCalculatorService.countPersonalRate(scoringData, RATE);
		assertThat(actualRate.compareTo(RATE.subtract(THREE_POINTS)) == 0).isTrue();
	}
	
	@Test
	void score_whenDivorced_thenRateIsIncreasedByOne() throws Exception
	{
		scoringData.setMaritalStatus(MaritalStatus.DIVORCED);
		BigDecimal actualRate = personalRateCalculatorService.countPersonalRate(scoringData, RATE);
		assertThat(actualRate.compareTo(RATE.add(ONE_POINT)) == 0).isTrue();
	}
	
	@Test
	void score_whenFemaleAndAgeBetween32And60_thenRateIsDecreasedByThree() throws Exception
	{
		scoringData.setGender(Gender.FEMALE);
		scoringData.setBirthdate(LocalDate.now().minusYears(33));
		BigDecimal actualRate = personalRateCalculatorService.countPersonalRate(scoringData, RATE);
		assertThat(actualRate.compareTo(RATE.subtract(THREE_POINTS)) == 0).isTrue();
	}
	
	@Test
	void score_whenMaleAndAgeBetween30And55_thenRateIsDecreasedByThree() throws Exception
	{
		scoringData.setGender(Gender.MALE);
		scoringData.setBirthdate(LocalDate.now().minusYears(33));
		BigDecimal actualRate = personalRateCalculatorService.countPersonalRate(scoringData, RATE);
		assertThat(actualRate.compareTo(RATE.subtract(THREE_POINTS)) == 0).isTrue();
	}
	
	@Test
	void score_whenNonBinary_thenRateIsIncreasedBySeven() throws Exception
	{
		scoringData.setGender(Gender.NON_BINARY);
		BigDecimal actualRate = personalRateCalculatorService.countPersonalRate(scoringData, RATE);
		assertThat(actualRate.compareTo(RATE.add(SEVEN_POINTS)) == 0).isTrue();
	}
	
	@Test
	void score_whenInsuranceEnabled_thenRateIsDecreasedByThree() throws Exception
	{
		scoringData.setIsInsuranceEnabled(true);
		BigDecimal actualRate = personalRateCalculatorService.countPersonalRate(scoringData, RATE);
		assertThat(actualRate.compareTo(RATE.subtract(THREE_POINTS)) == 0).isTrue();
	}
	
	@Test
	void score_whenSalaryClient_thenRateIsDecreasedByOne() throws Exception
	{
		scoringData.setIsSalaryClient(true);
		BigDecimal actualRate = personalRateCalculatorService.countPersonalRate(scoringData, RATE);
		assertThat(actualRate.compareTo(RATE.subtract(ONE_POINT)) == 0).isTrue();
	}
}