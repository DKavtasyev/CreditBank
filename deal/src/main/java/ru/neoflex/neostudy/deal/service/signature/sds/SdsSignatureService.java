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
	@Override
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
	@Override
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
	
	/**
	 * Проверяет на подлинность документ с помощью переданной в метод подписи. В передаваемой строке содержится подпись
	 * документа и публичный ключ, закодированный по алгоритму Base64, разделённые пробелом. В методе проверяется
	 * наличие документа и его подписи разделение подписи и ключа, проверка документа на подлинность.
	 * @param statement объект-entity, содержащий в себе проверяемый документ.
	 * @param signatureAndPublicKey подпись для проверяемого документа, с помощью которой осуществляется проверка
	 * документа на подлинность.
	 * @throws InternalMicroserviceException выкидывается в следующих случаях:
	 * <ul>
	 *     <li>Использования неподдерживаемого алгоритма кодирования.</li>
	 *     <li>Использования спецификации ключа, не подходящей для создания открытого ключа с помощью данной фабрики
	 *     ключей</li>
	 *     <li>Объект подписи не инициализирован должным образом или используемый алгоритм подписи не может обработать
	 *     предоставленные входные данные.</li>
	 * </ul>
	 * @throws DocumentSignatureException если не выполнены условия для проверки подписи:
	 * <ul>
	 *     <li>Документ ранее не был создан.</li>
	 *     <li>Документ ранее не был подписан.</li>
	 * </ul>
	 * @throws SignatureVerificationFailedException если проверка на подлинность показала, что документ не является
	 * подлинным или подпись для проверки не была передана.
	 */
	@Override
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
	
	/**
	 * Проверяет наличие подписываемого документа и подписи к нему, а также наличие передаваемой подписи для проверки.
	 * @param statement объект-entity, содержащий в себе проверяемый документ.
	 * @param signatureAndPublicKey подпись для проверяемого документа, с помощью которой осуществляется проверка
	 * документа на подлинность.
	 * @throws DocumentSignatureException если не выполнены условия для проверки подписи:
	 * <ul>
	 *     <li>Документ ранее не был создан.</li>
	 *     <li>Документ ранее не был подписан.</li>
	 * </ul>
	 * @throws SignatureVerificationFailedException если подпись для проверки не была передана.
	 */
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
