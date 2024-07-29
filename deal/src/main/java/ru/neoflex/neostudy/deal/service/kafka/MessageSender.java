package ru.neoflex.neostudy.deal.service.kafka;

import ru.neoflex.neostudy.common.dto.EmailMessage;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;

/**
 * Интерфейс, задающий контракт для отправки асинхронного сообщения в микросервис dossier.
 */
public interface MessageSender {
	void send(String topic, EmailMessage emailMessage) throws InternalMicroserviceException;
}
