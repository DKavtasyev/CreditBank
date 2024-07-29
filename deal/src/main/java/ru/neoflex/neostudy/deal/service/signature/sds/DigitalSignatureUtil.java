package ru.neoflex.neostudy.deal.service.signature.sds;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class DigitalSignatureUtil {
	
	/**
	 * Возвращает подпись переданного документа data, закодированную по Base64.
	 * @param data подписываемые данные.
	 * @param privateKey приватный ключ для подписи документов.
	 * @return подпись в виде строки String, закодированная по алгоритму Base64.
	 * @throws NoSuchAlgorithmException выбрасывается в случае использования неподдерживаемого алгоритма кодирования.
	 * @throws InvalidKeyException выбрасывается в случае некорректного приватного ключа.
	 * @throws SignatureException выбрасывается в случае возникновения ошибки при подписании документа.
	 */
	public String signData(byte[] data, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature rsa = Signature.getInstance("SHA256withRSA");
		rsa.initSign(privateKey);
		rsa.update(data);
		byte[] signedBytes = rsa.sign();
		return Base64.getEncoder().encodeToString(signedBytes);
	}
	
	/**
	 * Возвращает {@code true}, если проверка данных и подписи на оригинальность прошла успешно, и возвращает
	 * {@code false}, если данные оказались неоригинальными или изменёнными.
	 * @param data подписанные данные, оригинальность которых проверяется.
	 * @param signature signature подпись данных, закодированная по алгоритму Base64.
	 * @param publicKey объект публичного ключа.
	 * @return результат проверки данных на оригинальность в формате {@code boolean} значения.
	 * @throws NoSuchAlgorithmException выбрасывается в случае использования неподдерживаемого алгоритма кодирования.
	 * @throws InvalidKeyException выбрасывается в случае некорректного публичного ключа.
	 * @throws SignatureException выбрасывается, если объект подписи не инициализирован должным образом или используемый
	 * алгоритм подписи не может обработать предоставленные входные данные.
	 */
	public boolean verifySignature(byte[] data, String signature, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature sig = Signature.getInstance("SHA256withRSA");
		sig.initVerify(publicKey);
		sig.update(data);
		byte[] signatureBytes = Base64.getDecoder().decode(signature);
		return sig.verify(signatureBytes);
	}
	
	/**
	 * Возвращает декодированный объект {@code PrivateKey} из строки с этим объектом, представленной в формате Base64.
	 * @param privateKeyAsString строка с приватным ключом, закодированным по Base64.
	 * @return декодированный объект {@code PrivateKey}.
	 * @throws NoSuchAlgorithmException выбрасывается в случае использования неподдерживаемого алгоритма кодирования.
	 * @throws InvalidKeySpecException если используемая спецификации ключа не подходит для создания закрытого ключа с
	 * помощью данной фабрики ключей.
	 */
	public PrivateKey getPrivateKeyFromBase64(String privateKeyAsString) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] privateKeyAsBytes = Base64.getDecoder().decode(privateKeyAsString);
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyAsBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePrivate(privateKeySpec);
	}
	
	/**
	 * Возвращает декодированный объект {@code PublicKey} из строки с этим объектом, представленной в формате Base64.
	 * @param publicKeyAsString строка с публичным ключом, закодированным по Base64.
	 * @return декодированный объект {@code PublicKey}.
	 * @throws NoSuchAlgorithmException выбрасывается в случае использования неподдерживаемого алгоритма кодирования.
	 * @throws InvalidKeySpecException если используемая спецификации ключа не подходит для создания закрытого ключа с
	 * помощью данной фабрики ключей.
	 */
	public PublicKey getPublicKeyFromBase64(String publicKeyAsString) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] publicKeyAsBytes = Base64.getDecoder().decode(publicKeyAsString);
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyAsBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePublic(publicKeySpec);
	}
}
