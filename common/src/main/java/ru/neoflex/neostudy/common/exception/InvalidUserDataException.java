package ru.neoflex.neostudy.common.exception;

/**
 * Выбрасывается в случае некорректных данных от пользователя. Является общим классом для исключений, возникающих по
 * причине некорректных пользовательских данных.
 * @author Dmitriy Kavtasyev
 */
public class InvalidUserDataException extends Exception {
	public InvalidUserDataException(String message) {
		super(message);
	}
}
