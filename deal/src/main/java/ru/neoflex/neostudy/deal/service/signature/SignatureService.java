package ru.neoflex.neostudy.deal.service.signature;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.exception.DocumentSigningException;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.deal.entity.Statement;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Log4j2
public class SignatureService {
	private final KeyGeneratorUtil keyGeneratorUtil;
	private final DigitalSignatureUtil digitalSignatureUtil;
	
	
	public String createSignature() {
		KeyPair keyPair = keyGeneratorUtil.generateKeyPair();
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();
		log.debug("public key generated: {}", Base64.getEncoder().encodeToString(publicKey.getEncoded()));
		String privateKeyAsString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
		log.debug("private key generated: {}", privateKeyAsString);
		return privateKeyAsString;
	}
	
	public void signDocument(Statement statement, String privateSignature) throws InternalMicroserviceException, DocumentSigningException {
		byte[] privateKeyAsBytes = Base64.getDecoder().decode(privateSignature);
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyAsBytes);
		String publicSignature;
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
			log.debug("private key received: {}", Arrays.toString(privateKey.getEncoded()));
			publicSignature = digitalSignatureUtil.signData(statement.getPdfFile(), privateKey);
		}
		catch (NoSuchAlgorithmException e) {
			throw new InternalMicroserviceException("Algorithm not available", e);
		}
		catch (InvalidKeyException e) {
			throw new DocumentSigningException("Invalid key", e);
		}
		catch (InvalidKeySpecException e) {
			throw new InternalMicroserviceException("Invalid key specification", e);
		}
		catch (SignatureException e) {
			throw new InternalMicroserviceException("Signature error", e);
		}
		
		statement.setSessionCode(publicSignature);
		statement.setSignDate(LocalDateTime.now());
	}
}
