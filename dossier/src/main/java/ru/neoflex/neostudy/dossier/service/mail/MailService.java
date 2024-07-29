package ru.neoflex.neostudy.dossier.service.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.dto.EmailMessage;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.UserDocumentException;
import ru.neoflex.neostudy.common.util.UrlFormatter;

import java.net.URI;

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
	 * Формирует и отправляет пользователю email сообщение с предложением закончить начатое оформление кредита.
	 * @param emailMessage объект с данными для email сообщения.
	 * @throws InternalMicroserviceException если произошла ошибка при отправке или формировании email-сообщения.
	 */
	public void sendFinishRegistrationEmail(EmailMessage emailMessage) throws InternalMicroserviceException {
		sendEmail(emailMessage);
	}
	
	/**
	 * Формирует и отправляет пользователю email сообщение с предложением создать документы на кредит.
	 * @param emailMessage объект с данными для email сообщения.
	 * @throws InternalMicroserviceException если произошла ошибка при отправке или формировании email-сообщения.
	 */
	public void sendCreateDocumentsEmail(EmailMessage emailMessage) throws InternalMicroserviceException {
		sendEmail(emailMessage);
	}
	
	/**
	 * Формирует и отправляет пользователю email сообщение с документами на кредит.
	 * @param emailMessage объект с данными для email сообщения.
	 * @throws InternalMicroserviceException если произошла ошибка при отправке или формировании email-сообщения.
	 * @throws UserDocumentException если произошла ошибка при добавлении вложения к email-сообщению.
	 */
	public void sendDocumentsEmail(EmailMessage emailMessage) throws InternalMicroserviceException, UserDocumentException {
		var params = paramsService.getParams(emailMessage);
		
		Mail mail = mailBuilder.initializeMail(emailMessage.getAddress(), params)
				.addHtmlPart(MAIL_TEMPLATE, params)
				.addImgPart(IMG_PATH, CONTENT_ID)
				.addAttachmentPart(emailMessage.getMessage(), "document.pdf").build();
		mail.sendMessage();
	}
	
	/**
	 * Формирует и отправляет пользователю email сообщение с кодом подписания документов.
	 * @param emailMessage объект с данными для email сообщения.
	 * @throws InternalMicroserviceException если произошла ошибка при отправке или формировании email-сообщения.
	 */
	public void sendSesCodeEmail(EmailMessage emailMessage) throws InternalMicroserviceException {
		var params = paramsService.getParams(emailMessage);
		URI uri = UrlFormatter.substituteUrlValue((String)params.get(ParamsService.KEY_URL), emailMessage.getStatementId().toString());
		uri = UrlFormatter.addQueryParameter(uri.toString(), "code", emailMessage.getMessage());
		paramsService.updateUrl(params, uri.toString());
		
		Mail mail = mailBuilder.initializeMail(emailMessage.getAddress(), params)
				.addHtmlPart(MAIL_TEMPLATE, params)
				.addImgPart(IMG_PATH, CONTENT_ID).build();
		mail.sendMessage();
	}
	
	/**
	 * Формирует и отправляет пользователю email сообщение с уведомлением о выпущенном кредите.
	 * @param emailMessage объект с данными для email сообщения.
	 * @throws InternalMicroserviceException если произошла ошибка при отправке или формировании email-сообщения.
	 */
	public void sendCreditIssuedEmail(EmailMessage emailMessage) throws InternalMicroserviceException {
		sendEmail(emailMessage);
	}
	
	/**
	 * Формирует и отправляет пользователю email сообщение об отмене кредита по какой-либо причине.
	 * @param emailMessage объект с данными для email сообщения.
	 * @throws InternalMicroserviceException если произошла ошибка при отправке или формировании email-сообщения.
	 */
	public void sendRejectionOfStatementEmail(EmailMessage emailMessage) throws InternalMicroserviceException {
		var params = paramsService.getParams(emailMessage);
		params.put(ParamsService.KEY_MESSAGE_TEXT, emailMessage.getMessage());
		
		Mail mail = mailBuilder.initializeMail(emailMessage.getAddress(), params)
				.addHtmlPart(MAIL_TEMPLATE, params)
				.addImgPart(IMG_PATH, CONTENT_ID).build();
		mail.sendMessage();
	}
	
	/**
	 * Формирует и осуществляет отправку email-сообщения.
	 * @param emailMessage объект с данными для email сообщения.
	 * @throws InternalMicroserviceException если произошла ошибка при отправке или формировании email-сообщения.
	 */
	private void sendEmail(EmailMessage emailMessage) throws InternalMicroserviceException {
		var params = paramsService.getParams(emailMessage);
		
		Mail mail = mailBuilder.initializeMail(emailMessage.getAddress(), params)
				.addHtmlPart(MAIL_TEMPLATE, params)
				.addImgPart(IMG_PATH, CONTENT_ID).build();
		mail.sendMessage();
	}
}
