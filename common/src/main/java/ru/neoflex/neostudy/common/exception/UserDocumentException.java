package ru.neoflex.neostudy.common.exception;

/**
 * Выбрасывается, если пользовательский документ на кредит отсутствует или повреждён.
 * @author Dmitriy Kavtasyev
 */
public class UserDocumentException extends Exception {
	public UserDocumentException(String message) {
		super(message);
	}
	
	public UserDocumentException(String message, Throwable cause) {
		super(message, cause);
	}
}
