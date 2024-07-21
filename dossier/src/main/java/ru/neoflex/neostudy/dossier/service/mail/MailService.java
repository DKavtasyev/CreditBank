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
	
	private final Mail.MailBuilder mailBuilder;
	private final ParamsService paramsService;
	
	public void sendFinishRegistrationEmail(EmailMessage emailMessage) throws InternalMicroserviceException {
		var params = paramsService.getParams(emailMessage);
		
		Mail mail = mailBuilder.initializeMail(emailMessage.getAddress(), params)
				.addHtmlPart(MAIL_TEMPLATE, params)
				.addImgPart().build();
		mail.sendMessage();
	}
	
	
	public void sendCreateDocumentsEmail(EmailMessage emailMessage) throws InternalMicroserviceException {
		var params = paramsService.getParams(emailMessage);
		
		Mail mail = mailBuilder.initializeMail(emailMessage.getAddress(), params)
				.addHtmlPart(MAIL_TEMPLATE, params)
				.addImgPart().build();
		mail.sendMessage();
	}
	
	
	public void sendDocumentsEmail(EmailMessage emailMessage) throws InternalMicroserviceException, UserDocumentException {
		var params = paramsService.getParams(emailMessage);
		
		Mail mail = mailBuilder.initializeMail(emailMessage.getAddress(), params)
				.addHtmlPart(MAIL_TEMPLATE, params)
				.addImgPart()
				.addAttachmentPart(emailMessage.getMessage()).build();
		mail.sendMessage();
	}
	
	
	public void sendSesCodeEmail(EmailMessage emailMessage) throws InternalMicroserviceException {
		var params = paramsService.getParams(emailMessage);
		URI uri = UrlFormatter.substituteUrlValue((String)params.get(ParamsService.KEY_URL), emailMessage.getStatementId().toString());
		uri = UrlFormatter.addQueryParameter(uri.toString(), "code", emailMessage.getMessage());
		paramsService.updateUrl(params, uri.toString());
		
		Mail mail = mailBuilder.initializeMail(emailMessage.getAddress(), params)
				.addHtmlPart(MAIL_TEMPLATE, params)
				.addImgPart().build();
		mail.sendMessage();
	}
	
	public void sendCreditIssuedEmail(EmailMessage emailMessage) throws InternalMicroserviceException {
		var params = paramsService.getParams(emailMessage);
		
		Mail mail = mailBuilder.initializeMail(emailMessage.getAddress(), params)
				.addHtmlPart(MAIL_TEMPLATE, params)
				.addImgPart().build();
		mail.sendMessage();
	}
	
	public void sendRejectionOfStatementEmail(EmailMessage emailMessage) throws InternalMicroserviceException {
		var params = paramsService.getParams(emailMessage);
		params.put(ParamsService.KEY_MESSAGE_TEXT, emailMessage.getMessage());
		
		Mail mail = mailBuilder.initializeMail(emailMessage.getAddress(), params)
				.addHtmlPart(MAIL_TEMPLATE, params)
				.addImgPart().build();
		mail.sendMessage();
	}
}
