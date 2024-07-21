package ru.neoflex.neostudy.deal.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class DateFormatter {
	
	@Value("${app.date.pattern}")
	private String pattern;
	
	private DateTimeFormatter dtf;
	
	@PostConstruct
	public void init() {
		dtf = DateTimeFormatter.ofPattern(pattern);
	}
	public String printDate(LocalDate localDate) {
		return dtf.format(localDate);
	}
}
