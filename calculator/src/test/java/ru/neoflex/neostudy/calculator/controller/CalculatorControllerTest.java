package ru.neoflex.neostudy.calculator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
	static ScoringDataDto scoringData;
	
	@Nested
	@DisplayName("Тестирование метода CalculatorController:calculateLoanOffers()")
	class TestingCalculateLoanOffers {
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
		@Disabled
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
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void calculationOfPossibleLoanTerms_whenAmountLess30000_thenReturns400() throws Exception {
					loanStatementRequest.setAmount(BigDecimal.valueOf(29_999));
					mockMvc.perform(post("/calculator/offers")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
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
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void calculationOfPossibleLoanTerms_whenTermLess6_thenReturns400() throws Exception {
					loanStatementRequest.setTerm(5);
					mockMvc.perform(post("/calculator/offers")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля firstName")
			class TestingValidationOfFirstName {
				@Test
				void calculationOfPossibleLoanTerms_whenFirstNameIsNull_thenReturns400() throws Exception {
					loanStatementRequest.setFirstName(null);
					mockMvc.perform(post("/calculator/offers")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void calculationOfPossibleLoanTerms_whenFirstNameIsBlank_thenReturns400() throws Exception {
					loanStatementRequest.setFirstName("               ");
					mockMvc.perform(post("/calculator/offers")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void calculationOfPossibleLoanTerms_whenFirstNameLessTwo_thenReturns400() throws Exception {
					loanStatementRequest.setFirstName("A");
					mockMvc.perform(post("/calculator/offers")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void calculationOfPossibleLoanTerms_whenFirstNameMoreThirty_thenReturns400() throws Exception {
					loanStatementRequest.setFirstName("1234567890123456789012345678901");
					mockMvc.perform(post("/calculator/offers")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля lastName")
			class TestingValidationOfLastName {
				@Test
				void calculationOfPossibleLoanTerms_whenLastNameIsNull_thenReturns400() throws Exception {
					loanStatementRequest.setLastName(null);
					mockMvc.perform(post("/calculator/offers")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void calculationOfPossibleLoanTerms_whenLastNameIsBlank_thenReturns400() throws Exception {
					loanStatementRequest.setLastName("               ");
					mockMvc.perform(post("/calculator/offers")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void calculationOfPossibleLoanTerms_whenLastNameLessTwo_thenReturns400() throws Exception {
					loanStatementRequest.setLastName("A");
					mockMvc.perform(post("/calculator/offers")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void calculationOfPossibleLoanTerms_whenLastNameMoreThirty_thenReturns400() throws Exception {
					loanStatementRequest.setLastName("1234567890123456789012345678901");
					mockMvc.perform(post("/calculator/offers")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
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
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void calculationOfPossibleLoanTerms_whenMiddleNameMoreThirty_thenReturns400() throws Exception {
					loanStatementRequest.setMiddleName("1234567890123456789012345678901");
					mockMvc.perform(post("/calculator/offers")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля email")
			class TestingValidationOfEmail {
				@Test
				void calculationOfPossibleLoanTerms_whenEmailIsNull_thenReturns400() throws Exception {
					loanStatementRequest.setEmail(null);
					mockMvc.perform(post("/calculator/offers")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@ParameterizedTest
				@ValueSource(strings = {"    ", "1234", "fake", "vasya@", "@mail", "vasya@mail!", "vas @mail"})
				void calculationOfPossibleLoanTerms_whenEmailIsInvalid_thenReturns400(String argument) throws Exception {
					loanStatementRequest.setEmail(argument);
					mockMvc.perform(post("/calculator/offers")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля birthdate")
			class TestingValidationOfBirthdate {
				@Test
				void calculationOfPossibleLoanTerms_whenBirthdateIsNull_thenReturns400() throws Exception {
					loanStatementRequest.setBirthDate(null);
					mockMvc.perform(post("/calculator/offers")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void calculationOfPossibleLoanTerms_whenAgeIsLessThanEighteen_thenReturns400() throws Exception {
					loanStatementRequest.setBirthDate(LocalDate.now().minusYears(18).plusDays(1));
					mockMvc.perform(post("/calculator/offers")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля passportSeries")
			class TestingValidationOfPassportSeries {
				@Test
				void calculationOfPossibleLoanTerms_whenPassportSeriesIsNull_thenReturns400() throws Exception {
					loanStatementRequest.setPassportSeries(null);
					mockMvc.perform(post("/calculator/offers")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@ParameterizedTest
				@ValueSource(strings = {"    ", "12as", "as12", "fake"})
				void calculationOfPossibleLoanTerms_whenPassportSeriesIsNotDigits_thenReturns400(String argument) throws Exception {
					loanStatementRequest.setPassportSeries(argument);
					mockMvc.perform(post("/calculator/offers")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void calculationOfPossibleLoanTerms_whenPassportSeriesLessFour_thenReturns400() throws Exception {
					loanStatementRequest.setPassportSeries("123");
					mockMvc.perform(post("/calculator/offers")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void calculationOfPossibleLoanTerms_whenPassportSeriesMoreFour_thenReturns400() throws Exception {
					loanStatementRequest.setPassportSeries("12345");
					mockMvc.perform(post("/calculator/offers")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля passportNumber")
			class TestingValidationOfPassportNumber {
				@Test
				void calculationOfPossibleLoanTerms_whenPassportNumberIsNull_thenReturns400() throws Exception {
					loanStatementRequest.setPassportNumber(null);
					mockMvc.perform(post("/calculator/offers")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@ParameterizedTest
				@ValueSource(strings = {"      ", "12as12", "as1234", "number"})
				void calculationOfPossibleLoanTerms_whenPassportNumberIsNotDigits_thenReturns400(String argument) throws Exception {
					loanStatementRequest.setPassportNumber(argument);
					mockMvc.perform(post("/calculator/offers")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void calculationOfPossibleLoanTerms_whenPassportNumberLessFour_thenReturns400() throws Exception {
					loanStatementRequest.setPassportNumber("12345");
					mockMvc.perform(post("/calculator/offers")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void calculationOfPossibleLoanTerms_whenPassportNumberMoreFour_thenReturns400() throws Exception {
					loanStatementRequest.setPassportNumber("1234567");
					mockMvc.perform(post("/calculator/offers")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
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
					assertThat(statementCaptor.getValue().getAmount().compareTo(loanStatementRequest.getAmount())).isEqualTo(0);
					assertThat(statementCaptor.getValue().getTerm()).isEqualTo(loanStatementRequest.getTerm());
					assertThat(statementCaptor.getValue().getFirstName()).isEqualTo(loanStatementRequest.getFirstName());
					assertThat(statementCaptor.getValue().getLastName()).isEqualTo(loanStatementRequest.getLastName());
					assertThat(statementCaptor.getValue().getMiddleName()).isEqualTo(loanStatementRequest.getMiddleName());
					assertThat(statementCaptor.getValue().getEmail()).isEqualTo(loanStatementRequest.getEmail());
					assertThat(statementCaptor.getValue().getBirthDate().equals(loanStatementRequest.getBirthDate())).isTrue();
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
	@DisplayName("Тестирование метода CalculatorController:calculateLoanTerms()")
	class TestingCalculateLoanTerms {
		@BeforeAll
		static void initScoringData() {
			scoringData = DtoInitializer.initScoringData();
		}
		
		@Nested
		@DisplayName("Тестирование прослушивания HTTP запросов")
		class TestingListeningHttpRequests {
			@Test
			void fullCalculationOfLoanTerms_whenValidInput_returns200() throws Exception {
				mockMvc.perform(post("/calculator/calc")
								.contentType("application/json")
								.content(objectMapper.writeValueAsString(scoringData)))
						.andExpect(status().isOk());
			}
			
			@Test
			void fullCalculationOfLoanTerms_whenNotAllowedMethod_returns405() throws Exception {
				mockMvc.perform(get("/calculator/calc")
								.contentType("application/json")
								.content(objectMapper.writeValueAsString(scoringData)))
						.andExpect(status().isMethodNotAllowed());
			}
		}
		
		@Nested
		@DisplayName("Тестирование десериализации входных данных и вызова метода сервиса")
		class TestingDeserialization {
			@Test
			void fullCalculationOfLoanTerms_whenValidInput_thenMapsToBusinessModel() throws Exception {
				mockMvc.perform(post("/calculator/calc")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(scoringData)));
				
				ArgumentCaptor<ScoringDataDto> creditCaptor = ArgumentCaptor.forClass(ScoringDataDto.class);
				assertAll(() -> {
					verify(calculatorServiceMock, times(1)).score(creditCaptor.capture());
					assertThat(creditCaptor.getValue().getAmount().compareTo(scoringData.getAmount())).isEqualTo(0);
					assertThat(creditCaptor.getValue().getTerm()).isEqualTo(scoringData.getTerm());
					assertThat(creditCaptor.getValue().getFirstName()).isEqualTo(scoringData.getFirstName());
					assertThat(creditCaptor.getValue().getLastName()).isEqualTo(scoringData.getLastName());
					assertThat(creditCaptor.getValue().getMiddleName()).isEqualTo(scoringData.getMiddleName());
					assertThat(creditCaptor.getValue().getGender()).isEqualTo(scoringData.getGender());
					assertThat(creditCaptor.getValue().getBirthdate()).isEqualTo(scoringData.getBirthdate());
					assertThat(creditCaptor.getValue().getPassportSeries()).isEqualTo(scoringData.getPassportSeries());
					assertThat(creditCaptor.getValue().getPassportNumber()).isEqualTo(scoringData.getPassportNumber());
					assertThat(creditCaptor.getValue().getPassportIssueDate()).isEqualTo(scoringData.getPassportIssueDate());
					assertThat(creditCaptor.getValue().getPassportIssueBranch()).isEqualTo(scoringData.getPassportIssueBranch());
					assertThat(creditCaptor.getValue().getMaritalStatus()).isEqualTo(scoringData.getMaritalStatus());
					assertThat(creditCaptor.getValue().getDependentAmount()).isEqualTo(scoringData.getDependentAmount());
					assertThat(creditCaptor.getValue().getEmployment().getEmploymentStatus()).isEqualTo(scoringData.getEmployment().getEmploymentStatus());
					assertThat(creditCaptor.getValue().getEmployment().getEmploymentINN()).isEqualTo(scoringData.getEmployment().getEmploymentINN());
					assertThat(creditCaptor.getValue().getEmployment().getSalary().compareTo(scoringData.getEmployment().getSalary())).isZero();
					assertThat(creditCaptor.getValue().getEmployment().getPosition()).isEqualTo(scoringData.getEmployment().getPosition());
					assertThat(creditCaptor.getValue().getEmployment().getWorkExperienceTotal()).isEqualTo(scoringData.getEmployment().getWorkExperienceTotal());
					assertThat(creditCaptor.getValue().getEmployment().getWorkExperienceCurrent()).isEqualTo(scoringData.getEmployment().getWorkExperienceCurrent());
					assertThat(creditCaptor.getValue().getAccountNumber()).isEqualTo(scoringData.getAccountNumber());
					assertThat(creditCaptor.getValue().getIsInsuranceEnabled()).isEqualTo(scoringData.getIsInsuranceEnabled());
					assertThat(creditCaptor.getValue().getIsSalaryClient()).isEqualTo(scoringData.getIsSalaryClient());
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
								.content(objectMapper.writeValueAsString(scoringData)))
						.andExpect(status().isNotAcceptable());
			}
		}
	}
}
