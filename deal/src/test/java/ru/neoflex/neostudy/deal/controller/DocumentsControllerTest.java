package ru.neoflex.neostudy.deal.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.constants.ChangeType;
import ru.neoflex.neostudy.common.constants.Theme;
import ru.neoflex.neostudy.common.exception.*;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.service.DataService;
import ru.neoflex.neostudy.deal.service.document.PdfDocumentCreator;
import ru.neoflex.neostudy.deal.service.kafka.KafkaService;
import ru.neoflex.neostudy.deal.service.signature.SignatureService;

import java.util.Base64;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = DocumentsController.class)
class DocumentsControllerTest {
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private DataService dataService;
	@MockBean
	private KafkaService kafkaService;
	@MockBean
	private PdfDocumentCreator pdfDocumentCreator;
	@MockBean
	private SignatureService uuidSignatureService;
	
	UUID statementId;
	
	@Nested
	@DisplayName("Тестирование метода DocumentsController:sendDocuments()")
	class TestingSendDocumentsMethod {
		
		@BeforeEach
		void init() {
			statementId = UUID.randomUUID();
		}
		
		@Nested
		@DisplayName("Тестирование прослушивания HTTP запросов")
		class TestingListeningHttpRequests {
			@Test
			void sendDocuments_whenValidInput_returns200() throws Exception {
				byte[] documentAsBytes = "document".getBytes();
				Statement statement = new Statement();
				statement.setStatementId(statementId);
				when(dataService.findStatement(statementId)).thenReturn(statement);
				when(pdfDocumentCreator.createDocument(any(Statement.class))).thenReturn(documentAsBytes);
				mockMvc.perform(post("/deal/document/{statementId}/send", statementId))
						.andExpect(status().isOk());
			}
			
			@Test
			void sendDocuments_whenNotAllowedMethod_returns405() throws Exception {
				mockMvc.perform(get("/deal/document/{statementId}/send", statementId))
						.andExpect(status().isMethodNotAllowed());
			}
		}
		
		@Nested
		@DisplayName("Тестирование вызова методов сервисов")
		class TestingDeserialization {
			@Test
			void sendDocuments_whenValidInput_thenMapsToBusinessModel() throws Exception {
				Statement statement = new Statement();
				statement.setStatementId(statementId);
				
				byte[] documentAsBytes = "document".getBytes();
				String documentAsString = Base64.getEncoder().encodeToString(documentAsBytes);
				when(dataService.findStatement(statementId)).thenReturn(statement);
				when(pdfDocumentCreator.createDocument(any(Statement.class))).thenReturn(documentAsBytes);
				
				mockMvc.perform(post("/deal/document/{statementId}/send", statementId))
						.andExpect(status().isOk());
				
				assertAll(() -> {
					verify(dataService, times(1)).findStatement(statementId);
					verify(pdfDocumentCreator, times(1)).createDocument(statement);
					verify(dataService, times(1)).updateStatement(statement, ApplicationStatus.PREPARE_DOCUMENTS, ChangeType.AUTOMATIC);
					verify(kafkaService, times(1)).sendKafkaMessage(statement, Theme.SEND_DOCUMENTS, documentAsString);
					assertThat(statement.getPdfFile()).isEqualTo(documentAsBytes);
				});
			}
		}
		
		@Nested
		@DisplayName("Тестирование обработки исключений")
		class TestingExceptions {
			@Test
			void sendDocuments_whenStatementNotFound_thenReturn404() throws Exception {
				doThrow(StatementNotFoundException.class).when(dataService).findStatement(statementId);
				mockMvc.perform(post("/deal/document/{statementId}/send", statementId))
						.andExpect(status().isNotFound());
			}
			
			@Test
			void sendDocuments_whenRequestSendingError_thenReturn500() throws Exception {
				Statement statement = new Statement();
				statement.setStatementId(statementId);
				
				when(dataService.findStatement(statementId)).thenReturn(statement);
				doThrow(UserDocumentException.class).when(pdfDocumentCreator).createDocument(statement);
				mockMvc.perform(post("/deal/document/{statementId}/send", statementId))
						.andExpect(status().isUnprocessableEntity());
			}
		}
	}
	
	@Nested
	@DisplayName("Тестирование метода DocumentsController:signDocuments()")
	class TestingSignDocumentsMethod {
		
		@BeforeEach
		void init() {
			statementId = UUID.randomUUID();
		}
		
		@Nested
		@DisplayName("Тестирование прослушивания HTTP запросов")
		class TestingListeningHttpRequests {
			
			@Test
			void signDocuments_whenValidInput_returns200() throws Exception {
				Statement statement = new Statement();
				statement.setStatementId(statementId);
				
				when(dataService.findStatement(statementId)).thenReturn(statement);
				
				mockMvc.perform(post("/deal/document/{statementId}/sign", statementId))
						.andExpect(status().isOk());
			}
			
			@Test
			void signDocuments_whenNotAllowedMethod_returns405() throws Exception {
				mockMvc.perform(get("/deal/document/{statementId}/sign", statementId))
						.andExpect(status().isMethodNotAllowed());
			}
		}
		
		@Nested
		@DisplayName("Тестирование вызова методов сервисов")
		class TestingDeserialization {
			@Test
			void signDocuments_whenValidInput_thenMapsToBusinessModel() throws Exception {
				String expectedSessionCode = "session code";
				Statement statement = mock(Statement.class);
				when(dataService.findStatement(statementId)).thenReturn(statement);
				when(statement.getSessionCode()).thenReturn(expectedSessionCode);
				ArgumentCaptor<Statement> statementArgumentCaptor = ArgumentCaptor.forClass(Statement.class);
				ArgumentCaptor<String> keyPairCaptor = ArgumentCaptor.forClass(String.class);
				
				mockMvc.perform(post("/deal/document/{statementId}/sign", statementId))
						.andExpect(status().isOk());
				
				assertAll(() -> {
					verify(dataService, times(1)).findStatement(statementId);
					verify(uuidSignatureService, times(1)).createSignature();
					verify(uuidSignatureService, times(1)).signDocument(statementArgumentCaptor.capture(), keyPairCaptor.capture());
					verify(dataService, times(1)).saveStatement(statementArgumentCaptor.capture());
					verify(kafkaService, times(1)).sendKafkaMessage(statement, Theme.SEND_SES, expectedSessionCode);
				});
			}
		}
		
		@Nested
		@DisplayName("Тестирование обработки исключений")
		class TestingExceptions {
			@Test
			void signDocuments_whenStatementNotFound_thenReturn404() throws Exception {
				doThrow(StatementNotFoundException.class).when(dataService).findStatement(statementId);
				mockMvc.perform(post("/deal/document/{statementId}/sign", statementId))
						.andExpect(status().isNotFound());
			}
			
			@Test
			void signDocuments_whenDocumentIsNotCreated_thenReturn422() throws Exception {
				String keyPair = "keyPair";
				Statement statement = mock(Statement.class);
				when(dataService.findStatement(statementId)).thenReturn(statement);
				when(uuidSignatureService.createSignature()).thenReturn(keyPair);
				doThrow(DocumentSignatureException.class).when(uuidSignatureService).signDocument(statement, keyPair);
				mockMvc.perform(post("/deal/document/{statementId}/sign", statementId))
						.andExpect(status().isUnprocessableEntity());
			}
			
			@Test
			void signDocuments_whenSigningError_thenReturn500() throws Exception {
				Statement statement = mock(Statement.class);
				when(dataService.findStatement(statementId)).thenReturn(statement);
				doThrow(InternalMicroserviceException.class).when(uuidSignatureService).createSignature();
				mockMvc.perform(post("/deal/document/{statementId}/sign", statementId))
						.andExpect(status().isInternalServerError());
			}
		}
	}
	
	
	@Nested
	@DisplayName("Тестирование метода DocumentsController:verifySesCode()")
	class TestingVerifySesCodeMethod {
		
		@BeforeEach
		void init() {
			statementId = UUID.randomUUID();
		}
		
		@Nested
		@DisplayName("Тестирование прослушивания HTTP запросов")
		class TestingListeningHttpRequests {
			
			@Test
			void verifySesCode_whenValidInput_returns200() throws Exception {
				Statement statement = mock(Statement.class);
				when(dataService.findStatement(statementId)).thenReturn(statement);
				mockMvc.perform(post("/deal/document/{statementId}/code", statementId)
								.param("code", "1234"))
						.andExpect(status().isOk());
			}
			
			@Test
			void verifySesCode_whenNotAllowedMethod_returns405() throws Exception {
				mockMvc.perform(get("/deal/document/{statementId}/code", statementId)
								.param("code", "1234"))
						.andExpect(status().isMethodNotAllowed());
			}
		}
		
		@Nested
		@DisplayName("Тестирование вызова методов сервисов")
		class TestingDeserialization {
			@Test
			void verifySesCode_whenValidInput_thenMapsToBusinessModel() throws Exception {
				Statement statement = mock(Statement.class);
				when(dataService.findStatement(statementId)).thenReturn(statement);
				
				
				ArgumentCaptor<Statement> statementArgumentCaptor = ArgumentCaptor.forClass(Statement.class);
				ArgumentCaptor<String> signatureCaptor = ArgumentCaptor.forClass(String.class);
				ArgumentCaptor<ApplicationStatus> statusCaptor = ArgumentCaptor.forClass(ApplicationStatus.class);
				ArgumentCaptor<ChangeType> changeTypeCaptor = ArgumentCaptor.forClass(ChangeType.class);
				ArgumentCaptor<Theme> themeCaptor = ArgumentCaptor.forClass(Theme.class);
				
				mockMvc.perform(post("/deal/document/{statementId}/code", statementId)
								.param("code", "1234"))
						.andExpect(status().isOk());
				
				assertAll(() -> {
					verify(dataService, times(1)).findStatement(statementId);
					verify(uuidSignatureService, times(1)).verifySignature(statementArgumentCaptor.capture(), signatureCaptor.capture());
					verify(dataService, times(2)).updateStatement(statementArgumentCaptor.capture(), statusCaptor.capture(), changeTypeCaptor.capture());
					verify(kafkaService, times(1)).sendKafkaMessage(statementArgumentCaptor.capture(), themeCaptor.capture(), isNull());
					assertThat(statusCaptor.getAllValues().get(0)).isEqualTo(ApplicationStatus.DOCUMENT_SIGNED);
					assertThat(statusCaptor.getAllValues().get(1)).isEqualTo(ApplicationStatus.CREDIT_ISSUED);
					assertThat(changeTypeCaptor.getAllValues().get(0)).isEqualTo(ChangeType.AUTOMATIC);
					assertThat(changeTypeCaptor.getAllValues().get(1)).isEqualTo(ChangeType.AUTOMATIC);
					assertThat(signatureCaptor.getValue()).isEqualTo("1234");
					assertThat(themeCaptor.getValue()).isEqualTo(Theme.CREDIT_ISSUED);
				});
			}
		}
		
		@Nested
		@DisplayName("Тестирование обработки исключений")
		class TestingExceptions {
			@Test
			void verifySesCode_whenStatementNotFound_thenReturn404() throws Exception {
				doThrow(StatementNotFoundException.class).when(dataService).findStatement(statementId);
				mockMvc.perform(post("/deal/document/{statementId}/code", statementId)
								.param("code", "1234"))
						.andExpect(status().isNotFound());
			}
			
			@Test
			void verifySesCode_whenSignatureOrDocumentIsNotOriginal_thenReturn400() throws Exception {
				Statement statement = mock(Statement.class);
				when(dataService.findStatement(statementId)).thenReturn(statement);
				doThrow(SignatureVerificationFailedException.class).when(uuidSignatureService).verifySignature(statement, "1234");
				mockMvc.perform(post("/deal/document/{statementId}/code", statementId)
								.param("code", "1234"))
						.andExpect(status().isBadRequest());
			}
			
			@Test
			void verifySesCode_whenPreconditionsIsNotMet_thenReturn422() throws Exception {
				Statement statement = mock(Statement.class);
				when(dataService.findStatement(statementId)).thenReturn(statement);
				doThrow(DocumentSignatureException.class).when(uuidSignatureService).verifySignature(statement, "1234");
				mockMvc.perform(post("/deal/document/{statementId}/code", statementId)
								.param("code", "1234"))
						.andExpect(status().isUnprocessableEntity());
			}
			
			@Test
			void verifySesCode_whenSigningError_thenReturn500() throws Exception {
				Statement statement = mock(Statement.class);
				when(dataService.findStatement(statementId)).thenReturn(statement);
				doThrow(InternalMicroserviceException.class).when(uuidSignatureService).verifySignature(statement, "1234");
				mockMvc.perform(post("/deal/document/{statementId}/code", statementId)
								.param("code", "1234"))
						.andExpect(status().isInternalServerError());
			}
		}
	}
}