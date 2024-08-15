package ru.neoflex.neostudy.calculator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.calculator.util.CountTime;
import ru.neoflex.neostudy.common.dto.*;
import ru.neoflex.neostudy.common.exception.LoanRefusalException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Главный сервис калькулятора для предварительного расчёта кредита и расчёта графиков платежей по кредиту.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class CalculatorService {
	private final MonthlyPaymentCalculatorService monthlyPaymentCalculatorService;
	private final PersonalRateCalculatorService personalRateCalculatorService;
	private final SchedulePaymentsCalculatorService schedulePaymentsCalculatorService;
	private final RefusalService refusalService;
	
	@Value("${rate.base-rate}")
	private BigDecimal BASE_RATE;
	@Value("${credit.insurance-percent}")
	private BigDecimal INSURANCE_PERCENT;
	@Value("${rate.insurance-enabled}")
	private BigDecimal INSURANCE_ENABLED;
	@Value("${rate.salary-client}")
	private BigDecimal SALARY_CLIENT;
	
	/**
	 * Возвращает List с посчитанными кредитными предложениями в зависимости от кредитных опций. Опции
	 * представляют собой значение {@code boolean}. Метод перебирает все возможные комбинации значений кредитных опций в
	 * цикле for, который повторяется число раз, равное числу кредитных опций. В цикле каждой опции присваивается
	 * значение в соответствии с её разрядом двоичного числа i. Распределение опций по двоичным разрядам осуществлено в
	 * произвольном порядке. Далее аргумент {@code loanStatementRequest} и опции передаются в метод {@code generateOffers},
	 * который на основании переданных значений высчитывает один экземпляр кредитного предложения типа {@code LoanOfferDto},
	 * который добавляется в список кредитных предложений. При прохождении всех итераций цикла список с готовыми
	 * значениями возвращается.
	 * @param loanStatementRequest пользовательский запрос кредита
	 * @return список с рассчитанными кредитными предложениями типа {@code LoanOfferDto}.
	 */
	public List<LoanOfferDto> preScore(LoanStatementRequestDto loanStatementRequest) {
		List<LoanOfferDto> offers = new ArrayList<>();
		for (byte i = 0; i <= 3; i++) {
			boolean isInsuranceEnabled = (i & 2) == 2;
			boolean isSalaryClient = (i & 1) == 1;
			offers.add(generateOffer(loanStatementRequest, isInsuranceEnabled, isSalaryClient));
		}
		return offers;
	}
	
	/**
	 * Возвращает кредитное предложение, рассчитанное в соответствии параметрами метода, базовой процентной ставкой.
	 * Осуществляет корректировку процентной ставки в соответствии с кредитными опциями {@code isInsuranceEnabled},
	 * {@code isSalaryClient}, которые представляют собой значения {@code boolean}. Если параметр {@code isInsuranceEnabled}
	 * активен, тогда к общей сумме кредита {@code amount} добавляется страховка, составляющая определённый процент от
	 * кредита. Процент задаётся в виде множителя в параметре {@code INSURANCE_PERCENT} и равен количеству сотых
	 * долей после единицы. При этом процентная ставка корректируется на значение {@code INSURANCE_ENABLED}. Если
	 * параметр {@code isSalaryClient} активен, то процентная ставка корректируется на величину {@code SALARY_CLIENT}.
	 * Далее высчитывается ежемесячный платёж и формируется и возвращается кредитное предложение.
	 * @param loanStatementRequest данные запроса кредита от пользователя.
	 * @param isInsuranceEnabled параметр страховки кредита.
	 * @param isSalaryClient параметр зарплатный клиент.
	 * @return кредитное предложение типа {@code LoanOfferDto}
	 */
	private LoanOfferDto generateOffer(LoanStatementRequestDto loanStatementRequest, boolean isInsuranceEnabled, boolean isSalaryClient) {
		BigDecimal rate = BASE_RATE;
		BigDecimal amount = loanStatementRequest.getAmount();
		
		if (isInsuranceEnabled) {
			amount = amount.multiply(INSURANCE_PERCENT);
			rate = rate.add(INSURANCE_ENABLED);
		}
		if (isSalaryClient) {
			rate = rate.add(SALARY_CLIENT);
		}
		
		BigDecimal monthlyPayment = monthlyPaymentCalculatorService.calculate(amount, loanStatementRequest.getTerm(), rate);
		log.info("Monthly payment has been calculated. Value = " + monthlyPayment.doubleValue());
		
		return LoanOfferDto.builder()
				.requestedAmount(loanStatementRequest.getAmount())
				.totalAmount(monthlyPayment.multiply(BigDecimal.valueOf(loanStatementRequest.getTerm())))
				.term(loanStatementRequest.getTerm())
				.monthlyPayment(monthlyPayment)
				.rate(rate)
				.isInsuranceEnabled(isInsuranceEnabled)
				.isSalaryClient(isSalaryClient)
				.build();
	}
	
	/**
	 * Возвращает рассчитанные данные кредита, имеющие тип CreditDto. Принимает на вход данные от пользователя
	 * ScoringDataDto, добавляет к сумме кредита процент страховки, если данная кредитная опция активна. Высчитывает
	 * количество полных лет потребителя, проверяет выполнение условий выдачи кредита. При невыполнении условий
	 * выбрасывается исключение {@code LoanRefusalException}. Высчитывает персональную кредитную ставку, из неё
	 * высчитывает дневную кредитную ставку. Высчитывает ежемесячный платёж. По полученным данным высчитывает первый
	 * элемент графика платежей PaymentScheduleElementDto, добавляет его в график платежей, и на основании его вызывает
	 * рекурсивный метод расчёта последующих платежей. Формирует и возвращает готовый объект {@code CreditDto}.
	 * @param scoringData данные от пользователя для расчёта кредита.
	 * @return объект типа CreditDto, содержащий все данные о сроках и суммах платежей по кредиту.
	 * @throws LoanRefusalException если данные от пользователя не соответствуют условиям выдачи кредита.
	 */
	public CreditDto score(ScoringDataDto scoringData) throws LoanRefusalException {
		
		addInsuranceIfRequired(scoringData);
		int age = CountTime.countAge(scoringData.getBirthdate());
		refusalService.checkRefuseConditions(scoringData, age);
		
		List<PaymentScheduleElementDto> scheduleOfPayments = new ArrayList<>();
		BigDecimal rate = personalRateCalculatorService.countPersonalRate(scoringData, BASE_RATE, age);
		BigDecimal dailyRate = personalRateCalculatorService.calculateDailyRate(rate);
		BigDecimal monthlyPayment = monthlyPaymentCalculatorService.calculate(scoringData.getAmount(), scoringData.getTerm(), rate);
		
		PaymentScheduleElementDto firstPaymentScheduleElement = schedulePaymentsCalculatorService.calculatePaymentScheduleElement(1, scoringData.getAmount(), dailyRate, monthlyPayment, LocalDate.now());
		scheduleOfPayments.add(firstPaymentScheduleElement);
		schedulePaymentsCalculatorService.countPayment(firstPaymentScheduleElement, scheduleOfPayments, dailyRate);
		
		return CreditDto.builder()
				.amount(scoringData.getAmount())
				.term(scoringData.getTerm())
				.monthlyPayment(monthlyPayment)
				.rate(rate)
				.psk(scheduleOfPayments.stream().map(PaymentScheduleElementDto::getTotalPayment).reduce(BigDecimal.ZERO, BigDecimal::add))
				.isInsuranceEnabled(scoringData.getIsInsuranceEnabled())
				.isSalaryClient(scoringData.getIsSalaryClient())
				.paymentSchedule(scheduleOfPayments)
				.build();
	}
	
	/**
	 * Если страховка включена, прибавляет к общей сумме кредита определённый процент от неё, который является суммой
	 * страхового взноса. Процент задаётся в параметре {@code INSURANCE_PERCENT} в виде множителя и равен количеству
	 * сотых долей после единицы. Значение общей суммы кредита обновляется в существующем объекте {@code ScoringDataDto}.
	 * @param scoringData данные от пользователя для расчёта кредита.
	 */
	private void addInsuranceIfRequired(ScoringDataDto scoringData) {
		boolean isInsuranceEnabled = scoringData.getIsInsuranceEnabled();
		if (isInsuranceEnabled) {
			scoringData.setAmount(scoringData.getAmount().multiply(INSURANCE_PERCENT));
		}
	}
}
