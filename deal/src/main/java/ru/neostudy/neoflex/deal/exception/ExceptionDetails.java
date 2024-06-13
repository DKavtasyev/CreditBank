package ru.neostudy.neoflex.deal.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ExceptionDetails
{
	private int statusCode;
	private String message;
	private String details;
}
