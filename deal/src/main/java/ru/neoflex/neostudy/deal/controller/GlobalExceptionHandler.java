package ru.neoflex.neostudy.deal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import ru.neoflex.neostudy.common.exception.ExceptionDetails;
import ru.neoflex.neostudy.common.exception.InvalidPassportDataException;
import ru.neoflex.neostudy.common.exception.LoanRefusalException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
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
	
	@ExceptionHandler(LoanRefusalException.class)
	private ResponseEntity<ExceptionDetails> handleLoanRefusalException(LoanRefusalException e, WebRequest request) {
		ExceptionDetails exceptionDetails = new ExceptionDetails(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), request.getDescription(false));
		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(exceptionDetails);
	}
}
