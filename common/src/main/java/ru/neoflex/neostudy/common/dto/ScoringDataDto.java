package ru.neoflex.neostudy.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import ru.neoflex.neostudy.common.constants.DateTimeFormat;
import ru.neoflex.neostudy.common.constants.Gender;
import ru.neoflex.neostudy.common.constants.MaritalStatus;
import ru.neoflex.neostudy.common.validation.CheckAge;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Пользовательские данные для расчёта кредита")
public class ScoringDataDto {
	@NotNull
	@DecimalMin(value = "30000")
	@Schema(description = "Предварительно одобренная сумма", example = "1000000")
	BigDecimal amount;
	
	@NotNull
	@Min(value = 6)
	@Schema(description = "Предварительно одобренный срок кредитования", example = "12")
	Integer term;
	
	@NotBlank
	@Size(min = 2, max = 30)
	@Schema(description = "Имя", example = "Ivan")
	String firstName;
	
	@NotBlank
	@Size(min = 2, max = 30)
	@Schema(description = "Фамилия", example = "Ivanov")
	String lastName;
	
	@Size(min = 2, max = 30)
	@Schema(description = "Отчество", example = "Ivanovich")
	String middleName;
	
	@NotNull
	@Schema(description = "Пол", example = "MALE")
	Gender gender;
	
	@NotNull
	@CheckAge(value = 18)
	@Schema(description = "Дата рождения", example = "1995-02-15")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeFormat.DATE_PATTERN)
	LocalDate birthdate;
	
	@NotNull
	@Size(min = 4, max = 4)
	@Pattern(regexp = "^([1-9]\\d{3})$")
	@Schema(description = "Серия паспорта", example = "1234")
	String passportSeries;
	
	@NotNull
	@Size(min = 6, max = 6)
	@Pattern(regexp = "^([1-9]\\d{5})$")
	@Schema(description = "Номер паспорта", example = "123456")
	String passportNumber;
	
	@NotNull
	@Schema(description = "Дата выдачи паспорта", example = "2020-05-05")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeFormat.DATE_PATTERN)
	LocalDate passportIssueDate;
	
	@NotBlank
	@Schema(description = "Место выдачи паспорта", example = "ГУ МВД ПО Г. МОСКВА")
	String passportIssueBranch;
	
	@NotNull
	@Schema(description = "Семейное положение", example = "SINGLE")
	MaritalStatus maritalStatus;
	
	@NotNull
	@Schema(description = "Число иждивенцев", example = "0")
	Integer dependentAmount;
	
	@NotNull
	@Schema(description = "Информация о работе")
	EmploymentDto employment;
	
	@NotBlank
	@Pattern(regexp = "^([1-9]\\d*)$")
	@Schema(description = "Номер пользователя", example = "98723645982394178")
	String accountNumber;
	
	@NotNull
	@Schema(description = "Страховка включена", example = "false")
	Boolean isInsuranceEnabled;
	
	@NotNull
	@Schema(description = "Зарплатный клиент", example = "false")
	Boolean isSalaryClient;
}
