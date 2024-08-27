package ru.neoflex.neostudy.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Помечает поле в формате даты, для значения которого должно быть проверено выполнение условия с помощью класса-валидатора.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckAgeValidator.class)
public @interface CheckAge {
	
	/**
	 * Возвращает числовое значение, относительно которого проверяется выполнение условия валидации.
	 * @return значение, относительно которого проверяется условие.
	 */
	int value();
	
	/**
	 * Возвращает сообщение о невыполнении условия валидации.
	 * @return сообщение в формате String.
	 */
	String message() default "The age must be over 18 years old";
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default {};
}
