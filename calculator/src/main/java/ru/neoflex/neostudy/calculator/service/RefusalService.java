package ru.neoflex.neostudy.calculator.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.dto.EmploymentDto;
import ru.neoflex.neostudy.common.dto.ScoringDataDto;
import ru.neoflex.neostudy.common.exception.LoanRefusalException;

import java.math.BigDecimal;

import static ru.neoflex.neostudy.common.constants.EmploymentStatus.UNEMPLOYED;

@Service
public class RefusalService {
	
	@Value("${refusal.ratio-of-amount-to-salary}")
	private BigDecimal RATIO_OF_AMOUNT_TO_SALARY;
	@Value("${refusal.work-experience.min-total}")
	private int MIN_TOTAL_WORK_EXPERIENCE;
	@Value("${refusal.work-experience.min-current}")
	private int MIN_CURRENT_WORK_EXPERIENCE;
	@Value("${refusal.min-age}")
	private int MIN_AGE;
	@Value("${refusal.max-age}")
	private int MAX_AGE;
	
	/**
	 * Проверяет пользовательские данные на соответствие условиям выдачи кредита. В случае несоответствия какому-либо
	 * одному или нескольким условиям выбрасывается исключение, в сообщении которого перечислены условия, по которым
	 * данные от пользователя не прошли проверку.
	 * @param scoringData данные от пользователя для расчёта кредита.
	 * @param age возраст пользователя.
	 * @throws LoanRefusalException если данные не прошли проверку по какому-либо из условий.
	 */
	public void checkRefuseConditions(ScoringDataDto scoringData, int age) throws LoanRefusalException {
		EmploymentDto employmentDto = scoringData.getEmployment();
		
		boolean inappropriateTotalWorkExperience = employmentDto.getWorkExperienceTotal() < MIN_TOTAL_WORK_EXPERIENCE;
		boolean inappropriateCurrentWorkExperience = employmentDto.getWorkExperienceCurrent() < MIN_CURRENT_WORK_EXPERIENCE;
		boolean ageIsTooSmall = age < MIN_AGE;
		boolean ageIsTooBig = age > MAX_AGE;
		boolean inappropriateAmount = scoringData.getAmount().compareTo(employmentDto.getSalary().multiply(RATIO_OF_AMOUNT_TO_SALARY)) > 0;
		boolean isUnemployed = employmentDto.getEmploymentStatus().equals(UNEMPLOYED);
		
		if (inappropriateTotalWorkExperience || inappropriateCurrentWorkExperience || ageIsTooSmall || ageIsTooBig || inappropriateAmount || isUnemployed) {
			StringBuilder message = new StringBuilder("Loan denied: ");
			
			if (inappropriateTotalWorkExperience) {
				message.append("Total work experience is too small. ");
			}
			if (inappropriateCurrentWorkExperience) {
				message.append("Current work experience is too small. ");
			}
			
			if (ageIsTooSmall) {
				message.append("Age is too small. ");
			}
			else if (ageIsTooBig) {
				message.append("Age is too big. ");
			}
			
			if (inappropriateAmount) {
				message.append("Salary is too small. ");
			}
			if (isUnemployed) {
				message.append("Unemployed.");
			}
			throw new LoanRefusalException(message.toString().trim());
		}
	}
}
