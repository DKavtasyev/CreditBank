package ru.neoflex.neostudy.common.exception;

/**
 * Выбрасывается в случае возникновения ошибки при отправке электронного письма пользователю. Может возникнуть при
 * следующих ошибках:
 * <ul>
 *     <li>Ошибка подключения к почтовому серверу.</li>
 *     <li>Ошибка аутентификации на почтовом сервере.</li>
 *     <li>Неверные параметры конфигурации.</li>
 *     <li>Проблемы с почтовым протоколом.</li>
 * </ul>
 * @author Dmitriy Kavtasyev
 */
public class SendingEmailException extends InternalMicroserviceException {
	public SendingEmailException(String message, Throwable cause) {
		super(message, cause);
	}
}
