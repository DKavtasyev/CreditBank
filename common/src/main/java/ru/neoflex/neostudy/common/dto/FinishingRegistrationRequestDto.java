package ru.neoflex.neostudy.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import ru.neoflex.neostudy.common.constants.Gender;
import ru.neoflex.neostudy.common.constants.MaritalStatus;

import java.time.LocalDate;

import static ru.neoflex.neostudy.common.constants.DateTimeFormat.DATE_PATTERN;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Пользовательские данные для завершения оформления кредита")
public class FinishingRegistrationRequestDto {
	@NotNull(message = "Пол должен быть указан")
	@Schema(description = "Пол", example = "MALE")
	private Gender gender;
	
	@NotNull(message = "Семейное положение должно быть указано")
	@Schema(description = "Семейное положение", example = "SINGLE")
	private MaritalStatus maritalStatus;
	
	@NotNull(message = "Число иждивенцев должно быть указано")
	@Min(value = 0, message = "Число иждивенцев не может быть меньше нуля")
	@Schema(description = "Число иждивенцев", example = "0")
	private Integer dependentAmount;
	
	@NotNull(message = "Дата выдачи паспорта должна быть указана")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
	@Schema(description = "Дата выдачи паспорта", example = "2020-05-05")
	private LocalDate passportIssueDate;
	
	@NotNull(message = "Место выдачи паспорта должно быть указано")
	@Schema(description = "Место выдачи паспорта", example = "ГУ МВД РОССИИ ПО Г. МОСКВА")
	private String passportIssueBranch;
	
	@NotNull(message = "Информация о работе должна быть указана")
	@Schema(description = "Информация о работе")
	@Valid
	private EmploymentDto employment;
	
	@NotBlank(message = "Номер аккаунта должен быть указан")
	@Pattern(regexp = "^([1-9]\\d*)$", message = "Номер аккаунта должен состоять из цифр")
	@Schema(description = "Номер пользователя", example = "12341613246")
	private String accountNumber;
}
