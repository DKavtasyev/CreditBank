package ru.neostudy.neoflex.calculator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import ru.neostudy.neoflex.calculator.constants.EmploymentStatus;
import ru.neostudy.neoflex.calculator.constants.Position;

import java.math.BigDecimal;

@Data
@Builder
public class EmploymentDto
{
	@NotNull
	@Schema(description = "Рабочий статус", example = "EMPLOYEE")
	EmploymentStatus employmentStatus;
	
	@NotNull
	@Pattern(regexp = "^(0|[1-9][0-9]*)$")
	@Schema(description = "ИНН", example = "12345678")
	String employmentINN;
	
	@NotNull
	@DecimalMin(value = "0")
	@Schema(description = "Заработная плата", example = "150000")
	BigDecimal salary;
	
	@NotNull
	@Schema(description = "Карьерное положение", example = "SPECIALIST")
	Position position;
	
	@NotNull
	@Min(value = 0)
	@Schema(description = "Трудовой стаж", example = "60")
	Integer workExperienceTotal;
	
	@NotNull
	@Min(value = 0)
	@Schema(description = "Продолжительность работы на последнем месте работы", example = "24")
	Integer workExperienceCurrent;
}
