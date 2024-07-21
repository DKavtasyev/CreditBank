package ru.neoflex.neostudy.deal.service.document;

import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.deal.entity.Statement;

public interface DocumentCreator {
	byte[] createDocument(Statement statement) throws InternalMicroserviceException;
}
