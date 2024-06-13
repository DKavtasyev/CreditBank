package ru.neostudy.neoflex.deal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;
import ru.neostudy.neoflex.deal.constants.CreditStatus;
import ru.neostudy.neoflex.deal.dto.PaymentScheduleElementDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
@Table(schema = "public", name = "credit")
@Accessors(chain = true)
public class Credit
{
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
}
