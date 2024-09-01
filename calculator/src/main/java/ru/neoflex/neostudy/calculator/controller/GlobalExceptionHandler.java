package ru.neoflex.neostudy.calculator.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import ru.neoflex.neostudy.common.exception.dto.ExceptionDetails;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.LoanRefusalException;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(LoanRefusalException.class)
	private ResponseEntity<ExceptionDetails> refuseLoan(LoanRefusalException e, WebRequest request) {
		log.warn(e.getMessage());
		ExceptionDetails exceptionDetails = new ExceptionDetails(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), request.getDescription(false));
		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(exceptionDetails);
	}
	
	@ExceptionHandler(InternalMicroserviceException.class)
	private ResponseEntity<ExceptionDetails> handleInternalMicroserviceException(InternalMicroserviceException e, WebRequest request) {
		log.warn(e.getMessage());
		ExceptionDetails exceptionDetails = new ExceptionDetails(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), request.getDescription(false));
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionDetails);
	}
}
