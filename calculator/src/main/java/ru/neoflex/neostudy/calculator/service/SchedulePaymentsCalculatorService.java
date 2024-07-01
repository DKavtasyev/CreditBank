package ru.neoflex.neostudy.calculator.service;

import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.dto.PaymentScheduleElementDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
			LocalDate date = previousScheduleElement.getDate().plusMonths(1);
			int numberOfDays = (int) ChronoUnit.DAYS.between(previousScheduleElement.getDate(), date);
			BigDecimal interestPayment = countInterestPayment(previousRemainingDebt, dailyRate, numberOfDays);
			BigDecimal debtPayment = monthlyPayment.subtract(interestPayment);
			BigDecimal remainingDebt = previousRemainingDebt.subtract(debtPayment);
			
			PaymentScheduleElementDto paymentScheduleElement = new PaymentScheduleElementDto()
					.setNumber(previousScheduleElement.getNumber() + 1)
					.setDate(date)
					.setTotalPayment(monthlyPayment)
					.setInterestPayment(interestPayment)
					.setDebtPayment(debtPayment)
					.setRemainingDebt(remainingDebt);
			scheduleOfPayments.add(paymentScheduleElement);
			
			countPayment(paymentScheduleElement, scheduleOfPayments, dailyRate);
		}
	}
	
	BigDecimal countInterestPayment(BigDecimal remainingDebt, BigDecimal dailyRate, int numberOfDays) {
		return remainingDebt.multiply(dailyRate.multiply(BigDecimal.valueOf(numberOfDays))).setScale(16, RoundingMode.HALF_EVEN);
	}
}
