package ru.neoflex.neostudy.common.exception;

public class SendingEmailException extends InternalMicroserviceException {
	public SendingEmailException(String message, Throwable cause) {
		super(message, cause);
	}
}
