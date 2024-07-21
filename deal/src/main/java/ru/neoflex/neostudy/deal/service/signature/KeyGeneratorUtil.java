package ru.neoflex.neostudy.deal.service.signature;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.security.*;

@Getter
@Service
public class KeyGeneratorUtil {
	
	public KeyPair generateKeyPair() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(2048);
			return keyGen.generateKeyPair();
		}
		catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}
