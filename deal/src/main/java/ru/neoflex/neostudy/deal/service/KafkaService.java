package ru.neoflex.neostudy.deal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.constants.Theme;
import ru.neoflex.neostudy.common.dto.EmailMessage;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.entity.sign.SignData;
import ru.neoflex.neostudy.deal.repository.StatementRepository;

import java.util.UUID;

import static ru.neoflex.neostudy.common.constants.Theme.*;

@Service
@RequiredArgsConstructor
public class KafkaService {
	
	private final MessageSender messageSender;
	private final StatementRepository statementRepository;
	
	public void sendFinishRegistrationRequest(UUID statementId) throws StatementNotFoundException, InternalMicroserviceException {
		String email = statementRepository.getClientEmailByStatementId(statementId).orElseThrow(() ->
				new StatementNotFoundException(String.format("Statement with id = %s not found or corresponding client is invalid", statementId)));
		EmailMessage emailMessage = new EmailMessage(email, FINISH_REGISTRATION, statementId);
		messageSender.send(FINISH_REGISTRATION.getTopicName(), emailMessage);
	}
	
	public void sendCreatingDocumentsRequest(Statement statement) throws InternalMicroserviceException {
		sendMessage(statement, CREATE_DOCUMENTS);
	}
	
	public void sendDenial(Statement statement) throws InternalMicroserviceException {
		sendMessage(statement, STATEMENT_DENIED);
	}
	
	
	private void sendMessage(Statement statement, Theme theme) throws InternalMicroserviceException {
		String email = statement.getClient().getEmail();
		EmailMessage emailMessage = new EmailMessage(email, theme, statement.getStatementId());
		messageSender.send(theme.getTopicName(), emailMessage);
	}
	
	public void sendDocumentSigningRequest(Statement statement) throws InternalMicroserviceException {
		sendMessage(statement, SEND_DOCUMENTS);
	}
	
	public void sendSignature(SignData signData) throws InternalMicroserviceException {
		Statement statement = signData.getStatement();
		String email = statement.getClient().getEmail() + " " + signData.getToken();
		statement.getClient().setEmail(email);
		sendMessage(statement, SEND_SES);
	}
	
	public void sendCreditIssuedMessage(Statement statement) throws InternalMicroserviceException {
		sendMessage(statement, CREDIT_ISSUED);
	}
}
