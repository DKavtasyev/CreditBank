package ru.neostudy.neoflex.calculator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
	@Schema(description = "Номер платежа")
	private Integer number;
	@Schema(description = "Дата платежа")
	private LocalDate date;
	@Schema(description = "Сумма платежа")
	private BigDecimal totalPayment;
	@Schema(description = "Доля платежа в счёт уплаты процентов")
	private BigDecimal interestPayment;
	@Schema(description = "Доля платежа в счёт уплаты основного долга")
	private BigDecimal debtPayment;
	@Schema(description = "Оставшаяся сумма кредита")
	private BigDecimal remainingDebt;
	
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
