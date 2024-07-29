package ru.neoflex.neostudy.deal.service.signature.uuid;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.SignatureVerificationFailedException;
import ru.neoflex.neostudy.common.exception.DocumentSignatureException;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.service.signature.SignatureService;

import java.util.Objects;
import java.util.UUID;

@Service("uuidSignatureService")
@Log4j2
public class UuidSignatureService implements SignatureService {
	
	/**
	 * Генерирует подпись в виде UUID type 4 (псевдослучайная генерация UUID) и возвращает её в формате {@code String}.
	 * @return подпись в виде UUID type 4 в формате {@code String}.
	 */
	@Override
	public String createSignature() {
		String signature = UUID.randomUUID().toString();
		log.info("Method: UUID. Signature was generated: {}", signature);
		return signature;
	}
	
	@Override
	public void signDocument(Statement statement, String signature) throws DocumentSignatureException {
		if (statement.getPdfFile() == null) {
			throw new DocumentSignatureException("Method: UUID. Signature error: document is not created");
		}
		statement.setSessionCode(signature);
	}
	
	@Override
	public void verifySignature(Statement statement, String signature) throws SignatureVerificationFailedException, DocumentSignatureException {
		checkPreconditions(statement, signature);
		
		try {
			if (!statement.getSessionCode().equals(UUID.fromString(signature).toString())) {
				log.info("Method: UUID. Signature verification is failed. Method: UUID. Document is not original.");
				throw new SignatureVerificationFailedException("Method: UUID. Signature verification is failed. Document is not original.");
			}
			log.info("Method: UUID. Signature verification is passed. Document is original.");
		}
		catch (IllegalArgumentException e) {
			throw new SignatureVerificationFailedException("Method: UUID. Invalid signature key format", e);
		}
	}
	
	private void checkPreconditions(Statement statement, String signature) throws DocumentSignatureException, SignatureVerificationFailedException {
		if (statement.getPdfFile() == null) {
			throw new DocumentSignatureException("Method: UUID. Verify signature error: document is not created");
		}
		if (statement.getSessionCode() == null) {
			throw new DocumentSignatureException("Method: UUID. Verify signature error: document is not signed");
		}
		if (signature == null || signature.isEmpty() || signature.isBlank()) {
			throw new SignatureVerificationFailedException("Method: UUID. Signature is empty");
		}
	}
}
