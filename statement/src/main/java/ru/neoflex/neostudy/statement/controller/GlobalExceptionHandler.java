package ru.neoflex.neostudy.statement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import ru.neoflex.neostudy.common.exception.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(InvalidPreScoreParametersException.class)
	private ResponseEntity<?> handleInvalidLoanRequestParametersException(InvalidPreScoreParametersException e, WebRequest request) {
		ExceptionDetails exceptionDetails = new ExceptionDetails(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getDescription(false));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionDetails);
	}
	
	@ExceptionHandler(InvalidPassportDataException.class)
	private ResponseEntity<?> handleInvalidPassportDataException(InvalidPassportDataException e, WebRequest request) {
		ExceptionDetails exceptionDetails = new ExceptionDetails(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getDescription(false));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionDetails);
	}
	
	@ExceptionHandler(StatementNotFoundException.class)
	private ResponseEntity<?> handleStatementNotFoundException(StatementNotFoundException e, WebRequest request) {
		ExceptionDetails exceptionDetails = new ExceptionDetails(HttpStatus.NOT_FOUND.value(), e.getMessage(), request.getDescription(false));
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionDetails);
	}
	
	@ExceptionHandler(InternalMicroserviceException.class)
	private ResponseEntity<ExceptionDetails> handleLoanRefusalException(InternalMicroserviceException e, WebRequest request) {
		ExceptionDetails exceptionDetails = new ExceptionDetails(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage() + ": " + e.getCause(), request.getDescription(true));
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionDetails);
	}
}
