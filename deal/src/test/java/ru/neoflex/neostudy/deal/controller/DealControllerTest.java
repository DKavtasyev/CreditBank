package ru.neoflex.neostudy.deal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.neoflex.neostudy.common.dto.FinishingRegistrationRequestDto;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.exception.LoanRefusalException;
import ru.neoflex.neostudy.common.util.DtoInitializer;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.common.exception.InvalidPassportDataException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.deal.service.DataService;
import ru.neoflex.neostudy.deal.service.kafka.KafkaService;
import ru.neoflex.neostudy.deal.service.PreScoringService;
import ru.neoflex.neostudy.deal.service.ScoringService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.neoflex.neostudy.deal.custom.ResponseBodyMatcher.responseBody;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = DealController.class)
public class DealControllerTest {
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private PreScoringService preScoringService;
	@MockBean
	private ScoringService scoringService;
	@MockBean
	private DataService dataService;
	@MockBean
	private KafkaService kafkaService;
	
	LoanStatementRequestDto loanStatementRequest;
	LoanOfferDto loanOfferDto;
	FinishingRegistrationRequestDto finishingRegistrationRequest;
	
	@Nested
	@DisplayName("Тестирование метода DealController:createStatement()")
	class TestingCreateStatementMethod {
		@BeforeEach
		void initLoanStatementRequest() {
			loanStatementRequest = DtoInitializer.initLoanStatementRequest();
		}
		
		@Nested
		@DisplayName("Тестирование прослушивания HTTP запросов")
		class TestingListeningHttpRequests {
			@Test
			void getLoanOffers_whenValidInput_returns200() throws Exception {
				mockMvc.perform(post("/deal/statement")
								.contentType("application/json")
								.content(objectMapper.writeValueAsString(loanStatementRequest)))
						.andExpect(status().isOk());
			}
			
			@Test
			void getLoanOffers_whenNotAllowedMethod_returns405() throws Exception {
				mockMvc.perform(get("/deal/statement")
								.contentType("application/json")
								.content(objectMapper.writeValueAsString(loanStatementRequest)))
						.andExpect(status().isMethodNotAllowed());
			}
		}
		
		@Nested
		@DisplayName("Тестирование валидации входных данных")
		class TestingValidation {
			@Nested
			@DisplayName("Тестирование валидации поля amount")
			class TestingValidationOfAmount {
				@Test
				void getLoanOffers_whenAmountIsNull_thenReturns500() throws Exception {
					loanStatementRequest.setAmount(null);
					
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isInternalServerError());
				}
				
				@Test
				void getLoanOffers_whenAmountLess30000_thenReturns500() throws Exception {
					loanStatementRequest.setAmount(BigDecimal.valueOf(29_999));
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isInternalServerError());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля term")
			class TestingValidationOfTerm {
				@Test
				void getLoanOffers_whenTermIsNull_thenReturns500() throws Exception {
					loanStatementRequest.setTerm(null);
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isInternalServerError());
				}
				
				@Test
				void getLoanOffers_whenTermLess6_thenReturns500() throws Exception {
					loanStatementRequest.setTerm(5);
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isInternalServerError());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля firstName")
			class TestingValidationOfFirstName {
				@ParameterizedTest
				@MethodSource("argsProvidedFactory")
				void getLoanOffers_whenFirstNameIsInvalid_thenReturns500(String firstName) throws Exception {
					loanStatementRequest.setFirstName(firstName);
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isInternalServerError());
				}
				
				static Stream<String> argsProvidedFactory() {
					return Stream.of(null, "               ", "A", "1234567890123456789012345678901");
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля lastName")
			class TestingValidationOfLastName {
				@ParameterizedTest
				@MethodSource("argsProvidedFactory")
				void getLoanOffers_whenLastNameIsInvalid_thenReturns500(String lastName) throws Exception {
					loanStatementRequest.setLastName(lastName);
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isInternalServerError());
				}
				
				static Stream<String> argsProvidedFactory() {
					return Stream.of(null, "               ", "A", "1234567890123456789012345678901");
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля middleName")
			class TestingValidationOfMiddleName {
				@Test
				void getLoanOffers_whenMiddleNameLessTwo_thenReturns500() throws Exception {
					loanStatementRequest.setMiddleName("A");
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isInternalServerError());
				}
				
				@Test
				void getLoanOffers_whenMiddleNameMoreThirty_thenReturns500() throws Exception {
					loanStatementRequest.setMiddleName("1234567890123456789012345678901");
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isInternalServerError());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля email")
			class TestingValidationOfEmail {
				@ParameterizedTest
				@MethodSource("argsProvidedFactory")
				void getLoanOffers_whenEmailIsInvalid_thenReturns500(String argument) throws Exception {
					loanStatementRequest.setEmail(argument);
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isInternalServerError());
				}
				
				static Stream<String> argsProvidedFactory() {
					return Stream.of(null, "    ", "1234", "fake", "vasya@", "@mail", "vasya@mail!", "vas @mail");
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля birthdate")
			class TestingValidationOfBirthdate {
				@ParameterizedTest
				@MethodSource("invalidArgsProvidedFactory")
				void getLoanOffers_whenBirthdateIsInvalidOrInappropriate_thenReturns500(LocalDate argument) throws Exception {
					loanStatementRequest.setBirthDate(argument);
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isInternalServerError());
				}
				
				static Stream<LocalDate> invalidArgsProvidedFactory() {
					return Stream.of(null, LocalDate.now().minusYears(18).plusDays(1), LocalDate.now().minusYears(17), LocalDate.now());
				}
				
				@ParameterizedTest
				@MethodSource("validArgsProvidedFactory")
				void getLoanOffers_whenAgeIsIsEqualsOrMoreThanEighteen_thenReturns200(LocalDate argument) throws Exception {
					loanStatementRequest.setBirthDate(argument);
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isOk());
				}
				
				static Stream<LocalDate> validArgsProvidedFactory() {
					return Stream.of(LocalDate.now().minusYears(18), LocalDate.now().minusYears(19), LocalDate.now().minusYears(150));
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля passportSeries")
			class TestingValidationOfPassportSeries {
				
				@ParameterizedTest
				@MethodSource("argsProvidedFactory")
				void getLoanOffers_whenPassportSeriesIsInvalid_thenReturns500(String argument) throws Exception {
					loanStatementRequest.setPassportSeries(argument);
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isInternalServerError());
				}
				
				static Stream<String> argsProvidedFactory() {
					return Stream.of(null, "    ", "123", "12as", "as12", "fake", "12345");
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля passportNumber")
			class TestingValidationOfPassportNumber {
				
				@ParameterizedTest
				@MethodSource("argsProvidedFactory")
				void getLoanOffers_whenPassportNumberIsInvalid_thenReturns500(String argument) throws Exception {
					loanStatementRequest.setPassportNumber(argument);
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isInternalServerError());
				}
				
				static Stream<String> argsProvidedFactory() {
					return Stream.of(null, "      ", "12as12", "as1234", "number", "12345", "1234567");
				}
			}
		}
		
		@Nested
		@DisplayName("Тестирование десериализации входных данных и вызова методов сервисов")
		class TestingDeserialization {
			@Test
			void getLoanOffers_whenValidInput_thenMapsToBusinessModel() throws Exception {
				mockMvc.perform(post("/deal/statement")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(loanStatementRequest)));
				
				ArgumentCaptor<LoanStatementRequestDto> loanStatementRequestDtoCaptor = ArgumentCaptor.forClass(LoanStatementRequestDto.class);
				ArgumentCaptor<Statement> statementCaptor = ArgumentCaptor.forClass(Statement.class);
				assertAll(() -> {
					verify(dataService, times(1)).prepareData(loanStatementRequestDtoCaptor.capture());
					verify(preScoringService, times(1)).getOffers(loanStatementRequestDtoCaptor.capture(), statementCaptor.capture());
					assertThat(loanStatementRequestDtoCaptor.getValue().getAmount()).isEqualByComparingTo(loanStatementRequest.getAmount());
					assertThat(loanStatementRequestDtoCaptor.getValue().getTerm()).isEqualTo(loanStatementRequest.getTerm());
					assertThat(loanStatementRequestDtoCaptor.getValue().getFirstName()).isEqualTo(loanStatementRequest.getFirstName());
					assertThat(loanStatementRequestDtoCaptor.getValue().getLastName()).isEqualTo(loanStatementRequest.getLastName());
					assertThat(loanStatementRequestDtoCaptor.getValue().getMiddleName()).isEqualTo(loanStatementRequest.getMiddleName());
					assertThat(loanStatementRequestDtoCaptor.getValue().getEmail()).isEqualTo(loanStatementRequest.getEmail());
					assertThat(loanStatementRequestDtoCaptor.getValue().getBirthDate()).isEqualTo(loanStatementRequest.getBirthDate());
					assertThat(loanStatementRequestDtoCaptor.getValue().getPassportSeries()).isEqualTo(loanStatementRequest.getPassportSeries());
					assertThat(loanStatementRequestDtoCaptor.getValue().getPassportNumber()).isEqualTo(loanStatementRequest.getPassportNumber());
				});
			}
		}
		
		@Nested
		@DisplayName("Тестирование сериализации посчитанных предложений")
		class TestingSerializationOfLoanOfferDtoList {
			@Test
			void getLoanOffers_whenValidInput_thenMapsToBusinessModel() throws Exception {
				List<LoanOfferDto> offers = DtoInitializer.initOffers();
				Statement statement = new Statement();
				when(dataService.prepareData(any(LoanStatementRequestDto.class))).thenReturn(statement);
				when(preScoringService.getOffers(any(LoanStatementRequestDto.class), any(Statement.class))).thenReturn(offers);
				
				ResultActions response = mockMvc.perform(post("/deal/statement")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(loanStatementRequest)));
				
				response.andExpect(responseBody().containsListAsJson(offers, LoanOfferDto.class));
			}
		}
		
		@Nested
		@DisplayName("Тестирование обработки исключений")
		class TestingExceptions {
			@Test
			void getLoanOffers_whenPassportDataIsInvalid_thenReturn400() throws Exception {
				doThrow(InvalidPassportDataException.class).when(dataService).prepareData(any(LoanStatementRequestDto.class));
				mockMvc.perform(post("/deal/statement")
								.contentType("application/json")
								.content(objectMapper.writeValueAsString(loanStatementRequest)))
						.andExpect(status().isBadRequest());
			}
		}
	}
	
	@Nested
	@DisplayName("Тестирование метода DealController:applyOffer()")
	class TestingApplyOfferMethod {
		@BeforeEach
		void initLoanStatementRequest() {
			loanOfferDto = DtoInitializer.initLoanOfferDto();
		}
		
		@Nested
		@DisplayName("Тестирование прослушивания HTTP запросов")
		class TestingListeningHttpRequests {
			@Test
			void applyOffer_whenValidInput_returns200() throws Exception {
				mockMvc.perform(post("/deal/offer/select")
								.contentType("application/json")
								.content(objectMapper.writeValueAsString(loanOfferDto)))
						.andExpect(status().isOk());
			}
			
			@Test
			void applyOffer_whenNotAllowedMethod_returns405() throws Exception {
				mockMvc.perform(get("/deal/offer/select")
								.contentType("application/json")
								.content(objectMapper.writeValueAsString(loanOfferDto)))
						.andExpect(status().isMethodNotAllowed());
			}
		}
		
		@Nested
		@DisplayName("Тестирование десериализации входных данных и вызова методов сервисов")
		class TestingDeserialization {
			@Test
			void applyOffer_whenValidInput_thenMapsToBusinessModel() throws Exception {
				mockMvc.perform(post("/deal/offer/select")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(loanOfferDto)));
				
				ArgumentCaptor<LoanOfferDto> loanOfferDtoCaptor = ArgumentCaptor.forClass(LoanOfferDto.class);
				assertAll(() -> {
					verify(dataService, times(1)).applyOfferAndSave(loanOfferDtoCaptor.capture());
					verify(kafkaService, times(1)).sendFinishRegistrationRequest(loanOfferDto.getStatementId());
					assertThat(loanOfferDtoCaptor.getValue().getStatementId()).isEqualTo(loanOfferDto.getStatementId());
					assertThat(loanOfferDtoCaptor.getValue().getRequestedAmount()).isEqualByComparingTo(loanOfferDto.getRequestedAmount());
					assertThat(loanOfferDtoCaptor.getValue().getTotalAmount()).isEqualByComparingTo(loanOfferDto.getTotalAmount());
					assertThat(loanOfferDtoCaptor.getValue().getTerm()).isEqualTo(loanOfferDto.getTerm());
					assertThat(loanOfferDtoCaptor.getValue().getMonthlyPayment()).isEqualByComparingTo(loanOfferDto.getMonthlyPayment());
					assertThat(loanOfferDtoCaptor.getValue().getRate()).isEqualByComparingTo(loanOfferDto.getRate());
					assertThat(loanOfferDtoCaptor.getValue().getIsInsuranceEnabled()).isEqualTo(loanOfferDto.getIsInsuranceEnabled());
					assertThat(loanOfferDtoCaptor.getValue().getIsSalaryClient()).isEqualTo(loanOfferDto.getIsSalaryClient());
				});
			}
		}
		
		@Nested
		@DisplayName("Тестирование обработки исключений")
		class TestingExceptions {
			@Test
			void applyOffer_whenStatementNotFound_thenThrowStatementNotFoundException() throws Exception {
				doThrow(StatementNotFoundException.class).when(dataService).applyOfferAndSave(any(LoanOfferDto.class));
				mockMvc.perform(post("/deal/offer/select")
								.contentType("application/json")
								.content(objectMapper.writeValueAsString(loanOfferDto)))
						.andExpect(status().isNotFound());
			}
		}
	}
	
	@Nested
	@DisplayName("Тестирование метода DealController:calculateCredit()")
	class TestingCalculateCreditMethod {
		@BeforeEach
		void initFinishingRegistrationRequest() {
			finishingRegistrationRequest = DtoInitializer.initFinishingRegistrationRequest();
		}
		
		@Nested
		@DisplayName("Тестирование прослушивания HTTP запросов")
		class TestingListeningHttpRequests {
			@Test
			void calculateLoanParameters_whenValidInput_returns200() throws Exception {
				UUID statementId = UUID.randomUUID();
				mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
								.contentType("application/json")
								.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
						.andExpect(status().isOk());
			}
			
			@Test
			void calculateLoanParameters_whenNotAllowedMethod_returns405() throws Exception {
				UUID statementId = UUID.randomUUID();
				mockMvc.perform(get("/deal/calculate/{statementId}", statementId)
								.contentType("application/json")
								.content(objectMapper.writeValueAsString(loanStatementRequest)))
						.andExpect(status().isMethodNotAllowed());
			}
		}
		
		@Nested
		@DisplayName("Тестирование валидации входных данных")
		class TestingValidation {
			@Nested
			@DisplayName("Тестирование валидации поля gender")
			class TestingValidationOfGender {
				@Test
				void calculateLoanParameters_whenGenderIsNull_thenReturns500() throws Exception {
					finishingRegistrationRequest.setGender(null);
					
					UUID statementId = UUID.randomUUID();
					mockMvc.perform(post("/deal/calculate/{statementId}", statementId.toString())
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
							.andExpect(status().isInternalServerError());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля maritalStatus")
			class TestingValidationOfMaritalStatus {
				@Test
				void calculateLoanParameters_whenMaritalStatusIsNull_thenReturns500() throws Exception {
					finishingRegistrationRequest.setMaritalStatus(null);
					
					UUID statementId = UUID.randomUUID();
					mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
							.andExpect(status().isInternalServerError());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля dependentAmount")
			class TestingValidationOfDependentAmount {
				@Test
				void calculateLoanParameters_whenDependentAmountIsNull_thenReturns500() throws Exception {
					finishingRegistrationRequest.setDependentAmount(null);
					
					UUID statementId = UUID.randomUUID();
					mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
							.andExpect(status().isInternalServerError());
				}
				
				@Test
				void calculateLoanParameters_whenDependentAmountIsNegative_thenReturns500() throws Exception {
					finishingRegistrationRequest.setDependentAmount(-5);
					
					UUID statementId = UUID.randomUUID();
					mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
							.andExpect(status().isInternalServerError());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля passportIssueDate")
			class TestingValidationOfPassportIssueDate {
				@Test
				void calculateLoanParameters_whenPassportIssueDateIsNull_thenReturns500() throws Exception {
					finishingRegistrationRequest.setPassportIssueDate(null);
					
					UUID statementId = UUID.randomUUID();
					mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
							.andExpect(status().isInternalServerError());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля passportIssueBranch")
			class TestingValidationOfPassportIssueBranch {
				@Test
				void calculateLoanParameters_whenPassportIssueBranchIsNull_thenReturns500() throws Exception {
					finishingRegistrationRequest.setPassportIssueBranch(null);
					
					UUID statementId = UUID.randomUUID();
					mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
							.andExpect(status().isInternalServerError());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля employment")
			class TestingValidationOfEmployment {
				@Test
				void calculateLoanParameters_whenEmploymentIsNull_thenReturns500() throws Exception {
					finishingRegistrationRequest.setEmployment(null);
					
					UUID statementId = UUID.randomUUID();
					mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
							.andExpect(status().isInternalServerError());
				}
				
				@Nested
				@DisplayName("Тестирование валидации поля employmentStatus")
				class TestingValidationOfEmploymentStatus {
					@Test
					void calculateLoanParameters_whenEmploymentStatusIsNull_thenReturns500() throws Exception {
						finishingRegistrationRequest.getEmployment().setEmploymentStatus(null);
						
						UUID statementId = UUID.randomUUID();
						mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
										.contentType("application/json")
										.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
								.andExpect(status().isInternalServerError());
					}
				}
				
				@Nested
				@DisplayName("Тестирование валидации поля employmentINN")
				class TestingValidationOfEmploymentINN {
					@Test
					void calculateLoanParameters_whenEmploymentInnIsNull_thenReturns500() throws Exception {
						finishingRegistrationRequest.getEmployment().setEmploymentINN(null);
						
						UUID statementId = UUID.randomUUID();
						mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
										.contentType("application/json")
										.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
								.andExpect(status().isInternalServerError());
					}
					
					@ParameterizedTest
					@ValueSource(strings = {"wrong", "   ", "12345678901", "1234567890123", "123412341234a", " 123412341234", "123412341234!"})
					void calculateLoanParameters_whenEmploymentInnIsInvalid_thenReturns500(String employmentINN) throws Exception {
						finishingRegistrationRequest.getEmployment().setEmploymentINN(employmentINN);
						
						UUID statementId = UUID.randomUUID();
						mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
										.contentType("application/json")
										.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
								.andExpect(status().isInternalServerError());
					}
				}
				
				@Nested
				@DisplayName("Тестирование валидации поля salary")
				class TestingValidationOfSalary {
					@Test
					void calculateLoanParameters_whenSalaryIsNull_thenReturns500() throws Exception {
						finishingRegistrationRequest.getEmployment().setSalary(null);
						
						UUID statementId = UUID.randomUUID();
						mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
										.contentType("application/json")
										.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
								.andExpect(status().isInternalServerError());
					}
					
					@Test
					void calculateLoanParameters_whenSalaryIsNegative_thenReturns500() throws Exception {
						finishingRegistrationRequest.getEmployment().setSalary(new BigDecimal(-5));
						
						UUID statementId = UUID.randomUUID();
						mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
										.contentType("application/json")
										.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
								.andExpect(status().isInternalServerError());
					}
				}
				
				@Nested
				@DisplayName("Тестирование валидации поля position")
				class TestingValidationOfPosition {
					@Test
					void calculateLoanParameters_whenPositionIsNull_thenReturns500() throws Exception {
						finishingRegistrationRequest.getEmployment().setPosition(null);
						
						UUID statementId = UUID.randomUUID();
						mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
										.contentType("application/json")
										.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
								.andExpect(status().isInternalServerError());
					}
				}
				
				@Nested
				@DisplayName("Тестирование валидации поля workExperienceTotal")
				class TestingValidationOfWorkExperienceTotal {
					@Test
					void calculateLoanParameters_whenWorkExperienceTotalIsNull_thenReturns500() throws Exception {
						finishingRegistrationRequest.getEmployment().setWorkExperienceTotal(null);
						
						UUID statementId = UUID.randomUUID();
						mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
										.contentType("application/json")
										.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
								.andExpect(status().isInternalServerError());
					}
					
					@Test
					void calculateLoanParameters_whenWorkExperienceTotalIsNegative_thenReturns500() throws Exception {
						finishingRegistrationRequest.getEmployment().setWorkExperienceTotal(-5);
						
						UUID statementId = UUID.randomUUID();
						mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
										.contentType("application/json")
										.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
								.andExpect(status().isInternalServerError());
					}
				}
				
				@Nested
				@DisplayName("Тестирование валидации поля workExperienceCurrent")
				class TestingValidationOfWorkExperienceCurrent {
					@Test
					void calculateLoanParameters_whenWorkExperienceCurrentIsNull_thenReturns500() throws Exception {
						finishingRegistrationRequest.getEmployment().setWorkExperienceCurrent(null);
						
						UUID statementId = UUID.randomUUID();
						mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
										.contentType("application/json")
										.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
								.andExpect(status().isInternalServerError());
					}
					
					@Test
					void calculateLoanParameters_whenWorkExperienceCurrentIsNegative_thenReturns500() throws Exception {
						finishingRegistrationRequest.getEmployment().setWorkExperienceCurrent(-5);
						
						UUID statementId = UUID.randomUUID();
						mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
										.contentType("application/json")
										.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
								.andExpect(status().isInternalServerError());
					}
				}
			}
			
			
		}
		
		@Nested
		@DisplayName("Тестирование десериализации входных данных и вызова методов сервисов")
		class TestingDeserialization {
			@Test
			void getLoanOffers_whenValidInput_thenMapsToBusinessModel() throws Exception {
				UUID statementId = UUID.randomUUID();
				Statement statement = new Statement();
				statement.setStatementId(statementId);
				
				ArgumentCaptor<FinishingRegistrationRequestDto> finishingRegistrationRequestDtoCaptor = ArgumentCaptor.forClass(FinishingRegistrationRequestDto.class);
				ArgumentCaptor<Statement> statementCaptor = ArgumentCaptor.forClass(Statement.class);
				ArgumentCaptor<UUID> statementIdCaptor = ArgumentCaptor.forClass(UUID.class);
				when(dataService.findStatement(statementId)).thenReturn(statement);
				
				mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(finishingRegistrationRequest)));
				
				assertAll(() -> {
					verify(dataService, times(1)).findStatement(statementIdCaptor.capture());
					verify(scoringService, times(1)).scoreAndSaveCredit(finishingRegistrationRequestDtoCaptor.capture(), statementCaptor.capture());
					assertThat(finishingRegistrationRequestDtoCaptor.getValue().getGender()).isEqualTo(finishingRegistrationRequest.getGender());
					assertThat(finishingRegistrationRequestDtoCaptor.getValue().getMaritalStatus()).isEqualTo(finishingRegistrationRequest.getMaritalStatus());
					assertThat(finishingRegistrationRequestDtoCaptor.getValue().getDependentAmount()).isEqualTo(finishingRegistrationRequest.getDependentAmount());
					assertThat(finishingRegistrationRequestDtoCaptor.getValue().getPassportIssueDate()).isEqualTo(finishingRegistrationRequest.getPassportIssueDate());
					assertThat(finishingRegistrationRequestDtoCaptor.getValue().getPassportIssueBranch()).isEqualTo(finishingRegistrationRequest.getPassportIssueBranch());
					assertThat(finishingRegistrationRequestDtoCaptor.getValue().getEmployment().getEmploymentStatus()).isEqualTo(finishingRegistrationRequest.getEmployment().getEmploymentStatus());
					assertThat(finishingRegistrationRequestDtoCaptor.getValue().getEmployment().getEmploymentINN()).isEqualTo(finishingRegistrationRequest.getEmployment().getEmploymentINN());
					assertThat(finishingRegistrationRequestDtoCaptor.getValue().getEmployment().getSalary()).isEqualByComparingTo(finishingRegistrationRequest.getEmployment().getSalary());
					assertThat(finishingRegistrationRequestDtoCaptor.getValue().getEmployment().getPosition()).isEqualTo(finishingRegistrationRequest.getEmployment().getPosition());
					assertThat(finishingRegistrationRequestDtoCaptor.getValue().getEmployment().getWorkExperienceTotal()).isEqualTo(finishingRegistrationRequest.getEmployment().getWorkExperienceTotal());
					assertThat(finishingRegistrationRequestDtoCaptor.getValue().getEmployment().getWorkExperienceCurrent()).isEqualTo(finishingRegistrationRequest.getEmployment().getWorkExperienceCurrent());
					assertThat(finishingRegistrationRequestDtoCaptor.getValue().getAccountNumber()).isEqualTo(finishingRegistrationRequest.getAccountNumber());
				});
			}
			
		}
		
		@Nested
		@DisplayName("Тестирование обработки исключений")
		class TestingExceptions {
			@Test
			void calculateLoanParameters_whenStatementNotFound_thenReturn404() throws Exception {
				UUID statementId = UUID.randomUUID();
				doThrow(StatementNotFoundException.class).when(dataService).findStatement(statementId);
				mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
								.contentType("application/json")
								.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
						.andExpect(status().isNotFound());
			}
			
			@Test
			void calculateLoanParameters_whenLoanRefused_thenReturn406() throws Exception {
				UUID statementId = UUID.randomUUID();
				Statement statement = new Statement();
				statement.setStatementId(statementId);
				when(dataService.findStatement(statementId)).thenReturn(statement);
				doThrow(LoanRefusalException.class).when(scoringService).scoreAndSaveCredit(any(FinishingRegistrationRequestDto.class), any(Statement.class));
				mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
								.contentType("application/json")
								.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
						.andExpect(status().isNotAcceptable());
			}
		}
	}
}
