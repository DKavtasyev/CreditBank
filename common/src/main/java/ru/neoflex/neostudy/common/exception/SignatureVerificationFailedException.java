package ru.neoflex.neostudy.common.exception;

/**
 * Выбрасывается, когда проверка подписи документа на подлинность заканчивается неудачно. Это может произойти в
 * следующих случаях:
 * <ul>
 *     <li>Подписываемый документ был изменён.</li>
 *     <li>Подпись имеет некорректный формат или недействительна.</li>
 * </ul>
 * @author Dmitriy Kavtasyev
 */
public class SignatureVerificationFailedException extends Exception {
	public SignatureVerificationFailedException(String message) {
		super(message);
	}
	
	public SignatureVerificationFailedException(String message, Throwable cause) {
		super(message, cause);
	}
}
