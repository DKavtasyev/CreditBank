package ru.neoflex.neostudy.deal.service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.constants.Theme;
import ru.neoflex.neostudy.common.dto.EmailMessage;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.deal.entity.Statement;

/**
 * Сервис уровня business services для отправки сообщений по MQ Kafka.
 */
@Service
@RequiredArgsConstructor
public class KafkaService {
	
	private final MessageSender messageSender;
	
	/**
	 * Отправляет сообщение по Kafka, используя {@code MessageSender}.
	 * @param statement объект-entity, содержащий все данные по кредиту.
	 * @param theme элемент enum типа {@code Theme}, означает тему письма.
	 * @param message передающееся сообщение в формате String.
	 * @throws InternalMicroserviceException выбрасывается, если произошла ошибка при отправке сообщения.
	 */
	public void sendKafkaMessage(Statement statement, Theme theme, String message) throws InternalMicroserviceException {
		String email = statement.getClient().getEmail();
		String firstAndMiddleName = statement.getClient().getFirstName() + (statement.getClient().getMiddleName() == null ? "" : " " + statement.getClient().getMiddleName());
		EmailMessage emailMessage = new EmailMessage(email, theme, statement.getStatementId(), message, firstAndMiddleName);
		messageSender.send(theme.getTopicName(), emailMessage);
	}
}
