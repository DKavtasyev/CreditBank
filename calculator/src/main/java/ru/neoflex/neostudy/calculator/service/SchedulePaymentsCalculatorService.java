package ru.neoflex.neostudy.calculator.service;

import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.calculator.util.CountTime;
import ru.neoflex.neostudy.common.dto.PaymentScheduleElementDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * Сервис осуществляет расчёт графика и сумм платежей по кредиту.
 */
@Service
public class SchedulePaymentsCalculatorService {
	
	/**
	 * Рассчитывает и добавляет в график очередной элемент платежа по кредиту PaymentScheduleElementDto на основании
	 * предыдущего элемента графика платежей. Если оставшаяся сумма кредита плюс доля ежемесячного платежа, идущая в
	 * счёт уплаты процентов, больше, чем сумма ежемесячного платежа, тогда происходит расчёт следующего платежа, он
	 * добавляется в список с предыдущими платежами, затем происходит рекурсивный вызов текущей функции с передачей
	 * последнего рассчитанного платежа в качестве параметра предыдущего платежа.
	 * Если оставшаяся сумма кредита плюс доля платежа, идущая в счёт уплаты процентов, меньше, чем ежемесячный платёж,
	 * тогда оставшаяся сумма кредита прибавляется к предыдущему платежу, кредит полностью выплачен, и этот платёж
	 * является последним в графике платежей. Выполнение этого условия является условием выхода из рекурсии.
	 * @param previousScheduleElement последний из рассчитанных элементов графика платежа.
	 * @param scheduleOfPayments список с уже рассчитанными элементами платежей.
	 * @param dailyRate значение дневной процентной ставки.
	 */
	void countPayment(PaymentScheduleElementDto previousScheduleElement, List<PaymentScheduleElementDto> scheduleOfPayments, BigDecimal dailyRate) {
		BigDecimal monthlyPayment = previousScheduleElement.getTotalPayment();
		BigDecimal previousRemainingDebt = previousScheduleElement.getRemainingDebt();
		
		if (previousRemainingDebt.add(previousScheduleElement.getInterestPayment()).compareTo(monthlyPayment) <= 0) {
			previousScheduleElement.setTotalPayment(monthlyPayment.add(previousRemainingDebt));
		}
		else {
			PaymentScheduleElementDto paymentScheduleElement = calculatePaymentScheduleElement(previousScheduleElement.getNumber() + 1, previousRemainingDebt, dailyRate, monthlyPayment, previousScheduleElement.getDate());
			scheduleOfPayments.add(paymentScheduleElement);
			countPayment(paymentScheduleElement, scheduleOfPayments, dailyRate);
		}
	}
	
	/**
	 * Высчитывает и возвращает сумму процентов, которая будет начислена на оставшуюся сумму кредита за количество дней,
	 * переданных в метод в качестве параметра. При расчёте суммы процентов используется ежедневная процентная ставка.
	 * Данная сумма рассчитывается отдельно для каждого ежемесячного платежа по кредиту и составляет от него
	 * определённую часть наравне с суммой, которая идёт в счёт погашения основного долга по кредиту.
	 * @param remainingDebt оставшаяся сумма кредита.
	 * @param dailyRate ежедневная процентная ставка, выраженная в виде десятичной дроби.
	 * @param numberOfDays количество дней, за которые производится расчёт начисления процентов.
	 * @return число в формате BigDecimal с точностью 16 знаков после запятой, равное
	 * <code>remainingDebt &times; dailyRate &times; numberOfDays.
	 */
	private BigDecimal countInterestPayment(BigDecimal remainingDebt, BigDecimal dailyRate, int numberOfDays) {
		return remainingDebt.multiply(dailyRate.multiply(BigDecimal.valueOf(numberOfDays))).setScale(16, RoundingMode.HALF_EVEN);
	}
	
	/**
	 * Возвращает объект следующего ежемесячного платежа. Рассчитывает следующие его параметры:
	 * <ul>
	 *     <li>Дата платежа.</li>
	 *     <li>Сумма ежемесячного платежа.</li>
	 *     <li>Доля ежемесячного платежа в счёт погашения процентов.</li>
	 *     <li>Доля ежемесячного платежа в счёт погашения основного долга.</li>
	 *     <li>Оставшаяся сумма кредита.</li>
	 * </ul>
	 * @param number номер платежа.
	 * @param currentRemainingDebt текущая оставшаяся сумма кредита.
	 * @param dailyRate ежедневная процентная ставка.
	 * @param monthlyPayment сумма ежемесячного платёжа.
	 * @param currentDate текущая дата.
	 * @return объект PaymentScheduleElementDto.
	 */
	public PaymentScheduleElementDto calculatePaymentScheduleElement(int number, BigDecimal currentRemainingDebt, BigDecimal dailyRate, BigDecimal monthlyPayment, LocalDate currentDate) {
		LocalDate paymentDate = currentDate.plusMonths(1);
		int numberOfDays = CountTime.countDays(currentDate, paymentDate);
		BigDecimal interestPayment = countInterestPayment(currentRemainingDebt, dailyRate, numberOfDays);
		BigDecimal debtPayment = monthlyPayment.subtract(interestPayment);
		BigDecimal remainingDebt = currentRemainingDebt.subtract(debtPayment);
		
		return PaymentScheduleElementDto.builder()
				.number(number)
				.date(paymentDate)
				.totalPayment(monthlyPayment)
				.interestPayment(interestPayment)
				.debtPayment(debtPayment)
				.remainingDebt(remainingDebt)
				.build();
	}
}
