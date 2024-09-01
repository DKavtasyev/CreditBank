package ru.neoflex.neostudy.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

/**
 * Класс-валидатор, проверяющий количество полных лет, прошедших с момента указанной даты до настоящего момента.
 */
public class CheckAgeValidator implements ConstraintValidator<CheckAge, LocalDate> {
	int requiredAge;
	
	@Override
	public void initialize(CheckAge checkAge) {
		requiredAge = checkAge.value();
		ConstraintValidator.super.initialize(checkAge);
	}
	
	/**
	 * Возвращает {@code true}, если количество полных лет, прошедших от момента даты, которая указана в поле помеченном
	 * аннотацией {@code @CheckAge}, до настоящего момента, больше или равно значению value, указанному в параметре
	 * аннотации {@code @CheckAge}
	 * Возвращает {@code false}, если количество полных лет меньше значения value, указанного в параметре аннотации
	 * {@code @CheckAge}.
	 * <p>Если указанная дата отстоит от настоящего момента ровно на то количество лет, которое указано в параметрах
	 * аннотации, то условие считается выполненным и метод возвращает {@code true}.</p>
	 * @param date - аннотируемое поле, содержащее значение даты.
	 * @return булевое значение, обозначающее выполнение или невыполнение условия валидации.
	 */
	@Override
	public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {
		if (date == null) {
			return true;
		}
		return date.isBefore(LocalDate.now().minusYears(requiredAge).plusDays(1));
	}
}
