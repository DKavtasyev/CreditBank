package ru.neoflex.neostudy.deal.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Утилитный класс для преобразования формата даты.
 */
@Service
public class DateFormatter {
	
	@Value("${app.date.pattern}")
	private String pattern;
	
	private DateTimeFormatter dtf;
	
	@PostConstruct
	public void init() {
		dtf = DateTimeFormatter.ofPattern(pattern);
	}
	
	/**
	 * Возвращает строку с датой, приведённую в удобочитаемый вид формата dd MMMM yyyy для использования в документах.
	 * @param localDate форматируемая дата.
	 * @return строка с датой в формате для документов.
	 */
	public String printDate(LocalDate localDate) {
		return dtf.format(localDate);
	}
}
