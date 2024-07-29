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
	
	/**
	 * Отсылает с помощью Kafka сообщение с предложением закончить начатое оформление кредита.
	 * @param statementId идентификатор заявки пользователя {@code Statement}.
	 * @throws StatementNotFoundException выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 * не найден в базе данных.
	 * @throws InternalMicroserviceException выбрасывается, если произошла ошибка при отправке сообщения.
	 */
	public void sendFinishRegistrationRequest(UUID statementId) throws StatementNotFoundException, InternalMicroserviceException {
		String emailTo = getEmailTo(statementId);
		EmailMessage emailMessage = new EmailMessage(emailTo, FINISH_REGISTRATION, statementId, null);
		messageSender.send(FINISH_REGISTRATION.getTopicName(), emailMessage);
	}
	
	/**
	 * Отсылает с помощью Kafka сообщение с предложением создать документы на кредит.
	 * @param statement объект-entity, содержащий все данные по кредиту.
	 * @throws InternalMicroserviceException выбрасывается, если произошла ошибка при отправке сообщения.
	 */
	public void sendCreatingDocumentsRequest(Statement statement) throws InternalMicroserviceException {
		sendMessage(statement, CREATE_DOCUMENTS, null);
	}
	
	/**
	 * Отсылает с помощью Kafka сообщение об отмене кредита по какой-либо причине.
	 * @param statement объект-entity, содержащий все данные по кредиту.
	 * @param message сообщение пользователю, содержащее детали отмены кредита.
	 * @throws InternalMicroserviceException выбрасывается, если произошла ошибка при отправке сообщения.
	 */
	public void sendDenial(Statement statement, String message) throws InternalMicroserviceException {
		sendMessage(statement, STATEMENT_DENIED, message);
	}
	
	/**
	 * Отсылает с помощью Kafka сообщение с документами на кредит.
	 * @param statement объект-entity, содержащий все данные по кредиту.
	 * @param documents файл документа, закодированный по Base64.
	 * @throws InternalMicroserviceException выбрасывается, если произошла ошибка при отправке сообщения.
	 */
	public void sendDocumentSigningRequest(Statement statement, String documents) throws InternalMicroserviceException {
		sendMessage(statement, SEND_DOCUMENTS, documents);
	}
	
	/**
	 * Отсылает с помощью Kafka сообщение с кодом подписания документов.
	 * @param statementId идентификатор заявки пользователя {@code Statement}.
	 * @param signature подпись в формате {@code String}.
	 * @throws InternalMicroserviceException выбрасывается, если произошла ошибка при отправке сообщения.
	 * @throws StatementNotFoundException выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 * не найден в базе данных.
	 */
	public void sendSignature(UUID statementId, String signature) throws InternalMicroserviceException, StatementNotFoundException {
		String emailTo = getEmailTo(statementId);
		EmailMessage emailMessage = new EmailMessage(emailTo, SEND_SES, statementId, signature);
		messageSender.send(SEND_SES.getTopicName(), emailMessage);
	}
	
	/**
	 * Отсылает с помощью Kafka сообщение с уведомлением о выпущенном кредите.
	 * @param statement объект-entity, содержащий все данные по кредиту.
	 * @throws InternalMicroserviceException выбрасывается, если произошла ошибка при отправке сообщения.
	 */
	public void sendCreditIssuedMessage(Statement statement) throws InternalMicroserviceException {
		sendMessage(statement, CREDIT_ISSUED, null);
	}
	
	/**
	 * Возвращает email, вычитанный из базы данных у пользователя, которому принадлежит заявка {@code Statement},
	 * имеющая идентификатор statementId.
	 * @param statementId идентификатор заявки пользователя {@code Statement}.
	 * @return email пользователя из базы данных, которому принадлежит заявка {@code Statement} с идентификатором
	 * statementId.
	 * @throws StatementNotFoundException выбрасывается, если {@code Statement} с указанным statementId не найден в базе
	 * данных.
	 */
	private String getEmailTo(UUID statementId) throws StatementNotFoundException {
		return statementRepository.getClientEmailByStatementId(statementId).orElseThrow(() ->
				new StatementNotFoundException(String.format("Statement with id = %s not found or corresponding client is invalid", statementId)));
	}
	
	/**
	 * Отправляет сообщение по Kafka, используя {@code MessageSender}.
	 * @param statement объект-entity, содержащий все данные по кредиту.
	 * @param theme элемент enum типа {@code Theme}, означает тему письма.
	 * @param message передающееся сообщение в формате String.
	 * @throws InternalMicroserviceException выбрасывается, если произошла ошибка при отправке сообщения.
	 */
	private void sendMessage(Statement statement, Theme theme, String message) throws InternalMicroserviceException {
		String email = statement.getClient().getEmail();
		EmailMessage emailMessage = new EmailMessage(email, theme, statement.getStatementId(), message);
		messageSender.send(theme.getTopicName(), emailMessage);
	}
}
