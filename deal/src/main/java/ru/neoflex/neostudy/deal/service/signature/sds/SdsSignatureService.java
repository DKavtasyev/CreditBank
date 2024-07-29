package ru.neoflex.neostudy.deal.service.signature.sds;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.exception.DocumentSignatureException;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.SignatureVerificationFailedException;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.service.signature.SignatureService;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.Base64;

@Service("sdsSignatureService")
@RequiredArgsConstructor
@Log4j2
public class SdsSignatureService implements SignatureService {
	private final KeyGeneratorUtil keyGeneratorUtil;
	private final DigitalSignatureUtil digitalSignatureUtil;
	
	/**
	 * Возвращает приватный ключ в виде строки String, закодированный по алгоритму Base64.
	 * @return приватный ключ.
	 * @throws InternalMicroserviceException выбрасывается в случае использования неподдерживаемого алгоритма
	 * кодирования.
	 */
	public String createSignature() throws InternalMicroserviceException {
		KeyPair keyPair = keyGeneratorUtil.generateKeyPair();
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();
		String publicKeyAsString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
		log.debug("Method: SDS. public key generated: {}", publicKeyAsString);
		String privateKeyAsString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
		log.debug("Method: SDS. private key generated: {}", privateKeyAsString);
		return publicKeyAsString + " " + privateKeyAsString;
	}
	
	/**
	 * Декодирует приватный ключ из полученного кода в виде строки String, закодированной по алгоритму Base64.
	 * Подписывает документ, сохраняет полученную подпись в объект заявки по кредиту {@code Statement}.
	 * @param statement объект-entity, содержащий все данные по кредиту.
	 * @param keyPair закодированная по Base64 байтовая последовательность приватного кода.
	 * @throws DocumentSignatureException если документ для текущей заявки не был создан.
	 * @throws InternalMicroserviceException выкидывается в следующих случаях:
	 * <ul>
	 *     <li>Использования неподдерживаемого алгоритма кодирования.</li>
	 *     <li>Использования спецификации ключа, не подходящей для создания закрытого ключа с помощью данной фабрики
	 *     ключей</li>
	 *     <li>Объект подписи не инициализирован должным образом или используемый алгоритм подписи не может обработать
	 *     предоставленные входные данные.</li>
	 * </ul>
	 */
	public void signDocument(Statement statement, String keyPair) throws InternalMicroserviceException, DocumentSignatureException {
		log.info("Method: SDS. KeyPair: {}", keyPair);
		String[] keys = keyPair.split(" ");
		String publicKeyAsString = keys[0];
		String privateKeyAsString = keys[1];
		String documentSignature;
		try {
			PrivateKey privateKey = digitalSignatureUtil.getPrivateKeyFromBase64(privateKeyAsString);
			documentSignature = digitalSignatureUtil.signData(statement.getPdfFile(), privateKey) + " " + publicKeyAsString;
		}
		catch (NoSuchAlgorithmException e) {
			throw new InternalMicroserviceException("Method: SDS. Algorithm not available", e);
		}
		catch (InvalidKeyException e) {
			throw new InternalMicroserviceException("Method: SDS. Invalid private key", e);
		}
		catch (InvalidKeySpecException e) {
			throw new InternalMicroserviceException("Method: SDS. Invalid key specification", e);
		}
		catch (SignatureException e) {
			throw new InternalMicroserviceException("Method: SDS. Signature error", e);
		}
		catch(NullPointerException e) {
			throw new DocumentSignatureException("Method: SDS. Signature error: document is not created", e);
		}
		log.info("Method: SDS. Document has been signed. Signature: {}", documentSignature);
		statement.setSessionCode(documentSignature);
		statement.setSignDate(LocalDateTime.now());
	}
	
	public void verifySignature(Statement statement, String signatureAndPublicKey) throws InternalMicroserviceException, DocumentSignatureException, SignatureVerificationFailedException {
		checkPreconditions(statement, signatureAndPublicKey);
		try {
			String[] signatureAndPublicKeySeparated = signatureAndPublicKey.split(" ");
			String signature = signatureAndPublicKeySeparated[0];
			String publicKeyAsString = signatureAndPublicKeySeparated[1];
			PublicKey publicKey = digitalSignatureUtil.getPublicKeyFromBase64(publicKeyAsString);
			boolean isOriginal = digitalSignatureUtil.verifySignature(statement.getPdfFile(), signature, publicKey);
			if (!isOriginal) {
				log.info("Method: SDS. Signature verification is failed. Document is not original.");
				throw new SignatureVerificationFailedException("Method: SDS. Signature verification is failed. Document is not original.");
			}
			log.info("Method: SDS. Signature verification is passed. Document is original.");
		}
		catch (NoSuchAlgorithmException e) {
			throw new InternalMicroserviceException("Method: SDS. Algorithm not available", e);
		}
		catch (InvalidKeyException e) {
			throw new InternalMicroserviceException("Method: SDS. Invalid public key", e);
		}
		catch (InvalidKeySpecException e) {
			throw new InternalMicroserviceException("Method: SDS. Invalid key specification", e);
		}
		catch (SignatureException e) {
			throw new InternalMicroserviceException("Method: SDS. Signature error", e);
		}
		catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
			throw new SignatureVerificationFailedException("Method: SDS. Invalid signature key format", e);
		}
	}
	
	private void checkPreconditions(Statement statement, String signatureAndPublicKey) throws DocumentSignatureException, SignatureVerificationFailedException {
		if (statement.getPdfFile() == null) {
			throw new DocumentSignatureException("Method: SDS. Verify signature error: document is not created");
		}
		if (statement.getSessionCode() == null) {
			throw new DocumentSignatureException("Method: SDS. Verify signature error: document is not signed");
		}
		if (signatureAndPublicKey == null || signatureAndPublicKey.isEmpty() || signatureAndPublicKey.isBlank()) {
			throw new SignatureVerificationFailedException("Method: SDS. Signature is empty");
		}
	}
}
