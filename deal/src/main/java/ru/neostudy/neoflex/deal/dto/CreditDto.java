package ru.neostudy.neoflex.deal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class CreditDto
{
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
	@Schema(description = "Страховка включена")
	private Boolean isInsuranceEnabled;
	@Schema(description = "Зарплатный клиент")
	private Boolean isSalaryClient;
	@Schema(description = "График платежей")
	private List<PaymentScheduleElementDto> paymentSchedule;
}
