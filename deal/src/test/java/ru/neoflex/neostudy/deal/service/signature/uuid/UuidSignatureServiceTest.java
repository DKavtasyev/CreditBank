package ru.neoflex.neostudy.deal.service.signature.uuid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.neoflex.neostudy.common.exception.DocumentSignatureException;
import ru.neoflex.neostudy.common.exception.SignatureVerificationFailedException;
import ru.neoflex.neostudy.deal.entity.Statement;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UuidSignatureServiceTest {
	UuidSignatureService signatureService = new UuidSignatureService();
	
	@Nested
	@DisplayName("Тестирование метода UuidSignatureService:createSignature()")
	class TestingCreateSignatureMethod {
		
		@Test
		void createSignature_ifValid_thenOk() {
			String signature = signatureService.createSignature();
			assertDoesNotThrow(() -> UUID.fromString(signature));
		}
		
		@Test
		void createSignature_ifInvalid_thenThrowIllegalArgumentException() {
			String signature = signatureService.createSignature() + " wrong_uuid";
			assertThrows(IllegalArgumentException.class, () -> UUID.fromString(signature));
		}
	}
	
	@Nested
	@DisplayName("Тестирование метода UuidSignatureService:signDocument()")
	class TestingSignDocumentMethod {
		Statement statement;
		
		@BeforeEach
		void init()	{
			statement = new Statement();
		}
		
		@Test
		void signDocument_ifDocumentIsCreated_thenSignDocument() throws Exception {
			statement.setPdfFile("This is document for testing signature".getBytes());
			String signature = UUID.randomUUID().toString();
			signatureService.signDocument(statement, signature);
			assertThat(statement.getSessionCode()).isEqualTo(signature);
		}
		
		@Test
		void signDocument_ifDocumentIsNotCreated_thenThrowDocumentSignatureException() {
			String signature = UUID.randomUUID().toString();
			assertThrows(DocumentSignatureException.class, () -> signatureService.signDocument(statement, signature));
			
		}
	}
	
	@Nested
	@DisplayName("Тестирование метода UuidSignatureService:verifySignature()")
	class TestingVerifySignatureMethod {
		Statement statement;
		String signature;
		
		@BeforeEach
		void init()	{
			statement = new Statement();
			signature = UUID.randomUUID().toString();
		}
		
		@Test
		void verifySignature_ifVerificationWasSuccessful_thenOk() {
			statement.setPdfFile("This is document for testing signature".getBytes());
			statement.setSessionCode(signature);
			assertDoesNotThrow(() -> signatureService.verifySignature(statement, signature));
		}
		
		@Test
		void verifySignature_ifSignatureIsNotOriginal_thenThrowSignatureVerificationFailedException() {
			statement.setPdfFile("This is document for testing signature".getBytes());
			statement.setSessionCode(signature);
			assertThrows(SignatureVerificationFailedException.class, () -> signatureService.verifySignature(statement, UUID.randomUUID().toString()));
		}
		
		@Test
		void verifySignature_ifSignatureHasWrongFormat_thenThrowSignatureVerificationFailedException() {
			signature = signature + " wrong_uuid";
			statement.setPdfFile("This is document for testing signature".getBytes());
			statement.setSessionCode(signature);
			assertThrows(SignatureVerificationFailedException.class, () -> signatureService.verifySignature(statement, signature));
		}
		
		@Test
		void verifySignature_ifDocumentIsNotCreated_thenThrowDocumentSignatureException() {
			statement.setSessionCode(signature);
			assertThrows(DocumentSignatureException.class, () -> signatureService.verifySignature(statement, signature));
		}
		
		@Test
		void verifySignature_ifDocumentIsNotSigned_thenThrowDocumentSignatureException() {
			statement.setPdfFile("This is document for testing signature".getBytes());
			assertThrows(DocumentSignatureException.class, () -> signatureService.verifySignature(statement, signature));
		}
		
		@ParameterizedTest
		@MethodSource("argsProvidedFactory")
		void verifySignature_ifSignatureIsNullOrEmptyOrBlank_thenThrowSignatureVerificationFailedException(String argument) {
			statement.setPdfFile("This is document for testing signature".getBytes());
			statement.setSessionCode(signature);
			assertThrows(SignatureVerificationFailedException.class, () -> signatureService.verifySignature(statement, argument));
		}
		
		static Stream<String> argsProvidedFactory() {
			return Stream.of(null, "", "    ");
		}
	}
}