package ru.neostudy.neoflex.calculator.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.neostudy.neoflex.calculator.validation.CheckAge;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class LoanStatementRequestDto
{
	@NotNull
	@DecimalMin(value = "30000")
	private BigDecimal amount;			// Запрашиваемая сумма
	
	@NotNull
	@Min(value = 6)
	private Integer term;				// Запрашиваемый срок кредитования
	
	@NotBlank
	@Size(min = 2, max = 30)
	private String firstName;			// Имя
	
	@NotBlank
	@Size(min = 2, max = 30)
	private String lastName;			// Фамилия
	
	@Size(min = 2, max = 30)
	private String middleName;			// Отчество
	
	@NotNull
	@Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
	private String email;				// email
	
	@NotNull
	@CheckAge(value = 18)
	private LocalDate birthDate;		// Дата рождения
	
	@NotNull
	@Size(min = 4, max = 4)
	@Pattern(regexp="^(0|[1-9][0-9]*)$")
	private String passportSeries;		// Серия паспорта
	
	@NotNull
	@Size(min = 6, max = 6)
	@Pattern(regexp="^(0|[1-9][0-9]*)$")
	private String passportNumber;		// Номер паспорта
}
