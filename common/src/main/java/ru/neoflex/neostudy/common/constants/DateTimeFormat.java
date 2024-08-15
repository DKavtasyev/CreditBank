package ru.neoflex.neostudy.common.constants;

import lombok.experimental.UtilityClass;

/**
 * Унифицированный шаблон даты и времени для записи значений в базу данных.
 */
@UtilityClass
public class DateTimeFormat {
	public static final String DATE_PATTERN = "yyyy-MM-dd";
	@SuppressWarnings("SpellCheckingInspection")
	public static final String DATETIME_PATTERN = "yyyy-MM-dd/HH:mm:ss.nnnnnnnnn";
}
