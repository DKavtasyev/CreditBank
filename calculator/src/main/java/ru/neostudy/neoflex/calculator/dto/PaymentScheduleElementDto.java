package ru.neostudy.neoflex.calculator.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class PaymentScheduleElementDto
{
	private Integer number;					// Номер платежа
	private LocalDate date;					// Дата платежа
	private BigDecimal totalPayment;		// Сумма платежа
	private BigDecimal interestPayment;		// Доля платежа в счёт процентов
	private BigDecimal debtPayment;			// Доля платежа в счёт основного долга
	private BigDecimal remainingDebt;		// Оставшаяся сумма долга
}
