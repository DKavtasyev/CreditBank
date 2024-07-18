package ru.neoflex.neostudy.dossier.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.dto.EmailMessage;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.dossier.mail.MailService;
import ru.neoflex.neostudy.dossier.requester.DealRequester;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {
	private final MailService mailService;
	private final ParamsService paramsService;
	private final DealRequester dealRequester;
	
	
	public static final String FINISH_REGISTRATION_TOPIC = "finish-registration";
	public static final String CREATE_DOCUMENTS_TOPIC = "create-documents";
	public static final String SEND_DOCUMENTS_TOPIC = "send-documents";
	public static final String SEND_SES_TOPIC = "send-ses";
	public static final String CREDIT_ISSUED_TOPIC = "credit-issued";
	public static final String STATEMENT_DENIED_TOPIC = "statement-denied";
	public static final String MAIL_TEMPLATE_TOPIC = "email-message.html";
	
	@KafkaListener(topics = FINISH_REGISTRATION_TOPIC, containerFactory = "listenerContainerFactory")
	public void finishRegistrationListen(@Payload EmailMessage emailMessage) {
		var params = paramsService.getParams(emailMessage);
		mailService.sendAdvancedEmail(emailMessage.getAddress(), MAIL_TEMPLATE_TOPIC, params);
	}
	
	@KafkaListener(topics = CREATE_DOCUMENTS_TOPIC, containerFactory = "listenerContainerFactory")
	public void createDocumentsListen(@Payload EmailMessage emailMessage) {
		var params = paramsService.getParams(emailMessage);
		mailService.sendAdvancedEmail(emailMessage.getAddress(), MAIL_TEMPLATE_TOPIC, params);
	}
	
	@KafkaListener(topics = SEND_DOCUMENTS_TOPIC, containerFactory = "listenerContainerFactory")
	public void sendDocumentsListen(@Payload EmailMessage emailMessage) throws StatementNotFoundException, InternalMicroserviceException, JsonProcessingException {
		String documents = emailMessage.getMessage();
		dealRequester.sendStatementStatus(emailMessage.getStatementId(), ApplicationStatus.DOCUMENT_CREATED);
		var params = paramsService.getParams(emailMessage);
		paramsService.addDocumentAsText(params, documents);
		mailService.sendAdvancedEmail(emailMessage.getAddress(), MAIL_TEMPLATE_TOPIC, params);
	}
	
	@KafkaListener(topics = SEND_SES_TOPIC, containerFactory = "listenerContainerFactory")
	public void sendSesListen(@Payload EmailMessage emailMessage) {
		var params = paramsService.getParams(emailMessage);
		String[] array = emailMessage.getAddress().split(" ");
		String address = array[0];
		String sesCode = array[1];
		params.put("url", params.get("url") + "+" + sesCode);
		mailService.sendAdvancedEmail(address, MAIL_TEMPLATE_TOPIC, params);
	}
	
	@KafkaListener(topics = CREDIT_ISSUED_TOPIC, containerFactory = "listenerContainerFactory")
	public void creditIssuedListen(@Payload EmailMessage emailMessage) {
		var params = paramsService.getParams(emailMessage);
		mailService.sendAdvancedEmail(emailMessage.getAddress(), MAIL_TEMPLATE_TOPIC, params);
	}
	
	@KafkaListener(topics = STATEMENT_DENIED_TOPIC, containerFactory = "listenerContainerFactory")
	public void statementDeniedListen(@Payload EmailMessage emailMessage) {
		var params = paramsService.getParams(emailMessage);
		mailService.sendAdvancedEmail(emailMessage.getAddress(), MAIL_TEMPLATE_TOPIC, params);
	}
}
