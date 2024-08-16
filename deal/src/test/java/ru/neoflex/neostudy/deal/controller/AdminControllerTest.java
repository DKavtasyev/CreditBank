package ru.neoflex.neostudy.deal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.ResultActions;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.constants.ChangeType;
import ru.neoflex.neostudy.common.dto.CreditDto;
import ru.neoflex.neostudy.common.dto.FinishingRegistrationRequestDto;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.common.util.DtoInitializer;
import ru.neoflex.neostudy.deal.entity.Client;
import ru.neoflex.neostudy.deal.entity.Credit;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.entity.jsonb.StatementStatusHistory;
import ru.neoflex.neostudy.deal.mapper.CreditMapper;
import ru.neoflex.neostudy.deal.mapper.PreScoreClientPersonalIdentificationInformationMapper;
import ru.neoflex.neostudy.deal.mapper.ScoringDataMapper;
import ru.neoflex.neostudy.deal.service.DataService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.neoflex.neostudy.deal.custom.ResponseBodyMatcher.responseBody;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AdminController.class)
class AdminControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	
	private final CreditMapper creditMapper = new CreditMapper();
	private final PreScoreClientPersonalIdentificationInformationMapper clientMapper = new PreScoreClientPersonalIdentificationInformationMapper();
	private final ScoringDataMapper scoringDataMapper = new ScoringDataMapper();
	
	@MockBean
	private DataService dataService;
	UUID statementId;
	
	@Nested
	@DisplayName("Тестирование метода AdminController:updateStatementStatus()")
	class TestingUpdateStatementStatusMethod {
		
		@BeforeEach
		void init() {
			statementId = UUID.randomUUID();
		}
		
		@Nested
		@DisplayName("Тестирование прослушивания HTTP запросов")
		class TestingListeningHttpRequests {
			@Test
			void updateStatementStatus_whenValidInput_returns200() throws Exception {
				Statement statement = mock(Statement.class);
				when(dataService.findStatement(statementId)).thenReturn(statement);
				mockMvc.perform(put("/deal/admin/statement/{statementId}/status", statementId)
								.contentType("application/json")
								.content(objectMapper.writeValueAsString(ApplicationStatus.APPROVED)))
						.andExpect(status().isNoContent());
			}
			
			@Test
			void updateStatementStatus_whenNotAllowedMethod_returns405() throws Exception {
				mockMvc.perform(get("/deal/admin/statement/{statementId}/status", statementId)
								.contentType("application/json")
								.content(objectMapper.writeValueAsString(ApplicationStatus.APPROVED)))
						.andExpect(status().isMethodNotAllowed());
			}
		}
		
		@Nested
		@DisplayName("Тестирование десериализации входных данных и вызова методов сервисов")
		class TestingDeserialization {
			@Test
			void updateStatementStatus_whenValidInput_thenMapsToBusinessModel() throws Exception {
				mockMvc.perform(put("/deal/admin/statement/{statementId}/status", statementId)
								.contentType("application/json")
								.content(objectMapper.writeValueAsString(ApplicationStatus.APPROVED)))
						.andExpect(status().isNoContent());
				
				ArgumentCaptor<Statement> statementCaptor = ArgumentCaptor.forClass(Statement.class);
				ArgumentCaptor<ApplicationStatus> statusCaptor = ArgumentCaptor.forClass(ApplicationStatus.class);
				ArgumentCaptor<ChangeType> changeTypeCaptor = ArgumentCaptor.forClass(ChangeType.class);
				
				assertAll(() -> {
					verify(dataService, times(1)).findStatement(statementId);
					verify(dataService, times(1)).updateStatement(statementCaptor.capture(), statusCaptor.capture(), changeTypeCaptor.capture());
					assertThat(statusCaptor.getValue()).isEqualTo(ApplicationStatus.APPROVED);
					assertThat(changeTypeCaptor.getValue()).isEqualTo(ChangeType.MANUAL);
				});
			}
		}
		
		@Nested
		@DisplayName("Тестирование обработки исключений")
		class TestingExceptions {
			@Test
			void updateStatementStatus_whenPassportDataIsInvalid_thenReturn400() throws Exception {
				doThrow(StatementNotFoundException.class).when(dataService).findStatement(statementId);
				mockMvc.perform(put("/deal/admin/statement/{statementId}/status", statementId)
								.contentType("application/json")
								.content(objectMapper.writeValueAsString(ApplicationStatus.APPROVED)))
						.andExpect(status().isNotFound());
			}
		}
	}
	
	@Nested
	@DisplayName("Тестирование метода AdminController:getStatement()")
	class TestingGetStatementMethod {
		
		@BeforeEach
		void init() {
			statementId = UUID.randomUUID();
		}
		
		@Nested
		@DisplayName("Тестирование прослушивания HTTP запросов")
		class TestingListeningHttpRequests {
			@Test
			void getStatement_whenValidInput_returns200() throws Exception {
				Statement statement = mock(Statement.class);
				when(dataService.findStatement(statementId)).thenReturn(statement);
				mockMvc.perform(get("/deal/admin/statement/{statementId}", statementId))
						.andExpect(status().isOk());
			}
			
			@Test
			void getStatement_whenNotAllowedMethod_returns405() throws Exception {
				mockMvc.perform(post("/deal/admin/statement/{statementId}", statementId))
						.andExpect(status().isMethodNotAllowed());
			}
		}
		
		@Nested
		@DisplayName("Тестирование вызова методов сервисов")
		class TestingDeserialization {
			@Test
			void getStatement_whenValidInput_thenMapsToBusinessModel() throws Exception {
				Statement statement = new Statement();
				statement.setStatementId(statementId);
				when(dataService.findStatement(statementId)).thenReturn(statement);
				mockMvc.perform(get("/deal/admin/statement/{statementId}", statementId))
						.andExpect(status().isOk());
				
				verify(dataService, times(1)).findStatement(statementId);
			}
		}
		
		@Nested
		@DisplayName("Тестирование сериализации возвращаемого Statement")
		class TestingSerializationOfLoanOfferDtoList {
			@Test
			void getStatement_whenValidInput_thenMapsToBusinessModel() throws Exception {
				CreditDto creditDto = DtoInitializer.initCreditDto();
				Credit credit = creditMapper.dtoToEntity(creditDto);
				LoanStatementRequestDto loanStatementRequestDto = DtoInitializer.initLoanStatementRequest();
				Client client = clientMapper.dtoToEntity(loanStatementRequestDto);
				LoanOfferDto loanOfferDto = DtoInitializer.initLoanOfferDto();
				Statement statement = new Statement();
				statement.setStatementId(statementId);
				statement.setCredit(credit);
				statement.setClient(client);
				statement.setAppliedOffer(loanOfferDto);
				FinishingRegistrationRequestDto finishingRegistrationRequestDto = DtoInitializer.initFinishingRegistrationRequest();
				scoringDataMapper.formScoringDataDto(finishingRegistrationRequestDto, statement);
				statement.setStatus(ApplicationStatus.DOCUMENT_CREATED);
				statement.setCreationDate(LocalDateTime.now());
				statement.setSignDate(LocalDateTime.now());
				statement.setSessionCode(UUID.randomUUID().toString());
				StatementStatusHistory statementStatusHistory1 = new StatementStatusHistory();
				statementStatusHistory1.setStatus(ApplicationStatus.PREPARE_DOCUMENTS);
				statementStatusHistory1.setTime(LocalDateTime.now());
				statementStatusHistory1.setChangeType(ChangeType.AUTOMATIC);
				StatementStatusHistory statementStatusHistory2 = new StatementStatusHistory();
				statementStatusHistory2.setStatus(ApplicationStatus.APPROVED);
				statementStatusHistory2.setTime(LocalDateTime.now());
				statementStatusHistory2.setChangeType(ChangeType.MANUAL);
				statement.getStatementStatusHistory().add(statementStatusHistory1);
				statement.getStatementStatusHistory().add(statementStatusHistory2);
				byte[] pdfFile = "Test bytes for testing statement".getBytes();
				statement.setPdfFile(pdfFile);
				
				when(dataService.findStatement(statementId)).thenReturn(statement);
				
				ResultActions response = mockMvc.perform(get("/deal/admin/statement/{statementId}", statementId));
				
				response.andExpect(responseBody(objectMapper).containsObjectAsJson(statement, Statement.class));
			}
		}
		
		@Nested
		@DisplayName("Тестирование обработки исключений")
		class TestingExceptions {
			@Test
			void updateStatementStatus_whenPassportDataIsInvalid_thenReturn400() throws Exception {
				doThrow(StatementNotFoundException.class).when(dataService).findStatement(statementId);
				mockMvc.perform(get("/deal/admin/statement/{statementId}/", statementId))
						.andExpect(status().isNotFound());
			}
		}
	}
	
	@Nested
	@DisplayName("Тестирование метода AdminController:getAllStatements()")
	class TestingGetAllStatementsMethod {
		
		@Nested
		@DisplayName("Тестирование прослушивания HTTP запросов")
		class TestingListeningHttpRequests {
			@Test
			void getAllStatements_whenValidInput_returns200() throws Exception {
				mockMvc.perform(get("/deal/admin/statement", statementId))
						.andExpect(status().isOk());
			}
			
			@Test
			void getAllStatements_whenNotAllowedMethod_returns405() throws Exception {
				mockMvc.perform(post("/deal/admin/statement", statementId))
						.andExpect(status().isMethodNotAllowed());
			}
		}
		
		@Nested
		@DisplayName("Тестирование вызова методов сервисов")
		class TestingDeserialization {
			@Test
			void getAllStatements_whenValidInput_thenMapsToBusinessModel() throws Exception {
				List<Statement> statements = new ArrayList<>();
				when(dataService.findAllStatements(anyInt())).thenReturn(statements);
				mockMvc.perform(get("/deal/admin/statement", statementId)
								.param("page", "1"))
						.andExpect(status().isOk());
				
				verify(dataService, times(1)).findAllStatements(1);
			}
		}
		
		@Nested
		@DisplayName("Тестирование сериализации возвращаемого списка Statement")
		class TestingSerializationOfLoanOfferDtoList {
			@Test
			void getStatement_whenValidInput_thenMapsToBusinessModel() throws Exception {
				CreditDto creditDto = DtoInitializer.initCreditDto();
				Credit credit = creditMapper.dtoToEntity(creditDto);
				LoanStatementRequestDto loanStatementRequestDto = DtoInitializer.initLoanStatementRequest();
				Client client = clientMapper.dtoToEntity(loanStatementRequestDto);
				LoanOfferDto loanOfferDto = DtoInitializer.initLoanOfferDto();
				Statement statement1 = new Statement();
				statement1.setStatementId(statementId);
				statement1.setCredit(credit);
				statement1.setClient(client);
				statement1.setAppliedOffer(loanOfferDto);
				FinishingRegistrationRequestDto finishingRegistrationRequestDto = DtoInitializer.initFinishingRegistrationRequest();
				scoringDataMapper.formScoringDataDto(finishingRegistrationRequestDto, statement1);
				statement1.setStatus(ApplicationStatus.DOCUMENT_CREATED);
				statement1.setCreationDate(LocalDateTime.now());
				statement1.setSignDate(LocalDateTime.now());
				statement1.setSessionCode(UUID.randomUUID().toString());
				StatementStatusHistory statementStatusHistory1 = new StatementStatusHistory();
				statementStatusHistory1.setStatus(ApplicationStatus.PREPARE_DOCUMENTS);
				statementStatusHistory1.setTime(LocalDateTime.now());
				statementStatusHistory1.setChangeType(ChangeType.AUTOMATIC);
				StatementStatusHistory statementStatusHistory2 = new StatementStatusHistory();
				statementStatusHistory2.setStatus(ApplicationStatus.APPROVED);
				statementStatusHistory2.setTime(LocalDateTime.now());
				statementStatusHistory2.setChangeType(ChangeType.MANUAL);
				statement1.getStatementStatusHistory().add(statementStatusHistory1);
				statement1.getStatementStatusHistory().add(statementStatusHistory2);
				byte[] pdfFile = "Test bytes for testing statement".getBytes();
				statement1.setPdfFile(pdfFile);
				
				Statement statement2 = new Statement();
				statement2.setStatementId(UUID.randomUUID());
				statement2.setClient(client);
				statement2.setCredit(credit);
				statement2.setStatus(ApplicationStatus.CC_DENIED);
				statement2.setCreationDate(LocalDateTime.now().minusDays(15));
				statement2.setAppliedOffer(loanOfferDto);
				statement2.setSignDate(LocalDateTime.now().minusNanos(1928735698172634L));
				statement2.setSessionCode("81762345817623");
				statement2.getStatementStatusHistory().add(statementStatusHistory2);
				statement2.getStatementStatusHistory().add(statementStatusHistory1);
				statement2.setPdfFile("This is example string text for second statement field pdfFile".getBytes());
				
				List<Statement> statements = new ArrayList<>();
				statements.add(statement1);
				statements.add(statement2);
				
				when(dataService.findAllStatements(2)).thenReturn(statements);
				
				ResultActions response = mockMvc.perform(get("/deal/admin/statement", statementId)
						.param("page", "2"));
				
				response.andExpect(responseBody(objectMapper).containsListAsJson(statements, Statement.class));
			}
		}
	}
}