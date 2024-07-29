package ru.neoflex.neostudy.common.exception;

/**
 * Выбрасывается в случае отказа банка в предоставлении пользователю кредита. Условия отказа вычисляются в бизнес-логике
 * микросервиса calculator при вычислении персональной процентной ставки по кредиту, используя параметры из файла
 * конфигурации.
 * @author Dmitriy Kavtasyev
 */
public class LoanRefusalException extends Exception {
	public LoanRefusalException(String message) {
		super(message);
	}
}
