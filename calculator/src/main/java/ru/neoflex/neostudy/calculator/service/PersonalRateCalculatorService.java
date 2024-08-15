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
	private BigDecimal SELF_EMPLOYED_POINTS;
	@Value("${rate.employment-status.business-owner}")
	private BigDecimal BUSINESS_OWNER_POINTS;
	@Value("${rate.position.middle-manager}")
	private BigDecimal MIDDLE_MANAGER_POINTS;
	@Value("${rate.position.top-manager}")
	private BigDecimal TOP_MANAGER_POINTS;
	@Value("${rate.marital-status.married}")
	private BigDecimal MARRIED_POINTS;
	@Value("${rate.marital-status.divorced}")
	private BigDecimal DIVORCED_POINTS;
	@Value("${rate.woman.rate}")
	private BigDecimal WOMAN_POINTS;
	@Value("${rate.man.rate}")
	private BigDecimal MAN_POINTS;
	@Value("${rate.non-binary.rate}")
	private BigDecimal NON_BINARY_POINTS;
	@Value("${rate.insurance-enabled}")
	private BigDecimal INSURANCE_ENABLED;
	@Value("${rate.salary-client}")
	private BigDecimal SALARY_CLIENT;
	@Value("${rate.woman.age-from}")
	private int WOMAN_AGE_FROM;
	@Value("${rate.woman.age-to}")
	private int WOMAN_AGE_TO;
	@Value("${rate.man.age-from}")
	private int MAN_AGE_FROM;
	@Value("${rate.man.age-to}")
	private int MAN_AGE_TO;
	
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
			rate = rate.add(SELF_EMPLOYED_POINTS);
		}
		else if (employmentDto.getEmploymentStatus().equals(BUSINESS_OWNER)) {
			rate = rate.add(BUSINESS_OWNER_POINTS);
		}
		
		if (employmentDto.getPosition().equals(MID_MANAGER)) {
			rate = rate.add(MIDDLE_MANAGER_POINTS);
		}
		else if (employmentDto.getPosition().equals(TOP_MANAGER)) {
			rate = rate.add(TOP_MANAGER_POINTS);
		}
		
		if (scoringData.getMaritalStatus().equals(MARRIED)) {
			rate = rate.add(MARRIED_POINTS);
		}
		else if (scoringData.getMaritalStatus().equals(DIVORCED)) {
			rate = rate.add(DIVORCED_POINTS);
		}
		
		if ((scoringData.getGender().equals(Gender.FEMALE) && age > WOMAN_AGE_FROM && age < WOMAN_AGE_TO)) {
			rate = rate.add(WOMAN_POINTS);
		}
		else if (scoringData.getGender().equals(Gender.MALE) && age > MAN_AGE_FROM && age < MAN_AGE_TO) {
			rate = rate.add(MAN_POINTS);
		}
		else if (scoringData.getGender().equals(Gender.NON_BINARY)) {
			rate = rate.add(NON_BINARY_POINTS);
		}
		
		boolean isInsuranceEnabled = scoringData.getIsInsuranceEnabled();
		if (isInsuranceEnabled) {
			rate = rate.add(INSURANCE_ENABLED);
		}
		boolean isSalaryClient = scoringData.getIsSalaryClient();
		if (isSalaryClient) {
			rate = rate.add(SALARY_CLIENT);
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
