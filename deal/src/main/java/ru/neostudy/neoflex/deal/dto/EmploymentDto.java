package ru.neostudy.neoflex.deal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import ru.neostudy.neoflex.deal.constants.EmploymentStatus;
import ru.neostudy.neoflex.deal.constants.EmploymentPosition;

import java.math.BigDecimal;

@Data
@Builder
public class EmploymentDto
{
	@NotNull(message = "Поле \"Рабочий статус\" не может быть пустым")
	@Schema(description = "Рабочий статус", example = "EMPLOYED")
	EmploymentStatus employmentStatus;
	
	@NotNull(message = "Поле \"ИНН\" не может быть пустым")
	@Pattern(regexp = "^([1-9][0-9]{11})$", message = "ИНН должен состоять из 12 цифр")
	@Schema(description = "ИНН", example = "123456789012")
	String employmentINN;
	
	@NotNull(message = "Поле \"Заработная плата\" не может быть пустым")
	@DecimalMin(value = "0", message = "Заработная плата не может быть меньше нуля")
	@Schema(description = "Заработная плата", example = "150000")
	BigDecimal salary;
	
	@NotNull(message = "Поле \"Карьерное положение\" не может быть пустым")
	@Schema(description = "Карьерное положение", example = "WORKER")
	EmploymentPosition position;
	
	@NotNull(message = "Поле \"Трудовой стаж\" не может быть пустым")
	@Min(value = 0, message = "Трудовой стаж не может быть меньше нуля")
	@Schema(description = "Трудовой стаж", example = "60")
	Integer workExperienceTotal;
	
	@NotNull(message = "Поле \"Продолжительность работы на последнем месте работы\" не может быть пустым")
	@Min(value = 0, message = "Продолжительность работы на последнем месте работы не может быть меньше нуля")
	@Schema(description = "Продолжительность работы на последнем месте работы", example = "24")
	Integer workExperienceCurrent;
}
