package ru.neoflex.neostudy.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Используется для передачи пользователю по REST-протоколу информации о возникшем исключении.
 * @author Dmitriy Kavtasyev
 */
@AllArgsConstructor
@Getter
@Setter
public class ExceptionDetails {
	private int statusCode;
	private String message;
	private String details;
}
