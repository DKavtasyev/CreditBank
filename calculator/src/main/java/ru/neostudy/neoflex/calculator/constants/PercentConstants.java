package ru.neostudy.neoflex.calculator.constants;

import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Вычитаемые или прибавляемые значения к процентной ставке, для удобства приведённые к типу BigDecimal.
 * Да, они не константы. Пока не очень понимаю, как лучше в Spring сделать константы.
 */

@Component
public class PercentConstants
{
	//------------------------------------------------------------------------------------------------------------------
	//												Базовая ставка
	//------------------------------------------------------------------------------------------------------------------
	
	@Value("${base_rate}")
	private String baseRate;
	public BigDecimal BASE_RATE = new BigDecimal(baseRate);
	
	//------------------------------------------------------------------------------------------------------------------
	//												Процент страховки
	//------------------------------------------------------------------------------------------------------------------
	
	@Value("${credit.insurance}")
	private String insurance;
	public BigDecimal INSURANCE_PERCENT = new BigDecimal(insurance);
	
	//------------------------------------------------------------------------------------------------------------------
	//												Прескоринг условий
	//------------------------------------------------------------------------------------------------------------------
	
	@SuppressWarnings("SpellCheckingInspection")
	@Value("${credit.prescoring.amount.min}")
	private String minAmount;
	public BigDecimal MIN_AMOUNT = new BigDecimal(minAmount);
	
	@SuppressWarnings("SpellCheckingInspection")
	@Value("${credit.prescoring.term.min}")
	private String minTerm;
	public BigDecimal MIN_TERM = new BigDecimal(minTerm);
	
	//------------------------------------------------------------------------------------------------------------------
	//												Скоринг. Расчёт персональной ставки
	//------------------------------------------------------------------------------------------------------------------
	
	@Value("${credit.scoring.rates.insurance_enabled}")
	private String insuranceEnabled;
	public BigDecimal INSURANCE_ENABLED = new BigDecimal(insuranceEnabled);
	
	@Value("${credit.scoring.rates.salary_client}")
	private String salaryClient;
	public BigDecimal SALARY_CLIENT = new BigDecimal(salaryClient);
	
	@Value("${credit.scoring.rates.employment_status.self_employed}")
	private String selfEmployed;
	public BigDecimal SELF_EMPLOYED_POINTS = new BigDecimal(selfEmployed);
	
	@Value("${credit.scoring.rates.employment_status.business_owner}")
	private String businessOwner;
	public BigDecimal BUSINESS_OWNER_POINTS = new BigDecimal(businessOwner);
	
	@Value("${credit.scoring.rates.position.middle_manager}")
	private String middleManager;
	public BigDecimal MIDDLE_MANAGER_POINTS = new BigDecimal(middleManager);
	
	@Value("${credit.scoring.rates.position.top_manager}")
	private String topManager;
	public BigDecimal TOP_MANAGER_POINTS = new BigDecimal(topManager);
	
	@Value("${credit.scoring.rates.marital_status.married}")
	private String married;
	public BigDecimal MARRIED_POINTS = new BigDecimal(married);
	
	@Value("${credit.scoring.rates.marital_status.divorced}")
	private String divorced;
	public BigDecimal DIVORCED_POINTS = new BigDecimal(divorced);
	
	@Value("${credit.scoring.rates.gender.woman.age.from}")
	public int WOMAN_AGE_FROM;
	
	@Value("${credit.scoring.rates.gender.woman.age.to}")
	public int WOMAN_AGE_TO;
	
	@Value("${credit.scoring.rates.gender.woman.rate}")
	private String womanPoints;
	public BigDecimal WOMAN_POINTS = new BigDecimal(womanPoints);
	
	@Value("${credit.scoring.rates.gender.man.age.from}")
	public int MAN_AGE_FROM;
	
	@Value("${credit.scoring.rates.gender.man.age.to}")
	public int MAN_AGE_TO;
	
	@Value("${credit.scoring.rates.gender.man.rate}")
	private String manPoints;
	public BigDecimal MAN_POINTS = new BigDecimal(manPoints);
	
	@Value("${credit.scoring.rates.gender.non_binary.rate}")
	private String nonBinaryPoints;
	public BigDecimal NON_BINARY_POINTS = new BigDecimal(nonBinaryPoints);
	
	//------------------------------------------------------------------------------------------------------------------
	//												Скоринг. Отказ в кредите
	//------------------------------------------------------------------------------------------------------------------
	
	@Value("${credit.scoring.refusal.ratio_of_amount_to_salary}")
	private String ratioOfAmountToSalary;
	public BigDecimal RATIO_OF_AMOUNT_TO_SALARY = new BigDecimal(ratioOfAmountToSalary);
	
	@Value("${credit.scoring.refusal.age.min}")
	public int MIN_AGE;
	
	@Value("${credit.scoring.refusal.age.max}")
	public int MAX_AGE;
	
	@Value("${credit.scoring.refusal.work_experience.total.min}")
	public int MIN_TOTAL_WORK_EXPERIENCE;
	
	@Value("${credit.scoring.refusal.work_experience.current.min}")
	public int MIN_CURRENT_WORK_EXPERIENCE;
}
