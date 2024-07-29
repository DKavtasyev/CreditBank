package ru.neoflex.neostudy.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Данные о кредитном предложении")
public class LoanOfferDto {
	@Schema(description = "Идентификатор предложения")
	private UUID statementId;
	@Schema(description = "Запрашиваемая сумма", example = "1000000")
	private BigDecimal requestedAmount;
	@Schema(description = "Полная стоимость кредита")
	private BigDecimal totalAmount;
	@Schema(description = "Срок кредитования", example = "6")
	private Integer term;
	@Schema(description = "Ежемесячный платёж")
	private BigDecimal monthlyPayment;
	@Schema(description = "Процентная ставка", example = "0.16")
	private BigDecimal rate;
	@Schema(description = "Страховка включена", example = "false")
	private Boolean isInsuranceEnabled;
	@Schema(description = "Зарплатный клиент", example = "false")
	private Boolean isSalaryClient;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof LoanOfferDto that)) {
			return false;
		}
		
		return requestedAmount.compareTo(that.requestedAmount) == 0 &&
				(Objects.equals(totalAmount, that.totalAmount) || totalAmount.compareTo(that.totalAmount) == 0) &&
				Objects.equals(term, that.term) &&
				(Objects.equals(monthlyPayment, that.monthlyPayment) || monthlyPayment.compareTo(that.monthlyPayment) == 0) &&
				(Objects.equals(rate, that.rate) || rate.compareTo(that.rate) == 0) &&
				Objects.equals(isInsuranceEnabled, that.isInsuranceEnabled) &&
				Objects.equals(isSalaryClient, that.isSalaryClient);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(requestedAmount, totalAmount, term, monthlyPayment, rate, isInsuranceEnabled, isSalaryClient);
	}
}
