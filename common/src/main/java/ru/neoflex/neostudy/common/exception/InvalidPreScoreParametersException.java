package ru.neoflex.neostudy.common.exception;

/**
 * Выбрасывается, если данные, предоставленные пользователем, не проходят процедуру прескоринга. Конкретные условия
 * зависят от параметров валидации данных, поступающих с запросом от пользователя, которые указаны в entity-классе.
 * @author Dmitriy Kavtasyev
 */
public class InvalidPreScoreParametersException extends InvalidUserDataException {
	
	public InvalidPreScoreParametersException(String message) {
		super(message);
	}
}
