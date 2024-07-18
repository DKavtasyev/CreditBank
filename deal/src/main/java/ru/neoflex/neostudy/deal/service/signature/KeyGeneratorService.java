package ru.neoflex.neostudy.deal.service.signature;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.security.*;

@Getter
@Service
public class KeyGeneratorService {
	private final PrivateKey privateKey;
	private final PublicKey publicKey;
	
	public KeyGeneratorService() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(2048);
		KeyPair pair = keyGen.generateKeyPair();
		this.privateKey = pair.getPrivate();
		this.publicKey = pair.getPublic();
	}
}
