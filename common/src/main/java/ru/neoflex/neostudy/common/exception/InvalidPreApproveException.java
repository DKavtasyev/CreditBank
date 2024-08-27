package ru.neoflex.neostudy.common.exception;

/**
 * Выбрасывается, если предварительное одобрение кредита для данной заявки на кредит отсутствует или недействительно.
 * Сигнализирует пользователю о невозможности продолжения дальнейшего оформления кредита без предварительного одобрения.
 * @author Dmitriy Kavtasyev
 */
public class InvalidPreApproveException extends Exception {
	public InvalidPreApproveException(String message) {
		super(message);
	}
	
	public InvalidPreApproveException(String message, Throwable cause) {
		super(message, cause);
	}
}
