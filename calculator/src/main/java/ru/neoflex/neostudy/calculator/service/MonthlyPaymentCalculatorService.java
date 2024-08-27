package ru.neoflex.neostudy.calculator.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class MonthlyPaymentCalculatorService {
	
	/**
	 * Высчитывает и возвращает сумму ежемесячного платежа в числовом формате {@code BigDecimal}.
	 * @param amount общая сумма кредита.
	 * @param term срок кредита.
	 * @param rate процентная ставка по кредиту.
	 * @return сумму ежемесячного платежа в формате {@code BigDecimal}, высчитываемую с точностью 16 знаков после
	 * запятой по формуле: <br>
	 * <code>amount &times; (monthlyRate + (monthlyRate / ((1 + monthlyRate)<sup>term</sup>) - 1) )</code>,
	 * где <code>monthlyRate = rate / 12</code> - процентная ставка в месяц.
	 */
	BigDecimal calculate(BigDecimal amount, Integer term, BigDecimal rate) {
		BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(12), 16, RoundingMode.HALF_EVEN);
		BigDecimal denominator = BigDecimal.ONE.add(monthlyRate).pow(term).subtract(BigDecimal.ONE).setScale(16, RoundingMode.HALF_EVEN);
		return monthlyRate.divide(denominator, 22, RoundingMode.HALF_EVEN).add(monthlyRate).multiply(amount).setScale(16, RoundingMode.HALF_EVEN);
	}
}
