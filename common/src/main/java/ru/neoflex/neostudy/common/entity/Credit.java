package ru.neoflex.neostudy.common.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.neoflex.neostudy.common.constants.CreditStatus;
import ru.neoflex.neostudy.common.dto.PaymentScheduleElementDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity class must match the same class in MS deal except hibernate/jpa annotations.
 */
@Setter
@Getter
@Accessors(chain = true)
public class Credit {
	private UUID creditId;
	private BigDecimal amount;
	private Integer term;
	private BigDecimal monthlyPayment;
	private BigDecimal rate;
	private BigDecimal psk;
	private List<PaymentScheduleElementDto> paymentSchedule;
	private boolean insuranceEnabled;
	private boolean salaryClient;
	private CreditStatus creditStatus;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Credit credit)) {
			return false;
		}
		return insuranceEnabled == credit.insuranceEnabled
				&& salaryClient == credit.salaryClient
				&& amount.compareTo(credit.amount) == 0
				&& term.compareTo(credit.term) == 0
				&& monthlyPayment.compareTo(credit.monthlyPayment) == 0
				&& rate.compareTo(credit.rate) == 0
				&& psk.compareTo(credit.psk) == 0
				&& Objects.equals(paymentSchedule, credit.paymentSchedule)
				&& creditStatus == credit.creditStatus;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(amount, term, monthlyPayment, rate, psk, paymentSchedule, insuranceEnabled, salaryClient, creditStatus);
	}
}
