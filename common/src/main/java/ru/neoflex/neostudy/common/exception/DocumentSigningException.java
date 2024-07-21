package ru.neoflex.neostudy.common.exception;

public class DocumentSigningException extends Exception {
	public DocumentSigningException(String message) {
		super(message);
	}
	
	public DocumentSigningException(String message, Throwable cause) {
		super(message, cause);
	}
}
