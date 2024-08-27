package ru.neoflex.neostudy.calculator.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.constants.Gender;
import ru.neoflex.neostudy.common.dto.EmploymentDto;
import ru.neoflex.neostudy.common.dto.ScoringDataDto;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static ru.neoflex.neostudy.common.constants.EmploymentPosition.MID_MANAGER;
import static ru.neoflex.neostudy.common.constants.EmploymentPosition.TOP_MANAGER;
import static ru.neoflex.neostudy.common.constants.EmploymentStatus.BUSINESS_OWNER;
import static ru.neoflex.neostudy.common.constants.EmploymentStatus.SELF_EMPLOYED;
import static ru.neoflex.neostudy.common.constants.MaritalStatus.DIVORCED;
import static ru.neoflex.neostudy.common.constants.MaritalStatus.MARRIED;

/**
 * Сервис осуществляет расчёт персональной процентной ставки по кредиту в зависимости от значений значимых для расчёта
 * кредита параметров, которые были указаны пользователем.
 */
@Service
public class PersonalRateCalculatorService {
	
	@Value("${rate.employment-status.self-employed}")
	private BigDecimal selfEmployedPoints;
	@Value("${rate.employment-status.business-owner}")
	private BigDecimal businessOwnerPoints;
	@Value("${rate.position.middle-manager}")
	private BigDecimal middleManagerPoints;
	@Value("${rate.position.top-manager}")
	private BigDecimal topManagerPoints;
	@Value("${rate.marital-status.married}")
	private BigDecimal marriedPoints;
	@Value("${rate.marital-status.divorced}")
	private BigDecimal divorcedPoints;
	@Value("${rate.woman.rate}")
	private BigDecimal womanPoints;
	@Value("${rate.man.rate}")
	private BigDecimal manPoints;
	@Value("${rate.non-binary.rate}")
	private BigDecimal nonBinaryPoints;
	@Value("${rate.insurance-enabled}")
	private BigDecimal insuranceEnabled;
	@Value("${rate.salary-client}")
	private BigDecimal salaryClient;
	@Value("${rate.woman.age-from}")
	private int womanAgeFrom;
	@Value("${rate.woman.age-to}")
	private int womanAgeTo;
	@Value("${rate.man.age-from}")
	private int manAgeFrom;
	@Value("${rate.man.age-to}")
	private int manAgeTo;
	
	/**
	 * Производит расчёт и возвращает значение персональной процентной ставки в формате {@code BigDecimal} в зависимости
	 * от параметров, указанных пользователем.
	 * @param scoringData данные от пользователя для расчёта кредита.
	 * @param rate процентная ставка.
	 * @param age возраст пользователя.
	 * @return персонально рассчитанная процентная ставка в формате {@code BigDecimal} в виде десятичной дроби.
	 */
	BigDecimal countPersonalRate(ScoringDataDto scoringData, BigDecimal rate, int age) {
		EmploymentDto employmentDto = scoringData.getEmployment();
		
		if (employmentDto.getEmploymentStatus().equals(SELF_EMPLOYED)) {
			rate = rate.add(selfEmployedPoints);
		}
		else if (employmentDto.getEmploymentStatus().equals(BUSINESS_OWNER)) {
			rate = rate.add(businessOwnerPoints);
		}
		
		if (employmentDto.getPosition().equals(MID_MANAGER)) {
			rate = rate.add(middleManagerPoints);
		}
		else if (employmentDto.getPosition().equals(TOP_MANAGER)) {
			rate = rate.add(topManagerPoints);
		}
		
		if (scoringData.getMaritalStatus().equals(MARRIED)) {
			rate = rate.add(marriedPoints);
		}
		else if (scoringData.getMaritalStatus().equals(DIVORCED)) {
			rate = rate.add(divorcedPoints);
		}
		
		if ((scoringData.getGender().equals(Gender.FEMALE) && age > womanAgeFrom && age < womanAgeTo)) {
			rate = rate.add(womanPoints);
		}
		else if (scoringData.getGender().equals(Gender.MALE) && age > manAgeFrom && age < manAgeTo) {
			rate = rate.add(manPoints);
		}
		else if (scoringData.getGender().equals(Gender.NON_BINARY)) {
			rate = rate.add(nonBinaryPoints);
		}
		
		boolean isInsuranceEnabled = scoringData.getIsInsuranceEnabled();
		if (isInsuranceEnabled) {
			rate = rate.add(insuranceEnabled);
		}
		boolean isSalaryClient = scoringData.getIsSalaryClient();
		if (isSalaryClient) {
			rate = rate.add(salaryClient);
		}
		
		return rate.max(BigDecimal.ZERO);
	}
	
	/**
	 * Возвращает величину дневной процентной ставки в виде десятичной дроби.
	 * @param rate процентная ставка по кредиту.
	 * @return число в формате {@code BigDecimal} с точностью 16 знаков после запятой, равное <br>
	 * <code>rate / 365</code>
	 */
	public BigDecimal calculateDailyRate(BigDecimal rate) {
		return rate.divide(BigDecimal.valueOf(365), 16, RoundingMode.HALF_EVEN);
	}
}
