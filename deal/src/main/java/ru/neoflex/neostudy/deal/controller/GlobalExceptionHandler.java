package ru.neoflex.neostudy.deal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ru.neoflex.neostudy.deal.exception.*;

@ControllerAdvice
public class GlobalExceptionHandler
{
	@ExceptionHandler(InvalidPassportDataException.class)
	private ResponseEntity<?> handleInvalidPassportDataException(InvalidPassportDataException e, WebRequest request)
	{
		ExceptionDetails exceptionDetails = new ExceptionDetails(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getDescription(false));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionDetails);
	}
	
	@ExceptionHandler(StatementNotFoundException.class)
	private ResponseEntity<?> handleStatementNotFoundException(StatementNotFoundException e, WebRequest request)
	{
		ExceptionDetails exceptionDetails = new ExceptionDetails(HttpStatus.NOT_FOUND.value(), e.getMessage(), request.getDescription(false));
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionDetails);
	}
	
	@ExceptionHandler(InvalidPreScoreParameters.class)
	private ResponseEntity<?> handleInvalidLoanRequestParametersException(InvalidPreScoreParameters e, WebRequest request)
	{
		ExceptionDetails exceptionDetails = new ExceptionDetails(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getDescription(false));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionDetails);
	}
	
	@ExceptionHandler(InvalidScoreParameters.class)
	private ResponseEntity<?> handleInvalidScoreParametersException(InvalidScoreParameters e, WebRequest request)
	{
		ExceptionDetails exceptionDetails = new ExceptionDetails(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getDescription(false));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionDetails);
	}
}
