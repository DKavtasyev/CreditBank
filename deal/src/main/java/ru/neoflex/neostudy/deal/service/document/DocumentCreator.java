package ru.neoflex.neostudy.deal.service.document;

import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.UserDocumentException;
import ru.neoflex.neostudy.deal.entity.Statement;

/**
 * Интерфейс, задающий контракт для создания документа на кредит.
 */
public interface DocumentCreator {
	byte[] createDocument(Statement statement) throws InternalMicroserviceException, UserDocumentException;
}
