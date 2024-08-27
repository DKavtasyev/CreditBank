package ru.neoflex.neostudy.calculator.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.neoflex.neostudy.common.constants.EmploymentStatus;
import ru.neoflex.neostudy.common.dto.ScoringDataDto;
import ru.neoflex.neostudy.common.exception.LoanRefusalException;
import ru.neoflex.neostudy.common.util.DtoInitializer;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class RefusalServiceTest {
	
	@Autowired
	private RefusalService refusalService;
	
	private ScoringDataDto scoringData;
	
	@Nested
	@DisplayName("Тестирование метода RefusalService:countPersonalRate()")
	class TestingCountPersonalRate {
		@BeforeEach
		void initScoringData() {
			scoringData = DtoInitializer.initScoringData();
		}
		
		@Test
		void score_whenAgeLessThanTwenty_thenThrowLoanStatementException() {
			LocalDate birthDate = LocalDate.now().minusYears(19).minusDays(364);
			scoringData.setBirthdate(birthDate);
			assertThrows(LoanRefusalException.class, () -> refusalService.checkRefuseConditions(scoringData, 19));
		}
		
		@Test
		void score_whenAgeMoreThanSixtyFive_thenThrowLoanStatementException() {
			scoringData.setBirthdate(LocalDate.now().minusYears(66));
			assertThrows(LoanRefusalException.class, () -> refusalService.checkRefuseConditions(scoringData, 66));
		}
		
		@Test
		void score_whenInappropriateTotalWorkExperience_thenThrowLoanStatementException() {
			scoringData.getEmployment().setWorkExperienceTotal(17);
			assertThrows(LoanRefusalException.class, () -> refusalService.checkRefuseConditions(scoringData, DtoInitializer.AGE));
		}
		
		@Test
		void score_whenInappropriateCurrentWorkExperience_thenThrowLoanStatementException() {
			scoringData.getEmployment().setWorkExperienceCurrent(2);
			assertThrows(LoanRefusalException.class, () -> refusalService.checkRefuseConditions(scoringData, DtoInitializer.AGE));
		}
		
		@Test
		void score_whenInappropriateAmount_thenThrowLoanStatementException() {
			BigDecimal salary = scoringData.getEmployment().getSalary();
			scoringData.setAmount(salary.multiply(BigDecimal.valueOf(25.1)));
			assertThrows(LoanRefusalException.class, () -> refusalService.checkRefuseConditions(scoringData, DtoInitializer.AGE));
		}
		
		@Test
		void score_whenUnemployed_thenThrowLoanStatementException() {
			scoringData.getEmployment().setEmploymentStatus(EmploymentStatus.UNEMPLOYED);
			assertThrows(LoanRefusalException.class, () -> refusalService.checkRefuseConditions(scoringData, DtoInitializer.AGE));
		}
	}
}
