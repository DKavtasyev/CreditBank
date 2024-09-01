package ru.neoflex.neostudy.dossier.service.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import ru.neoflex.neostudy.common.constants.Theme;
import ru.neoflex.neostudy.common.dto.EmailMessage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Сервис формирует из полученных данных параметры для выполнения шаблона thymeleaf и формирования html страницы для
 * отправки по email пользователю.
 */
@Service
@RequiredArgsConstructor
public class ParamsService {
	public static final String KEY_SUBJECT = "subject";
	public static final String KEY_CURRENT_DATE = "currentDate";
	public static final String KEY_MESSAGE_TEXT = "messageText";
	public static final String KEY_BUTTON_TEXT = "buttonText";
	public static final String KEY_URL = "url";
	
	@Value("${app.bank.host}")
	private String bankHost;
	@Value("${app.bank.port}")
	private String bankPort;
	
	/**
	 * Формирует и возвращает {@code Map<String, Object>}, содержащую в себе параметры, необходимые для отправки и
	 * формирования email-сообщения.
	 * @param emailMessage объект с данными для email сообщения.
	 * @return {@code Map<String, Object>} с наименованием и значением параметров.
	 */
	public Map<String, Object> getParams(EmailMessage emailMessage) {
		Theme theme = emailMessage.getTheme();
		UriComponentsBuilder builder = theme.getPath();
		
		String url = null;
		if (builder != null){
			if (builder.build().getHost() == null) {
				builder.scheme("http").host(bankHost).port(bankPort);
			}
			url = builder.build(false).toString();
		}
		
		String messageText = String.format(theme.getMessageText(), emailMessage.getFirstAndMiddleName());
		
		Map<String, Object> params = new HashMap<>();
		params.put(KEY_SUBJECT, theme.getSubject());
		params.put(KEY_CURRENT_DATE, LocalDate.now());
		params.put(KEY_MESSAGE_TEXT, messageText);
		params.put(KEY_BUTTON_TEXT, theme.getButtonText());
		params.put(KEY_URL, url);
		return params;
	}
	
	public void updateUrl(Map<String, Object> params, String url) {
		params.put(KEY_URL, url);
	}
}
