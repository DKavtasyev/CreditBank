package ru.neostudy.neoflex.calculator.dto;

import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class CreditDto
{
	private BigDecimal amount;									// Запрашиваемая сумма
	private Integer term;										// Срок кредитования
	private BigDecimal monthlyPayment;							// Ежемесячный платёж
	private BigDecimal rate;									// Процентная ставка
	private BigDecimal psk;										// Полная стоимость кредита (ПСК)
	private Boolean isInsuranceEnabled;							// Страховка включена
	private Boolean isSalaryClient;								// Зарплатный клиент
	private List<PaymentScheduleElementDto> paymentSchedule;	// График платежей
}
