package ru.neostudy.neoflex.calculator.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.neostudy.neoflex.calculator.constants.Gender;
import ru.neostudy.neoflex.calculator.constants.MaritalStatus;
import ru.neostudy.neoflex.calculator.validation.CheckAge;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class ScoringDataDto
{
	@NotNull
	@DecimalMin(value = "30000")
	BigDecimal amount;				// Предварительно одобренная сумма
	
	@NotNull
	@Min(value = 6)
	Integer term;					// Предварительно одобренный срок кредитования
	
	@NotBlank
	@Size(min = 2, max = 30)
	String firstName;				// Имя
	
	@NotBlank
	@Size(min = 2, max = 30)
	String lastName;				// Фамилия
	
	@Size(min = 2, max = 30)
	String middleName;				// Отчество
	
	@NotNull
	Gender gender;					// Пол
	
	@NotNull
	@CheckAge(value = 18)
	LocalDate birthdate;			// День рождения
	
	@NotNull
	@Size(min = 4, max = 4)
	@Pattern(regexp="^(0|[1-9][0-9]*)$")
	String passportSeries;
	
	@NotNull
	@Size(min = 6, max = 6)
	@Pattern(regexp="^(0|[1-9][0-9]*)$")
	String passportNumber;			// Номер паспорта
	
	@NotNull
	LocalDate passportIssueDate;	// Дата выдачи паспорта
	
	@NotBlank
	String passportIssueBranch;		// Кем выдан
	
	@NotNull
	MaritalStatus maritalStatus;	// Семейное положение
	
	@NotNull
	Integer dependentAmount;		// Количество иждивенцев
	
	@NotNull
	EmploymentDto employment;		// Место работы
	
	@NotBlank
	@Pattern(regexp="^(0|[1-9][0-9]*)$")
	String accountNumber;			// Номер пользователя
	
	@NotNull
	Boolean isInsuranceEnabled;		// Страховка включена
	
	@NotNull
	Boolean isSalaryClient;			// Зарплатный клиент
}
