package ru.neoflex.neostudy.calculator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.neoflex.neostudy.calculator.service.CalculatorService;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.dto.ScoringDataDto;
import ru.neoflex.neostudy.common.exception.LoanRefusalException;
import ru.neoflex.neostudy.common.util.DtoInitializer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.neoflex.neostudy.calculator.custom.ResponseBodyMatcher.responseBody;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CalculatorController.class)
public class CalculatorControllerTest {
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private CalculatorService calculatorServiceMock;
	
	LoanStatementRequestDto loanStatementRequest;
	ScoringDataDto scoringDataDto;
	
	@Nested
	@DisplayName("Тестирование метода CalculatorController:generateOffers()")
	class TestingGenerateOffers {
		@BeforeEach
		void initLoanStatementRequest() {
			loanStatementRequest = DtoInitializer.initLoanStatementRequest();
		}
		
		@Nested
		@DisplayName("Тестирование прослушивания HTTP запросов")
		class TestingListeningHttpRequests {
			@Test
			void calculationOfPossibleLoanTerms_whenValidInput_returns200() throws Exception {
				mockMvc.perform(post("/calculator/offers")
								.contentType("application/json")
								.content(objectMapper.writeValueAsString(loanStatementRequest)))
						.andExpect(status().isOk());
			}
			
			@Test
			void calculationOfPossibleLoanTerms_whenNotAllowedMethod_returns405() throws Exception {
				mockMvc.perform(get("/calculator/offers")
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
				void calculationOfPossibleLoanTerms_whenAmountIsNull_thenReturns400() throws Exception {
					loanStatementRequest.setAmount(null);
					mockMvc.perform(post("/calculator/offers")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isInternalServerError());
				}
				
				@Test
				void calculationOfPossibleLoanTerms_whenAmountLess30000_thenReturns400() throws Exception {
					loanStatementRequest.setAmount(BigDecimal.valueOf(29_999));
					mockMvc.perform(post("/calculator/offers")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isInternalServerError());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля term")
			class TestingValidationOfTerm {
				@Test
				void calculationOfPossibleLoanTerms_whenTermIsNull_thenReturns400() throws Exception {
					loanStatementRequest.setTerm(null);
					mockMvc.perform(post("/calculator/offers")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isInternalServerError());
				}
				
				@Test
				void calculationOfPossibleLoanTerms_whenTermLess6_thenReturns400() throws Exception {
					loanStatementRequest.setTerm(5);
					mockMvc.perform(post("/calculator/offers")
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
				void calculationOfPossibleLoanTerms_whenFirstNameIsInvalid_thenReturns400(String firstName) throws Exception {
					loanStatementRequest.setFirstName(firstName);
					mockMvc.perform(post("/calculator/offers")
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
				void calculationOfPossibleLoanTerms_whenLastNameIsInvalid_thenReturns400(String lastName) throws Exception {
					loanStatementRequest.setLastName(lastName);
					mockMvc.perform(post("/calculator/offers")
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
				void calculationOfPossibleLoanTerms_whenMiddleNameLessTwo_thenReturns400() throws Exception {
					loanStatementRequest.setMiddleName("A");
					mockMvc.perform(post("/calculator/offers")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isInternalServerError());
				}
				
				@Test
				void calculationOfPossibleLoanTerms_whenMiddleNameMoreThirty_thenReturns400() throws Exception {
					loanStatementRequest.setMiddleName("1234567890123456789012345678901");
					mockMvc.perform(post("/calculator/offers")
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
				void calculationOfPossibleLoanTerms_whenEmailIsInvalid_thenReturns400(String argument) throws Exception {
					loanStatementRequest.setEmail(argument);
					mockMvc.perform(post("/calculator/offers")
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
				void calculationOfPossibleLoanTerms_whenBirthdateIsInvalid_thenReturns500(LocalDate date) throws Exception {
					loanStatementRequest.setBirthDate(date);
					mockMvc.perform(post("/calculator/offers")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isInternalServerError());
				}
				
				static Stream<LocalDate> invalidArgsProvidedFactory() {
					return Stream.of(null, LocalDate.now().minusYears(18).plusDays(1), LocalDate.now().minusYears(17), LocalDate.now());
				}
				
				@ParameterizedTest
				@MethodSource("validArgsProvidedFactory")
				void calculationOfPossibleLoanTerms_whenAgeIsEqualsOrMoreThanEighteen_thenReturns200(LocalDate date) throws Exception {
					loanStatementRequest.setBirthDate(date);
					mockMvc.perform(post("/calculator/offers")
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
				void calculationOfPossibleLoanTerms_whenPassportSeriesIsInvalid_thenReturns500(String argument) throws Exception {
					loanStatementRequest.setPassportSeries(argument);
					mockMvc.perform(post("/calculator/offers")
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
				void calculationOfPossibleLoanTerms_whenPassportNumberIsInvalid_thenReturns500(String argument) throws Exception {
					loanStatementRequest.setPassportNumber(argument);
					mockMvc.perform(post("/calculator/offers")
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
		@DisplayName("Тестирование десериализации входных данных и вызова метода сервиса")
		class TestingDeserialization {
			@Test
			void calculationOfPossibleLoanTerms_whenValidInput_thenMapsToBusinessModel() throws Exception {
				mockMvc.perform(post("/calculator/offers")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(loanStatementRequest)));
				
				ArgumentCaptor<LoanStatementRequestDto> statementCaptor = ArgumentCaptor.forClass(LoanStatementRequestDto.class);
				assertAll(() -> {
					verify(calculatorServiceMock, times(1)).preScore(statementCaptor.capture());
					assertThat(statementCaptor.getValue().getAmount()).isEqualTo(loanStatementRequest.getAmount());
					assertThat(statementCaptor.getValue().getTerm()).isEqualTo(loanStatementRequest.getTerm());
					assertThat(statementCaptor.getValue().getFirstName()).isEqualTo(loanStatementRequest.getFirstName());
					assertThat(statementCaptor.getValue().getLastName()).isEqualTo(loanStatementRequest.getLastName());
					assertThat(statementCaptor.getValue().getMiddleName()).isEqualTo(loanStatementRequest.getMiddleName());
					assertThat(statementCaptor.getValue().getEmail()).isEqualTo(loanStatementRequest.getEmail());
					assertThat(statementCaptor.getValue().getBirthDate()).isEqualTo(loanStatementRequest.getBirthDate());
					assertThat(statementCaptor.getValue().getPassportSeries()).isEqualTo(loanStatementRequest.getPassportSeries());
					assertThat(statementCaptor.getValue().getPassportNumber()).isEqualTo(loanStatementRequest.getPassportNumber());
				});
			}
		}
		
		@Nested
		@DisplayName("Тестирование сериализации посчитанных предложений")
		class TestingSerializationOfLoanOfferDtoList {
			@Test
			void calculationOfPossibleLoanTerms_whenValidInput_thenMapsToBusinessModel() throws Exception {
				List<LoanOfferDto> offers = DtoInitializer.initOffers();
				when(calculatorServiceMock.preScore(any(LoanStatementRequestDto.class))).thenReturn(offers);
				
				ResultActions request = mockMvc.perform(post("/calculator/offers")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(loanStatementRequest)));
				
				request.andExpect(responseBody().containsListAsJson(offers, LoanOfferDto.class));
			}
		}
	}
	
	@Nested
	@DisplayName("Тестирование метода CalculatorController:calculateCredit()")
	class TestingCalculateCredit {
		@BeforeEach
		void initScoringData() {
			scoringDataDto = DtoInitializer.initScoringData();
		}
		
		@Nested
		@DisplayName("Тестирование прослушивания HTTP запросов")
		class TestingListeningHttpRequests {
			@Test
			void fullCalculationOfLoanTerms_whenValidInput_returns200() throws Exception {
				mockMvc.perform(post("/calculator/calc")
								.contentType("application/json")
								.content(objectMapper.writeValueAsString(scoringDataDto)))
						.andExpect(status().isOk());
			}
			
			@Test
			void fullCalculationOfLoanTerms_whenNotAllowedMethod_returns405() throws Exception {
				mockMvc.perform(get("/calculator/calc")
								.contentType("application/json")
								.content(objectMapper.writeValueAsString(scoringDataDto)))
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
				void calculationOfPossibleLoanTerms_whenAmountIsNull_thenReturns400() throws Exception {
					scoringDataDto.setAmount(null);
					mockMvc.perform(post("/calculator/calc")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(scoringDataDto)))
							.andExpect(status().isInternalServerError());
				}
				
				@Test
				void calculationOfPossibleLoanTerms_whenAmountLess30000_thenReturns400() throws Exception {
					scoringDataDto.setAmount(BigDecimal.valueOf(29_999));
					mockMvc.perform(post("/calculator/calc")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(scoringDataDto)))
							.andExpect(status().isInternalServerError());
				}
				
				@Test
				void calculationOfPossibleLoanTerms_whenAmountIs30000_thenReturns200() throws Exception {
					scoringDataDto.setAmount(BigDecimal.valueOf(30_000));
					mockMvc.perform(post("/calculator/calc")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(scoringDataDto)))
							.andExpect(status().isOk());
				}
				
				
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля term")
			class TestingValidationOfTerm {
				@Test
				void calculationOfPossibleLoanTerms_whenTermIsNull_thenReturns400() throws Exception {
					scoringDataDto.setTerm(null);
					mockMvc.perform(post("/calculator/calc")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(scoringDataDto)))
							.andExpect(status().isInternalServerError());
				}
				
				@Test
				void calculationOfPossibleLoanTerms_whenTermLess6_thenReturns400() throws Exception {
					scoringDataDto.setTerm(5);
					mockMvc.perform(post("/calculator/calc")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(scoringDataDto)))
							.andExpect(status().isInternalServerError());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля firstName")
			class TestingValidationOfFirstName {
				@ParameterizedTest
				@MethodSource("argsProvidedFactory")
				void calculationOfPossibleLoanTerms_whenFirstNameIsInvalid_thenReturns400(String firstName) throws Exception {
					scoringDataDto.setFirstName(firstName);
					mockMvc.perform(post("/calculator/calc")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(scoringDataDto)))
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
				void calculationOfPossibleLoanTerms_whenLastNameIsInvalid_thenReturns400(String lastName) throws Exception {
					scoringDataDto.setLastName(lastName);
					mockMvc.perform(post("/calculator/calc")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(scoringDataDto)))
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
				void calculationOfPossibleLoanTerms_whenMiddleNameLessTwo_thenReturns400() throws Exception {
					scoringDataDto.setMiddleName("A");
					mockMvc.perform(post("/calculator/calc")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(scoringDataDto)))
							.andExpect(status().isInternalServerError());
				}
				
				@Test
				void calculationOfPossibleLoanTerms_whenMiddleNameMoreThirty_thenReturns400() throws Exception {
					scoringDataDto.setMiddleName("1234567890123456789012345678901");
					mockMvc.perform(post("/calculator/calc")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(scoringDataDto)))
							.andExpect(status().isInternalServerError());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля gender")
			class TestingValidationOfGender {
				@Test
				void calculateCredit_whenGenderIsNull_thenReturns500() throws Exception {
					scoringDataDto.setGender(null);
					
					mockMvc.perform(post("/calculator/offers")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(scoringDataDto)))
							.andExpect(status().isInternalServerError());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля birthdate")
			class TestingValidationOfBirthdate {
				@ParameterizedTest
				@MethodSource("invalidArgsProvidedFactory")
				void calculationOfPossibleLoanTerms_whenBirthdateIsInvalid_thenReturns500(LocalDate date) throws Exception {
					scoringDataDto.setBirthdate(date);
					mockMvc.perform(post("/calculator/calc")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(scoringDataDto)))
							.andExpect(status().isInternalServerError());
				}
				
				static Stream<LocalDate> invalidArgsProvidedFactory() {
					return Stream.of(null, LocalDate.now().minusYears(18).plusDays(1), LocalDate.now().minusYears(17), LocalDate.now());
				}
				
				@ParameterizedTest
				@MethodSource("validArgsProvidedFactory")
				void calculationOfPossibleLoanTerms_whenAgeIsEqualsOrMoreThanEighteen_thenReturns200(LocalDate date) throws Exception {
					scoringDataDto.setBirthdate(date);
					mockMvc.perform(post("/calculator/calc")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(scoringDataDto)))
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
				void calculationOfPossibleLoanTerms_whenPassportSeriesIsInvalid_thenReturns500(String argument) throws Exception {
					scoringDataDto.setPassportSeries(argument);
					mockMvc.perform(post("/calculator/calc")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(scoringDataDto)))
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
				void calculationOfPossibleLoanTerms_whenPassportNumberIsInvalid_thenReturns500(String argument) throws Exception {
					scoringDataDto.setPassportNumber(argument);
					mockMvc.perform(post("/calculator/calc")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(scoringDataDto)))
							.andExpect(status().isInternalServerError());
				}
				
				static Stream<String> argsProvidedFactory() {
					return Stream.of(null, "      ", "12as12", "as1234", "number", "12345", "1234567");
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля passportIssueDate")
			class TestingValidationOfPassportIssueDate {
				@Test
				void calculateCredit_whenPassportIssueDateIsNull_thenReturns500() throws Exception {
					scoringDataDto.setPassportIssueDate(null);
					
					mockMvc.perform(post("/calculator/calc")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(scoringDataDto)))
							.andExpect(status().isInternalServerError());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля passportIssueBranch")
			class TestingValidationOfPassportIssueBranch {
				@Test
				void calculateCredit_whenPassportIssueBranchIsNull_thenReturns500() throws Exception {
					scoringDataDto.setPassportIssueBranch(null);
					
					mockMvc.perform(post("/calculator/calc")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(scoringDataDto)))
							.andExpect(status().isInternalServerError());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля maritalStatus")
			class TestingValidationOfMaritalStatus {
				@Test
				void calculateCredit_whenMaritalStatusIsNull_thenReturns500() throws Exception {
					scoringDataDto.setMaritalStatus(null);
					
					mockMvc.perform(post("/calculator/calc")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(scoringDataDto)))
							.andExpect(status().isInternalServerError());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля dependentAmount")
			class TestingValidationOfDependentAmount {
				@Test
				void calculateCredit_whenDependentAmountIsNull_thenReturns500() throws Exception {
					scoringDataDto.setDependentAmount(null);
					
					mockMvc.perform(post("/calculator/calc")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(scoringDataDto)))
							.andExpect(status().isInternalServerError());
				}
				
				@Test
				void calculateCredit_whenDependentAmountIsNegative_thenReturns500() throws Exception {
					scoringDataDto.setDependentAmount(-5);
					
					mockMvc.perform(post("/calculator/calc")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(scoringDataDto)))
							.andExpect(status().isInternalServerError());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля employment")
			class TestingValidationOfEmployment {
				@Test
				void calculateCredit_whenEmploymentIsNull_thenReturns500() throws Exception {
					scoringDataDto.setEmployment(null);
					
					mockMvc.perform(post("/calculator/calc")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(scoringDataDto)))
							.andExpect(status().isInternalServerError());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля accountNumber")
			class TestingValidationOfAccountNumber {
				@ParameterizedTest
				@MethodSource("argsProvidedFactory")
				void calculationOfPossibleLoanTerms_whenPassportNumberIsInvalid_thenReturns500(String argument) throws Exception {
					scoringDataDto.setAccountNumber(argument);
					mockMvc.perform(post("/calculator/calc")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(scoringDataDto)))
							.andExpect(status().isInternalServerError());
				}
				
				static Stream<String> argsProvidedFactory() {
					return Stream.of(null, "      ", "12as12", "as1234", "number");
				}
			}
		}
		
		@Nested
		@DisplayName("Тестирование десериализации входных данных и вызова метода сервиса")
		class TestingDeserialization {
			@Test
			void fullCalculationOfLoanTerms_whenValidInput_thenMapsToBusinessModel() throws Exception {
				mockMvc.perform(post("/calculator/calc")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(scoringDataDto)));
				
				ArgumentCaptor<ScoringDataDto> creditCaptor = ArgumentCaptor.forClass(ScoringDataDto.class);
				assertAll(() -> {
					verify(calculatorServiceMock, times(1)).score(creditCaptor.capture());
					assertThat(creditCaptor.getValue().getAmount()).isEqualTo(scoringDataDto.getAmount());
					assertThat(creditCaptor.getValue().getTerm()).isEqualTo(scoringDataDto.getTerm());
					assertThat(creditCaptor.getValue().getFirstName()).isEqualTo(scoringDataDto.getFirstName());
					assertThat(creditCaptor.getValue().getLastName()).isEqualTo(scoringDataDto.getLastName());
					assertThat(creditCaptor.getValue().getMiddleName()).isEqualTo(scoringDataDto.getMiddleName());
					assertThat(creditCaptor.getValue().getGender()).isEqualTo(scoringDataDto.getGender());
					assertThat(creditCaptor.getValue().getBirthdate()).isEqualTo(scoringDataDto.getBirthdate());
					assertThat(creditCaptor.getValue().getPassportSeries()).isEqualTo(scoringDataDto.getPassportSeries());
					assertThat(creditCaptor.getValue().getPassportNumber()).isEqualTo(scoringDataDto.getPassportNumber());
					assertThat(creditCaptor.getValue().getPassportIssueDate()).isEqualTo(scoringDataDto.getPassportIssueDate());
					assertThat(creditCaptor.getValue().getPassportIssueBranch()).isEqualTo(scoringDataDto.getPassportIssueBranch());
					assertThat(creditCaptor.getValue().getMaritalStatus()).isEqualTo(scoringDataDto.getMaritalStatus());
					assertThat(creditCaptor.getValue().getDependentAmount()).isEqualTo(scoringDataDto.getDependentAmount());
					assertThat(creditCaptor.getValue().getEmployment().getEmploymentStatus()).isEqualTo(scoringDataDto.getEmployment().getEmploymentStatus());
					assertThat(creditCaptor.getValue().getEmployment().getEmploymentINN()).isEqualTo(scoringDataDto.getEmployment().getEmploymentINN());
					assertThat(creditCaptor.getValue().getEmployment().getSalary()).isEqualTo(scoringDataDto.getEmployment().getSalary());
					assertThat(creditCaptor.getValue().getEmployment().getPosition()).isEqualTo(scoringDataDto.getEmployment().getPosition());
					assertThat(creditCaptor.getValue().getEmployment().getWorkExperienceTotal()).isEqualTo(scoringDataDto.getEmployment().getWorkExperienceTotal());
					assertThat(creditCaptor.getValue().getEmployment().getWorkExperienceCurrent()).isEqualTo(scoringDataDto.getEmployment().getWorkExperienceCurrent());
					assertThat(creditCaptor.getValue().getAccountNumber()).isEqualTo(scoringDataDto.getAccountNumber());
					assertThat(creditCaptor.getValue().getIsInsuranceEnabled()).isEqualTo(scoringDataDto.getIsInsuranceEnabled());
					assertThat(creditCaptor.getValue().getIsSalaryClient()).isEqualTo(scoringDataDto.getIsSalaryClient());
				});
			}
		}
		
		@Nested
		@DisplayName("Тестирование обработки исключений")
		class TestingExceptions {
			@Test
			void fullCalculationOfLoanTerms_whenNotSuitableInput_thenThrowLoanRefusalException() throws Exception {
				when(calculatorServiceMock.score(any(ScoringDataDto.class))).thenThrow(LoanRefusalException.class);
				mockMvc.perform(post("/calculator/calc")
								.contentType("application/json")
								.content(objectMapper.writeValueAsString(scoringDataDto)))
						.andExpect(status().isNotAcceptable());
			}
		}
	}
}
