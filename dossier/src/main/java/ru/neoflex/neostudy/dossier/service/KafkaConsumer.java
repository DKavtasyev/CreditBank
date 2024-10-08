package ru.neoflex.neostudy.dossier.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.dto.EmailMessage;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.common.exception.UserDocumentException;
import ru.neoflex.neostudy.dossier.requester.DealRequester;
import ru.neoflex.neostudy.dossier.service.mail.MailService;


@Service
@RequiredArgsConstructor
public class KafkaConsumer {
	private final MailService mailService;
	private final DealRequester dealRequester;
	
	public static final String FINISH_REGISTRATION_TOPIC = "finish-registration";
	public static final String CREATE_DOCUMENTS_TOPIC = "create-documents";
	public static final String SEND_DOCUMENTS_TOPIC = "send-documents";
	public static final String SEND_SES_TOPIC = "send-ses";
	public static final String CREDIT_ISSUED_TOPIC = "credit-issued";
	public static final String STATEMENT_DENIED_TOPIC = "statement-denied";
	public static final String CLIENT_REJECTION_TOPIC = "client-rejection";
	
	@KafkaListener(topics = FINISH_REGISTRATION_TOPIC, containerFactory = "listenerContainerFactory")
	public void finishRegistrationListen(@Payload EmailMessage emailMessage, Acknowledgment acknowledgment) throws InternalMicroserviceException {
		mailService.sendEmail(emailMessage);
		acknowledgment.acknowledge();
	}
	
	@KafkaListener(topics = CREATE_DOCUMENTS_TOPIC, containerFactory = "listenerContainerFactory")
	public void createDocumentsListen(@Payload EmailMessage emailMessage, Acknowledgment acknowledgment) throws InternalMicroserviceException {
		mailService.sendEmail(emailMessage);
		acknowledgment.acknowledge();
	}
	
	@KafkaListener(topics = SEND_DOCUMENTS_TOPIC, containerFactory = "listenerContainerFactory")
	public void sendDocumentsListen(@Payload EmailMessage emailMessage, Acknowledgment acknowledgment) throws StatementNotFoundException, InternalMicroserviceException, UserDocumentException {
		dealRequester.sendStatementStatus(emailMessage.getStatementId(), ApplicationStatus.DOCUMENT_CREATED);
		mailService.sendEmail(emailMessage, "contract.pdf");
		acknowledgment.acknowledge();
	}
	
	@KafkaListener(topics = SEND_SES_TOPIC, containerFactory = "listenerContainerFactory")
	public void sendSesListen(@Payload EmailMessage emailMessage, Acknowledgment acknowledgment) throws InternalMicroserviceException {
		mailService.sendEmail(emailMessage, "code", emailMessage.getMessage());
		acknowledgment.acknowledge();
	}
	
	@KafkaListener(topics = CREDIT_ISSUED_TOPIC, containerFactory = "listenerContainerFactory")
	public void creditIssuedListen(@Payload EmailMessage emailMessage, Acknowledgment acknowledgment) throws InternalMicroserviceException {
		mailService.sendEmail(emailMessage);
		acknowledgment.acknowledge();
	}
	
	@KafkaListener(topics = STATEMENT_DENIED_TOPIC, containerFactory = "listenerContainerFactory")
	public void statementDeniedListen(@Payload EmailMessage emailMessage, Acknowledgment acknowledgment) throws InternalMicroserviceException {
		mailService.sendEmail(emailMessage);
		acknowledgment.acknowledge();
	}
	
	@KafkaListener(topics = CLIENT_REJECTION_TOPIC, containerFactory = "listenerContainerFactory")
	public void clientRejectionListen(@Payload EmailMessage emailMessage, Acknowledgment acknowledgment) throws InternalMicroserviceException {
		mailService.sendEmail(emailMessage);
		acknowledgment.acknowledge();
	}
}
