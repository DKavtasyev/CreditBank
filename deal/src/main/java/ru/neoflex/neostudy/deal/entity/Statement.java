package ru.neoflex.neostudy.deal.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.deal.entity.jsonb.StatusHistory;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.UUID;

import static ru.neoflex.neostudy.common.constants.DateTimeFormat.DATETIME_PATTERN;

@Entity
@Setter
@Getter
@ToString
@Table(schema = "public", name = "statement")
public class Statement
{
	@Id
	@Column(name = "statement_id", nullable = false)
	private UUID statementId;
	
	@OneToOne
	@JoinColumn(name = "client_id", nullable = false)
	private Client client;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "credit_id")
	private Credit credit;
	
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	@JdbcType(PostgreSQLEnumJdbcType.class)
	private ApplicationStatus status;
	
	@CreationTimestamp
	@Column(name = "creation_date", nullable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATETIME_PATTERN)
	private LocalDateTime creationDate;
	
	@JdbcTypeCode(value = SqlTypes.JSON)
	@Column(name = "applied_offer")
	private LoanOfferDto appliedOffer;
	
	@Column(name = "sign_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATETIME_PATTERN)
	private LocalDateTime signDate;
	
	@Column(name = "ses_code")
	private String sessionCode;
	
	@JdbcTypeCode(value = SqlTypes.JSON)
	@Column(name = "status_history")
	private LinkedList<StatusHistory> statusHistory = new LinkedList<>();
}
