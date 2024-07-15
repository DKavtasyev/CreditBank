package ru.neoflex.neostudy.deal.service;

import ru.neoflex.neostudy.common.dto.EmailMessage;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;

public interface MessageSender {
	void send(String topic, EmailMessage emailMessage) throws InternalMicroserviceException;
}
