package ru.neoflex.neostudy.common.exception;

/**
 * Выбрасывается, когда в программе возникает какая-либо внутренняя техническая ошибка. Возможные варианты:
 * <ul>
 *     <li>Ошибка подключения к микросервису.</li>
 *     <li>Ошибка сериализации/десериализации значения.</li>
 *     <li>Ошибка создания документа.</li>
 *     <li>Ошибка создания или декодирования электронной подписи.</li>
 * </ul>
 * <p>
 *     Приложения выбрасывают это исключение для оповещения о неисправной работе программы.
 * </p>
 * @author Dmitriy Kavtasyev
 */

public class InternalMicroserviceException extends Exception {
	public InternalMicroserviceException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InternalMicroserviceException(String message) {
		super(message);
	}
}
