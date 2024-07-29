package ru.neoflex.neostudy.deal.service.signature;

import ru.neoflex.neostudy.common.exception.SignatureVerificationFailedException;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.DocumentSignatureException;
import ru.neoflex.neostudy.deal.entity.Statement;

/**
 * Интерфейс, являющийся общим и задающий контракт для сервисов по работе с подписями.
 */
public interface SignatureService {
	String createSignature() throws InternalMicroserviceException;
	void signDocument(Statement statement, String signature) throws InternalMicroserviceException, DocumentSignatureException;
	void verifySignature(Statement statement, String signature) throws SignatureVerificationFailedException, InternalMicroserviceException, DocumentSignatureException;
}
