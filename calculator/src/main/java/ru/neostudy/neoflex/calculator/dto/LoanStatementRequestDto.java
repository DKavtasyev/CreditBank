package ru.neostudy.neoflex.calculator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
	@Schema(description = "Запрашиваемая сумма", example = "1000000")
	private BigDecimal amount;
	
	@NotNull
	@Min(value = 6)
	@Schema(description = "Запрашиваемый срок кредитования", example = "12")
	private Integer term;
	
	@NotBlank
	@Size(min = 2, max = 30)
	@Schema(description = "Имя", example = "Ivan")
	private String firstName;
	
	@NotBlank
	@Size(min = 2, max = 30)
	@Schema(description = "Фамилия", example = "Ivanov")
	private String lastName;
	
	@Size(min = 2, max = 30)
	@Schema(description = "Отчество", example = "Ivanovich")
	private String middleName;
	
	@NotNull
	@Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
	@Schema(description = "email", example = "ivan@mail.ru")
	private String email;
	
	@NotNull
	@CheckAge(value = 18)
	@Schema(description = "Дата рождения", example = "1995-02-15")
	private LocalDate birthDate;		//
	
	@NotNull
	@Size(min = 4, max = 4)
	@Pattern(regexp="^(0|[1-9][0-9]*)$")
	@Schema(description = "Серия паспорта", example = "1234")
	private String passportSeries;
	
	@NotNull
	@Size(min = 6, max = 6)
	@Pattern(regexp="^(0|[1-9][0-9]*)$")
	@Schema(description = "Номер паспорта", example = "123456")
	private String passportNumber;
}
