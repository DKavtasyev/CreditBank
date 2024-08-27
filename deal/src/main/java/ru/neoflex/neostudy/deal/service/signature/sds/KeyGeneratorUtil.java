package ru.neoflex.neostudy.deal.service.signature.sds;

import lombok.Getter;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;

import java.security.*;

@Getter
@Service
public class KeyGeneratorUtil {
	
	/**
	 * Генерирует и возвращает объект {@code KeyPair}, содержащий в себе публичный и приватный ключи.
	 * @return {@code KeyPair}.
	 * @throws InternalMicroserviceException если указанный алгоритм кодирования не поддерживается.
	 */
	public KeyPair generateKeyPair() throws InternalMicroserviceException {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(2048);
			return keyGen.generateKeyPair();
		}
		catch (NoSuchAlgorithmException e) {
			throw new InternalMicroserviceException("");
		}
	}
}
