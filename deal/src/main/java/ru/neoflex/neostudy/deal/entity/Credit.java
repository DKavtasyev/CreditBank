package ru.neoflex.neostudy.deal.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;
import ru.neoflex.neostudy.common.constants.CreditStatus;
import ru.neoflex.neostudy.common.dto.PaymentScheduleElementDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "public", name = "credit")
public class Credit {
	@Id
	@Column(name = "credit_id", nullable = false)
	private UUID creditId;
	
	@Column(name = "amount", nullable = false)
	private BigDecimal amount;
	
	@Column(name = "term", nullable = false)
	private Integer term;
	
	@Column(name = "monthly_payment", nullable = false)
	private BigDecimal monthlyPayment;
	
	@Column(name = "rate", nullable = false)
	private BigDecimal rate;
	
	@Column(name = "psk", nullable = false)
	private BigDecimal psk;
	
	@Column(name = "payment_schedule", nullable = false)
	@JdbcTypeCode(value = SqlTypes.JSON)
	private List<PaymentScheduleElementDto> paymentSchedule;
	
	@Basic
	@Column(name = "insurance_enabled", nullable = false)
	private boolean insuranceEnabled;
	
	@Basic
	@Column(name = "salary_client", nullable = false)
	private boolean salaryClient;
	
	@Column(name = "credit_status")
	@Enumerated(value = EnumType.STRING)
	@JdbcType(PostgreSQLEnumJdbcType.class)
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
