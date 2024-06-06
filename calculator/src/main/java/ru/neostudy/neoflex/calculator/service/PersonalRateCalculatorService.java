package ru.neostudy.neoflex.calculator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neostudy.neoflex.calculator.constants.PercentConstants;
import ru.neostudy.neoflex.calculator.dto.EmploymentDto;
import ru.neostudy.neoflex.calculator.dto.ScoringDataDto;
import ru.neostudy.neoflex.calculator.exception.LoanRefusalException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static ru.neostudy.neoflex.calculator.constants.EmploymentStatus.*;
import static ru.neostudy.neoflex.calculator.constants.Gender.*;
import static ru.neostudy.neoflex.calculator.constants.MaritalStatus.DIVORCED;
import static ru.neostudy.neoflex.calculator.constants.MaritalStatus.MARRIED;
import static ru.neostudy.neoflex.calculator.constants.Position.MIDDLE_MANAGER;
import static ru.neostudy.neoflex.calculator.constants.Position.TOP_MANAGER;

@Service
@RequiredArgsConstructor
public class PersonalRateCalculatorService
{
	private final PercentConstants constants;
	BigDecimal countPersonalRate(ScoringDataDto scoringData, BigDecimal rate) throws LoanRefusalException
	{
		EmploymentDto employmentDto = scoringData.getEmployment();
		int age = (int) ChronoUnit.YEARS.between(scoringData.getBirthdate(), LocalDate.now());
		
		boolean inappropriateWorkExperience = employmentDto.getWorkExperienceTotal() < constants.MIN_TOTAL_WORK_EXPERIENCE || employmentDto.getWorkExperienceCurrent() < constants.MIN_CURRENT_WORK_EXPERIENCE;
		boolean inappropriateAge = age < constants.MIN_AGE || age > constants.MAX_AGE;
		boolean inappropriateAmount = scoringData.getAmount().compareTo(employmentDto.getSalary().multiply(constants.RATIO_OF_AMOUNT_TO_SALARY)) > 0;
		boolean isUnemployed = employmentDto.getEmploymentStatus().equals(UNEMPLOYED);
		
		if (inappropriateWorkExperience || inappropriateAge || inappropriateAmount || isUnemployed)
			throw new LoanRefusalException();
		else
		{
			if (employmentDto.getEmploymentStatus().equals(SELF_EMPLOYED))
				rate = rate.add(constants.SELF_EMPLOYED_POINTS);
			else if (employmentDto.getEmploymentStatus().equals(BUSINESS_OWNER))
				rate = rate.add(constants.BUSINESS_OWNER_POINTS);
			
			if (employmentDto.getPosition().equals(MIDDLE_MANAGER))
				rate = rate.add(constants.MIDDLE_MANAGER_POINTS);
			else if (employmentDto.getPosition().equals(TOP_MANAGER))
				rate = rate.add(constants.TOP_MANAGER_POINTS);
			
			if (scoringData.getMaritalStatus().equals(MARRIED))
				rate = rate.add(constants.MARRIED_POINTS);
			else if (scoringData.getMaritalStatus().equals(DIVORCED))
				rate = rate.add(constants.DIVORCED_POINTS);
			
			if ((scoringData.getGender().equals(FEMALE) && age > constants.WOMAN_AGE_FROM && age < constants.WOMAN_AGE_TO))
				rate = rate.add(constants.WOMAN_POINTS);
			else if (scoringData.getGender().equals(MALE) && age > constants.MAN_AGE_FROM && age < constants.MAN_AGE_TO)
				rate = rate.add(constants.MAN_POINTS);
			else if (scoringData.getGender().equals(NON_BINARY))
				rate = rate.add(constants.NON_BINARY_POINTS);
			
			if (scoringData.getIsInsuranceEnabled())
				rate = rate.add(constants.INSURANCE_ENABLED);
			if (scoringData.getIsSalaryClient())
				rate = rate.add(constants.SALARY_CLIENT);
		}
		return rate.max(BigDecimal.ZERO);
	}
}
