package ru.neoflex.neostudy.calculator.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.dto.EmploymentDto;
import ru.neoflex.neostudy.common.dto.ScoringDataDto;
import ru.neoflex.neostudy.common.exception.LoanRefusalException;

import java.math.BigDecimal;

import static ru.neoflex.neostudy.common.constants.EmploymentStatus.UNEMPLOYED;

/**
 * Сервис осуществляет проверку соответствия пользователя условиям выдачи кредита.
 */
@Service
public class RefusalService {
	
	@Value("${refusal.ratio-of-amount-to-salary}")
	private BigDecimal ratioOfAmountToSalary;
	@Value("${refusal.work-experience.min-total}")
	private int minTotalWorkExperience;
	@Value("${refusal.work-experience.min-current}")
	private int minCurrentWorkExperience;
	@Value("${refusal.min-age}")
	private int minAge;
	@Value("${refusal.max-age}")
	private int maxAge;
	
	/**
	 * Проверяет пользовательские данные на соответствие условиям выдачи кредита. В случае несоответствия какому-либо
	 * одному или нескольким условиям выбрасывается исключение с указанием в сообщении перечня условий, по которым
	 * данные от пользователя не прошли проверку. Условия, при выполнении хотя бы одного из которых будет вынесен отказ
	 * в выдаче кредита:
	 * <ul>
	 *     <li>Рабочий статус: Безработный.</li>
	 *     <li>Сумма займа больше, чем двадцать пять заплат.</li>
	 *     <li>Возраст менее двадцати или более шестидесяти пяти лет.</li>
	 *     <li>Общий стаж работы менее восемнадцати месяцев.</li>
	 *     <li>Текущий стаж работы менее трёх месяцев.</li>
	 * </ul>
	 *
	 * @param scoringData данные от пользователя для расчёта кредита.
	 * @param age         возраст пользователя.
	 * @throws LoanRefusalException если данные не прошли проверку по какому-либо из условий.
	 */
	public void checkRefuseConditions(ScoringDataDto scoringData, int age) throws LoanRefusalException {
		EmploymentDto employmentDto = scoringData.getEmployment();
		
		boolean inappropriateTotalWorkExperience = employmentDto.getWorkExperienceTotal() < minTotalWorkExperience;
		boolean inappropriateCurrentWorkExperience = employmentDto.getWorkExperienceCurrent() < minCurrentWorkExperience;
		boolean ageIsTooSmall = age < minAge;
		boolean ageIsTooBig = age > maxAge;
		boolean inappropriateAmount = scoringData.getAmount().compareTo(employmentDto.getSalary().multiply(ratioOfAmountToSalary)) > 0;
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
