package ru.neostudy.neoflex.deal.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckAgeValidator.class)
public @interface CheckAge
{
	int value();
	String message() default "The age must be over 18 years old";
	Class<?>[] groups() default {};
	Class<? extends Payload> [] payload() default {};
}
