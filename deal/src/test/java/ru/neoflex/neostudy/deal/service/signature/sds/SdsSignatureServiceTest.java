package ru.neoflex.neostudy.deal.service.signature.sds;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SdsSignatureServiceTest {
	
	@Mock
	KeyGeneratorUtil keyGeneratorUtil;
	@InjectMocks
	SdsSignatureService sdsSignatureService;
	
	@Test
	void createSignature() throws InternalMicroserviceException {
		KeyPair keyPairMock = mock(KeyPair.class);
		PublicKey publicKey = mock(PublicKey.class);
		PrivateKey privateKey = mock(PrivateKey.class);
		when(keyGeneratorUtil.generateKeyPair()).thenReturn(keyPairMock);
		when(keyPairMock.getPublic()).thenReturn(publicKey);
		when(keyPairMock.getPrivate()).thenReturn(privateKey);
		when(publicKey.getEncoded()).thenReturn("publicKeyMock".getBytes());
		when(privateKey.getEncoded()).thenReturn("privateKeyMock".getBytes());
		String actualSignature = sdsSignatureService.createSignature();
		Assertions.assertThat(actualSignature).isEqualTo("cHVibGljS2V5TW9jaw== cHJpdmF0ZUtleU1vY2s=");
	}
}