package ru.neoflex.neostudy.common.exception;

/**
 * Выбрасывается, если заявка Statement с указанным идентификационным номером не найдена в базе данных. Это возможно,
 * если требуемая заявка отсутствует в базе данных, или допущена ошибка при указании значения идентификатора заявки.
 * @author Dmitriy Kavtasyev
 */
public class StatementNotFoundException extends Exception {
	
	public StatementNotFoundException(String message) {
		super(message);
	}
}
