package ru.neostudy.neoflex.calculator.service;

import org.springframework.stereotype.Service;
import ru.neostudy.neoflex.calculator.config.RateConfig;
import ru.neostudy.neoflex.calculator.config.RefusalConfig;
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
public class PersonalRateCalculatorService
{
	private final RefusalConfig refusalConfig;
	private final RefusalConfig.WorkExperience workExperience;
	private final RateConfig.Woman womanConfig;
	private final RateConfig.Man manConfig;
	
	private final BigDecimal RATIO_OF_AMOUNT_TO_SALARY;
	private final BigDecimal SELF_EMPLOYED_POINTS;
	private final BigDecimal BUSINESS_OWNER_POINTS;
	private final BigDecimal MIDDLE_MANAGER_POINTS;
	private final BigDecimal TOP_MANAGER_POINTS;
	private final BigDecimal MARRIED_POINTS;
	private final BigDecimal DIVORCED_POINTS;
	private final BigDecimal WOMAN_POINTS;
	private final BigDecimal MAN_POINTS;
	private final BigDecimal NON_BINARY_POINTS;
	private final BigDecimal INSURANCE_ENABLED;
	private final BigDecimal SALARY_CLIENT;
	
	public PersonalRateCalculatorService(RefusalConfig refusalConfig, RateConfig rateConfig)
	{
		this.refusalConfig = refusalConfig;
		this.workExperience = refusalConfig.getWorkExperience();
		this.womanConfig = rateConfig.getWoman();
		this.manConfig = rateConfig.getMan();
		
		RATIO_OF_AMOUNT_TO_SALARY = new BigDecimal(refusalConfig.getRatioOfAmountToSalary());
		SELF_EMPLOYED_POINTS = new BigDecimal(rateConfig.getEmploymentStatus().getSelfEmployed());
		BUSINESS_OWNER_POINTS = new BigDecimal(rateConfig.getEmploymentStatus().getBusinessOwner());
		MIDDLE_MANAGER_POINTS = new BigDecimal(rateConfig.getPosition().getMiddleManager());
		TOP_MANAGER_POINTS = new BigDecimal(rateConfig.getPosition().getTopManager());
		MARRIED_POINTS = new BigDecimal(rateConfig.getMaritalStatus().getMarried());
		DIVORCED_POINTS = new BigDecimal(rateConfig.getMaritalStatus().getDivorced());
		WOMAN_POINTS = new BigDecimal(womanConfig.getRate());
		MAN_POINTS = new BigDecimal(manConfig.getRate());
		NON_BINARY_POINTS = new BigDecimal(rateConfig.getNonBinary().getRate());
		INSURANCE_ENABLED = new BigDecimal(rateConfig.getInsuranceEnabled());
		SALARY_CLIENT = new BigDecimal(rateConfig.getSalaryClient());
	}
	
	BigDecimal countPersonalRate(ScoringDataDto scoringData, BigDecimal rate) throws LoanRefusalException
	{
		EmploymentDto employmentDto = scoringData.getEmployment();
		int age = (int) ChronoUnit.YEARS.between(scoringData.getBirthdate(), LocalDate.now());
		
		boolean inappropriateWorkExperience = employmentDto.getWorkExperienceTotal() < workExperience.getMinTotal() || employmentDto.getWorkExperienceCurrent() < workExperience.getMinCurrent();
		boolean inappropriateAge = age < refusalConfig.getMinAge() || age > refusalConfig.getMaxAge();
		boolean inappropriateAmount = scoringData.getAmount().compareTo(employmentDto.getSalary().multiply(RATIO_OF_AMOUNT_TO_SALARY)) > 0;
		boolean isUnemployed = employmentDto.getEmploymentStatus().equals(UNEMPLOYED);
		
		if (inappropriateWorkExperience || inappropriateAge || inappropriateAmount || isUnemployed)
			throw new LoanRefusalException();
		else
		{
			if (employmentDto.getEmploymentStatus().equals(SELF_EMPLOYED))
				rate = rate.add(SELF_EMPLOYED_POINTS);
			else if (employmentDto.getEmploymentStatus().equals(BUSINESS_OWNER))
				rate = rate.add(BUSINESS_OWNER_POINTS);
			
			if (employmentDto.getPosition().equals(MIDDLE_MANAGER))
				rate = rate.add(MIDDLE_MANAGER_POINTS);
			else if (employmentDto.getPosition().equals(TOP_MANAGER))
				rate = rate.add(TOP_MANAGER_POINTS);
			
			if (scoringData.getMaritalStatus().equals(MARRIED))
				rate = rate.add(MARRIED_POINTS);
			else if (scoringData.getMaritalStatus().equals(DIVORCED))
				rate = rate.add(DIVORCED_POINTS);
			
			if ((scoringData.getGender().equals(FEMALE) && age > womanConfig.getAgeFrom() && age < womanConfig.getAgeTo()))
				rate = rate.add(WOMAN_POINTS);
			else if (scoringData.getGender().equals(MALE) && age > manConfig.getAgeFrom() && age < manConfig.getAgeTo())
				rate = rate.add(MAN_POINTS);
			else if (scoringData.getGender().equals(NON_BINARY))
				rate = rate.add(NON_BINARY_POINTS);
			
			if (scoringData.getIsInsuranceEnabled())
				rate = rate.add(INSURANCE_ENABLED);
			if (scoringData.getIsSalaryClient())
				rate = rate.add(SALARY_CLIENT);
		}
		return rate.max(BigDecimal.ZERO);
	}
}
