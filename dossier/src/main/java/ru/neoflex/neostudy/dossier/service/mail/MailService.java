package ru.neoflex.neostudy.dossier.service.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import ru.neoflex.neostudy.common.dto.EmailMessage;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.UserDocumentException;
import ru.neoflex.neostudy.common.util.UrlBuilder;

import java.net.URI;
import java.util.Map;

/**
 * Сервис для формирования и отправки пользователю информации по электронной почте.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class MailService {
	public static final String MAIL_TEMPLATE = "email-message.html";
	public static final String IMG_PATH = "static/img/logo.png";
	public static final String CONTENT_ID = "<logo@dossier>";
	
	private final Mail.MailBuilder mailBuilder;
	private final ParamsService paramsService;
	
	/**
	 * Формирует и осуществляет отправку email-сообщения.
	 * @param emailMessage объект с данными для email сообщения.
	 * @throws InternalMicroserviceException возникла ошибка при формировании или отправке email-сообщения.
	 */
	public void sendEmail(EmailMessage emailMessage) throws InternalMicroserviceException {
		var params = paramsService.getParams(emailMessage);
		
		Mail mail = mailBuilder.initializeMail(emailMessage.getAddress(), params)
				.addHtmlPart(MAIL_TEMPLATE, params)
				.addImgPart(IMG_PATH, CONTENT_ID).build();
		mail.sendMessage();
	}
	
	/**
	 * Формирует и отправляет email-сообщение с прикреплённым документом. Документ должен быть представлен в виде
	 * строки, зашифрованной в Base64, и передан через поле message объекта emailMessage.
	 * @param emailMessage объект с данными для email сообщения, включая зашифрованный документ.
	 * @param fileName     имя прикладываемого файла.
	 * @throws InternalMicroserviceException возникла ошибка при формировании или отправке email-сообщения.
	 * @throws UserDocumentException         если произошла ошибка при загрузке документа.
	 */
	public void sendEmail(EmailMessage emailMessage, String fileName) throws InternalMicroserviceException, UserDocumentException {
		var params = paramsService.getParams(emailMessage);
		
		Mail mail = mailBuilder.initializeMail(emailMessage.getAddress(), params)
				.addHtmlPart(MAIL_TEMPLATE, params)
				.addImgPart(IMG_PATH, CONTENT_ID)
				.addAttachmentPart(emailMessage.getMessage(), fileName).build();
		mail.sendMessage();
	}
	
	/**
	 * Формирует и отправляет email-сообщение, добавляя к URL-пути в поле theme объекта emailMessage указанный параметр
	 * запроса.
	 * @param emailMessage    объект с данными для email сообщения.
	 * @param queryParamName  имя параметра запроса, который необходимо добавить к URL.
	 * @param queryParamValue значение параметра запроса.
	 * @throws InternalMicroserviceException возникла ошибка при формировании или отправке email-сообщения.
	 */
	public void sendEmail(EmailMessage emailMessage, String queryParamName, String queryParamValue) throws InternalMicroserviceException {
		var params = paramsService.getParams(emailMessage);
		
		UriComponentsBuilder componentsBuilder = UriComponentsBuilder.fromUriString((String) params.get(ParamsService.KEY_URL));
		componentsBuilder.uriVariables(Map.of("statementId", emailMessage.getStatementId().toString()));
		URI uri = UrlBuilder.builder()
				.init(componentsBuilder)
				.addQueryParameter(queryParamName, queryParamValue)
				.build();
		
		paramsService.updateUrl(params, uri.toString());
		
		Mail mail = mailBuilder.initializeMail(emailMessage.getAddress(), params)
				.addHtmlPart(MAIL_TEMPLATE, params)
				.addImgPart(IMG_PATH, CONTENT_ID).build();
		mail.sendMessage();
	}
}
