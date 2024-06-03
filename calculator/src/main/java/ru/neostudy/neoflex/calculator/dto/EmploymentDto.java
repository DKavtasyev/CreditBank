package ru.neostudy.neoflex.calculator.dto;

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
	EmploymentStatus employmentStatus;
	
	@NotNull
	@Pattern(regexp="^(0|[1-9][0-9]*)$")
	String employmentINN;
	
	@NotNull
	@DecimalMin(value = "0")
	BigDecimal salary;
	
	@NotNull
	Position position;
	
	@NotNull
	@Min(value = 0)
	Integer workExperienceTotal;
	
	@NotNull
	@Min(value = 0)
	Integer workExperienceCurrent;
}
