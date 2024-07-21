package ru.neoflex.neostudy.deal.service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.constants.Theme;
import ru.neoflex.neostudy.common.dto.EmailMessage;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.repository.StatementRepository;

import java.util.UUID;

import static ru.neoflex.neostudy.common.constants.Theme.*;

@Service
@RequiredArgsConstructor
public class KafkaService {
	
	private final MessageSender messageSender;
	private final StatementRepository statementRepository;
	
	public void sendFinishRegistrationRequest(UUID statementId) throws StatementNotFoundException, InternalMicroserviceException {
		String emailTo = getEmailTo(statementId);
		EmailMessage emailMessage = new EmailMessage(emailTo, FINISH_REGISTRATION, statementId, null);
		messageSender.send(FINISH_REGISTRATION.getTopicName(), emailMessage);
	}
	
	public void sendCreatingDocumentsRequest(Statement statement) throws InternalMicroserviceException {
		sendMessage(statement, CREATE_DOCUMENTS, null);
	}
	
	public void sendDenial(Statement statement, String message) throws InternalMicroserviceException {
		sendMessage(statement, STATEMENT_DENIED, message);
	}
	
	public void sendDocumentSigningRequest(Statement statement, String documents) throws InternalMicroserviceException {
		sendMessage(statement, SEND_DOCUMENTS, documents);
	}
	
	public void sendSignature(UUID statementId, String signature) throws InternalMicroserviceException, StatementNotFoundException {
		String emailTo = getEmailTo(statementId);
		EmailMessage emailMessage = new EmailMessage(emailTo, SEND_SES, statementId, signature);
		messageSender.send(SEND_SES.getTopicName(), emailMessage);
	}
	
	public void sendCreditIssuedMessage(Statement statement) throws InternalMicroserviceException {
		sendMessage(statement, CREDIT_ISSUED, null);
	}
	
	private String getEmailTo(UUID statementId) throws StatementNotFoundException {
		return statementRepository.getClientEmailByStatementId(statementId).orElseThrow(() ->
				new StatementNotFoundException(String.format("Statement with id = %s not found or corresponding client is invalid", statementId)));
	}
	
	private void sendMessage(Statement statement, Theme theme, String message) throws InternalMicroserviceException {
		String email = statement.getClient().getEmail();
		EmailMessage emailMessage = new EmailMessage(email, theme, statement.getStatementId(), message);
		messageSender.send(theme.getTopicName(), emailMessage);
	}
}
