package ru.neoflex.neostudy.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Основные параметры кредита и график платежей")
public class CreditDto {
	@Schema(description = "Сумма займа")
	private BigDecimal amount;
	@Schema(description = "Срок кредитования")
	private Integer term;
	@Schema(description = "Ежемесячный платёж")
	private BigDecimal monthlyPayment;
	@Schema(description = "Процентная ставка")
	private BigDecimal rate;
	@Schema(description = "Полная стоимость кредита")
	private BigDecimal psk;
	@Setter
	@Schema(description = "Страховка включена")
	private Boolean isInsuranceEnabled;
	@Schema(description = "Зарплатный клиент")
	private Boolean isSalaryClient;
	@Schema(description = "График платежей")
	private List<PaymentScheduleElementDto> paymentSchedule;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof CreditDto creditDto)) {
			return false;
		}
		return (Objects.equals(amount, creditDto.amount) || amount.compareTo(creditDto.amount) == 0)
				&& Objects.equals(term, creditDto.term)
				&& (Objects.equals(monthlyPayment, creditDto.monthlyPayment) || monthlyPayment.compareTo(creditDto.monthlyPayment) == 0)
				&& (Objects.equals(rate, creditDto.rate) || rate.compareTo(creditDto.rate) == 0)
				&& (Objects.equals(psk, creditDto.psk) || psk.compareTo(creditDto.psk) == 0)
				&& Objects.equals(isInsuranceEnabled, creditDto.isInsuranceEnabled)
				&& Objects.equals(isSalaryClient, creditDto.isSalaryClient)
				&& Objects.equals(paymentSchedule, creditDto.paymentSchedule);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(amount, term, monthlyPayment, rate, psk, isInsuranceEnabled, isSalaryClient, paymentSchedule);
	}
}

