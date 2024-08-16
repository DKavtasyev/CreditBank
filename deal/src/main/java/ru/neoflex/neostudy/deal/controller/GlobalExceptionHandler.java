package ru.neoflex.neostudy.deal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import ru.neoflex.neostudy.common.exception.*;
import ru.neoflex.neostudy.common.exception.dto.ExceptionDetails;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(InvalidPassportDataException.class)
	private ResponseEntity<?> handleInvalidPassportDataException(InvalidPassportDataException e, WebRequest request) {
		ExceptionDetails exceptionDetails = new ExceptionDetails(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getDescription(false));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionDetails);
	}
	
	@ExceptionHandler(SignatureVerificationFailedException.class)
	private ResponseEntity<ExceptionDetails> handleDocumentSigningException(SignatureVerificationFailedException e, WebRequest request) {
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
	
	@ExceptionHandler(UserDocumentException.class)
	private ResponseEntity<ExceptionDetails> handleUnsignedDocumentException(UserDocumentException e, WebRequest request) {
		ExceptionDetails exceptionDetails = new ExceptionDetails(HttpStatus.UNPROCESSABLE_ENTITY.value(), e.getMessage(), request.getDescription(false));
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(exceptionDetails);
	}
	
	@ExceptionHandler(InvalidPreApproveException.class)
	private ResponseEntity<ExceptionDetails> handleInvalidPreApproveException(InvalidPreApproveException e, WebRequest request) {
		ExceptionDetails exceptionDetails = new ExceptionDetails(HttpStatus.PRECONDITION_REQUIRED.value(), e.getMessage(), request.getDescription(false));
		return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).body(exceptionDetails);
	}
	
	@ExceptionHandler(InternalMicroserviceException.class)
	private ResponseEntity<ExceptionDetails> handleLoanRefusalException(InternalMicroserviceException e, WebRequest request) {
		ExceptionDetails exceptionDetails = new ExceptionDetails(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage() + ": " + e.getCause(), request.getDescription(true));
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionDetails);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	private ResponseEntity<?> handleInvalidFinishingRegistrationRequestParametersException(WebRequest request)
	{
		String message = "MS deal: invalid input parameters of FinishingRegistrationRequestDto";
		ExceptionDetails exceptionDetails = new ExceptionDetails(HttpStatus.INTERNAL_SERVER_ERROR.value(), message, request.getDescription(false));
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionDetails);
	}
}
