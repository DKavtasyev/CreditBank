package ru.neoflex.neostudy.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import ru.neoflex.neostudy.common.constants.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Данные платежа")
public class PaymentScheduleElementDto {
	@Schema(description = "Номер платежа")
	private Integer number;
	@Schema(description = "Дата платежа")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeFormat.DATE_PATTERN)
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
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof PaymentScheduleElementDto that)) {
			return false;
		}
		return Objects.equals(number, that.number)
				&& Objects.equals(date, that.date)
				&& (Objects.equals(totalPayment, that.totalPayment) || totalPayment.compareTo(that.totalPayment) == 0)
				&& (Objects.equals(interestPayment, that.interestPayment) || interestPayment.compareTo(that.interestPayment) == 0)
				&& (Objects.equals(debtPayment, that.debtPayment) || debtPayment.compareTo(that.debtPayment) == 0)
				&& (Objects.equals(remainingDebt, that.remainingDebt) || remainingDebt.compareTo(that.remainingDebt) == 0);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(number, date, totalPayment, interestPayment, debtPayment, remainingDebt);
	}
}
