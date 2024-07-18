package ru.neoflex.neostudy.dossier.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.constants.Theme;
import ru.neoflex.neostudy.common.dto.EmailMessage;
import ru.neoflex.neostudy.dossier.config.AppConfig;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ParamsService {
	
	private final AppConfig appConfig;
	public Map<String, Object> getParams(EmailMessage emailMessage) {
		Theme theme = emailMessage.getTheme();
		String path = theme.getPath();
		String url = null;
		if (path != null) {
			url = path.contains("ya.ru") ? path : appConfig.getBankUrl() + theme.getPath();
		}
		
		Map<String, Object> params = new HashMap<>();
		params.put("subject", theme.getSubject());
		params.put("currentDate", LocalDate.now());
		params.put("messageText", theme.getMessageText());
		params.put("buttonText", theme.getButtonText());
		params.put("url", url);
		params.put("statementId", emailMessage.getStatementId());
		return params;
	}
	
	public void addDocumentAsText(Map<String, Object> params, String document) {
		String messageText = (String) params.get("messageText");
		params.put("messageText", messageText + "\n\nВаши документы: \n\n" + document);
	}
}
