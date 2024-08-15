package ru.neoflex.neostudy.deal.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.dto.EmailMessage;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;

/**
 * Сервис уровня business objects для отправки сообщений по MQ Kafka.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class MessageSenderKafka implements MessageSender {

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;
	
	/**
	 * Отправляет сообщение с помощью Kafka.
	 * @param topic топик, в который будет отправлено сообщение.
	 * @param emailMessage тело сообщения.
	 * @throws InternalMicroserviceException выбрасывается, если произошла ошибка при отправке сообщения.
	 */
	@Override
	public void send(String topic, EmailMessage emailMessage) throws InternalMicroserviceException {
		String messageAsString = null;
		try {
			messageAsString = objectMapper.writeValueAsString(emailMessage);
			String finalMessageAsString = messageAsString;
			kafkaTemplate.send(topic, messageAsString)
					.whenComplete((result, exception) -> {
						if (exception == null) {
							log.info("Message {} was sent, offset: {}", finalMessageAsString, result.getRecordMetadata().offset());
						}
						else {
							log.error("Message {} was not sent", finalMessageAsString, exception);
						}
					});
		}
		catch (JsonProcessingException e) {
			throw new InternalMicroserviceException("Can't serialize message: " + emailMessage, e);
		}
		catch (Exception e) {
			throw new InternalMicroserviceException(String.format("Message %s was not sent", messageAsString), e);
		}
		
	}
}
