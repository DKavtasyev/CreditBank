package ru.neoflex.neostudy.deal.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;
import ru.neoflex.neostudy.common.constants.Gender;
import ru.neoflex.neostudy.common.constants.MaritalStatus;
import ru.neoflex.neostudy.deal.entity.jsonb.Employment;
import ru.neoflex.neostudy.deal.entity.jsonb.Passport;

import java.time.LocalDate;
import java.util.UUID;

import static ru.neoflex.neostudy.common.constants.DateTimeFormat.DATE_PATTERN;

@Entity
@Setter
@Getter
@Accessors(chain = true)
public class Client
{
	@Id
	@Column(name = "client_id", nullable = false)
	private UUID clientIdUuid;
	
	@Column(name = "last_name", nullable = false)
	private String lastName;
	
	@Column(name = "first_name", nullable = false)
	private String firstName;
	
	@Column(name = "middle_name")
	private String middleName;
	
	@Basic
	@Column(name = "birth_date", nullable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
	private LocalDate birthdate;
	
	@Column(name = "email", nullable = false)
	private String email;
	
	@Column(name = "gender")
	@Enumerated(value = EnumType.STRING)
	@JdbcType(PostgreSQLEnumJdbcType.class)
	private Gender gender;
	
	@Column(name = "marital_status")
	@JdbcType(PostgreSQLEnumJdbcType.class)
	@Enumerated(value = EnumType.STRING)
	private MaritalStatus maritalStatus;
	
	@Column(name = "dependent_amount")
	private Integer dependentAmount;
	
	@Column(name = "passport", nullable = false)
	@JdbcTypeCode(value = SqlTypes.JSON)
	private Passport passport;
	
	@Column(name = "employment")
	@JdbcTypeCode(value = SqlTypes.JSON)
	private Employment employment;
	
	@Column(name = "account_number")
	private String accountNumber;
}
