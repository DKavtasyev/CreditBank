package ru.neoflex.neostudy.deal.service.signature;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.*;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class DigitalSignatureService {
	
	public String signData(String data, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature rsa = Signature.getInstance("SHA256withRSA");
		rsa.initSign(privateKey);
		rsa.update(data.getBytes());
		byte[] signedBytes = rsa.sign();
		return Base64.getEncoder().encodeToString(signedBytes);
	}
	
	public boolean verifySignature(String data, String signature, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature sig = Signature.getInstance("SHA256withRSA");
		sig.initVerify(publicKey);
		sig.update(data.getBytes());
		byte[] signatureBytes = Base64.getDecoder().decode(signature);
		return sig.verify(signatureBytes);
	}
}
