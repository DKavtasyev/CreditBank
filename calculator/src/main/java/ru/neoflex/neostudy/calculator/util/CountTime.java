package ru.neoflex.neostudy.calculator.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Утилитный класс для расчёта временных параметров.
 */
@UtilityClass
public class CountTime {
	
	/**
	 * Высчитывает и возвращает количество полных лет, прошедших с переданной даты до настоящего момента.
	 * @param date дата, с которой считается количество лет
	 * @return количество полных лет в формате int.
	 */
	public static int countAge(LocalDate date) {
		return (int) ChronoUnit.YEARS.between(date, LocalDate.now());
	}
	
	/**
	 * Высчитывает и возвращает количество календарных дней между двумя переданными датами.
	 * @param startDate начальная дата.
	 * @param finishDate конечная дата.
	 * @return количество дней в формате int.
	 */
	public static int countDays(LocalDate startDate, LocalDate finishDate) {
		return (int) ChronoUnit.DAYS.between(startDate, finishDate);
	}
}
