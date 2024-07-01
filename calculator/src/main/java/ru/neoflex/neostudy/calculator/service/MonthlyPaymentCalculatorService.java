package ru.neoflex.neostudy.calculator.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class MonthlyPaymentCalculatorService {
	BigDecimal calculate(BigDecimal amount, Integer term, BigDecimal rate) {
		BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(12), 16, RoundingMode.HALF_EVEN);
		BigDecimal denominator = BigDecimal.ONE.add(monthlyRate).pow(term).subtract(BigDecimal.ONE).setScale(16, RoundingMode.HALF_EVEN);
		return monthlyRate.divide(denominator, 22, RoundingMode.HALF_EVEN).add(monthlyRate).multiply(amount).setScale(16, RoundingMode.HALF_EVEN);
	}
}
