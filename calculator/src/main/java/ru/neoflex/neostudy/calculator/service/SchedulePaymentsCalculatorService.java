package ru.neoflex.neostudy.calculator.service;

import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.calculator.util.CountTime;
import ru.neoflex.neostudy.common.dto.PaymentScheduleElementDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class SchedulePaymentsCalculatorService {
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
	
	BigDecimal countInterestPayment(BigDecimal remainingDebt, BigDecimal dailyRate, int numberOfDays) {
		return remainingDebt.multiply(dailyRate.multiply(BigDecimal.valueOf(numberOfDays))).setScale(16, RoundingMode.HALF_EVEN);
	}
	
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
