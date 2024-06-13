package ru.neoflex.neostudy.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class CheckAgeValidator implements ConstraintValidator<CheckAge, LocalDate>
{
	int requiredAge;
	
	@Override
	public void initialize(CheckAge checkAge)
	{
		requiredAge = checkAge.value();
		ConstraintValidator.super.initialize(checkAge);
	}
	
	@Override
	public boolean isValid(LocalDate birthdate, ConstraintValidatorContext constraintValidatorContext)
	{
		if (birthdate == null)
			return true;
		return birthdate.isBefore(LocalDate.now().minusYears(requiredAge));
	}
}
