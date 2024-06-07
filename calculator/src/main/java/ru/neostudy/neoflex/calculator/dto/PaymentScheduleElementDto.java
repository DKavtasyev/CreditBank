package ru.neostudy.neoflex.calculator.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

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
	
	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof PaymentScheduleElementDto that)) return false;
		return Objects.equals(number, that.number)
				&& Objects.equals(date, that.date)
				&& totalPayment.compareTo(that.totalPayment) == 0
				&& interestPayment.compareTo(that.interestPayment) == 0
				&& debtPayment.compareTo(that.debtPayment) == 0
				&& remainingDebt.compareTo(that.remainingDebt) == 0;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(number, date, totalPayment, interestPayment, debtPayment, remainingDebt);
	}
}
