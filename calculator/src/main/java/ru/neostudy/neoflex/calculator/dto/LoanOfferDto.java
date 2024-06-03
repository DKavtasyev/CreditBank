package ru.neostudy.neoflex.calculator.dto;

import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class LoanOfferDto
{
	private UUID statementId;				// Идентификатор заявки
	private BigDecimal requestedAmount;		// Запрашиваемая сумма
	private BigDecimal totalAmount;			// Полная стоимость кредита ПСК
	private Integer term;					// Срок кредитования
	private BigDecimal monthlyPayment;		// Ежемесячный платёж
	private BigDecimal rate;				// Процентная ставка
	private Boolean isInsuranceEnabled;		// Страховка активна
	private Boolean isSalaryClient;			// Зарплатный клиент
	
	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof LoanOfferDto that)) return false;
		
		return requestedAmount.compareTo(that.requestedAmount) == 0 &&
				totalAmount.compareTo(that.totalAmount) == 0 &&
				Objects.equals(term, that.term) &&
				monthlyPayment.compareTo(that.monthlyPayment) == 0 &&
				rate.compareTo(that.rate) == 0 &&
				Objects.equals(isInsuranceEnabled, that.isInsuranceEnabled) &&
				Objects.equals(isSalaryClient, that.isSalaryClient);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(requestedAmount, totalAmount, term, monthlyPayment, rate, isInsuranceEnabled, isSalaryClient);
	}
}
