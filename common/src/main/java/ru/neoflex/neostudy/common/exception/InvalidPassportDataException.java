package ru.neoflex.neostudy.common.exception;

/**
 * Выбрасывается, когда пользователь не проходит идентификацию по паспортным данным. Означает, что данные, указанные
 * пользователем, не совпадают с данными того же пользователя, имеющегося в базе данных. Выбрасывается, если, при
 * совпадении серии и номера паспорта, не совпадает хотя бы один параметр из перечисленных:
 * <ul>
 *     <li>Фамилия.</li>
 *     <li>Имя.</li>
 *     <li>Отчество.</li>
 *     <li>Дата рождения.</li>
 * </ul>
 * @author Dmitriy Kavtasyev
 */
public class InvalidPassportDataException extends InvalidUserDataException {
	
	public InvalidPassportDataException(String message) {
		super(message);
	}
}
