package ru.neoflex.neostudy.common.exception;

public class InvalidPreApproveException extends Exception {
	public InvalidPreApproveException(String message) {
		super(message);
	}
	
	public InvalidPreApproveException(String message, Throwable cause) {
		super(message, cause);
	}
}
