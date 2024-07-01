package ru.neoflex.neostudy.deal.controller;

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
import ru.neoflex.neostudy.common.dto.FinishingRegistrationRequestDto;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.util.DtoInitializer;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.exception.InvalidPassportDataException;
import ru.neoflex.neostudy.deal.exception.StatementNotFoundException;
import ru.neoflex.neostudy.deal.service.DataService;
import ru.neoflex.neostudy.deal.service.PreScoringService;
import ru.neoflex.neostudy.deal.service.ScoringService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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
	
	LoanStatementRequestDto loanStatementRequest;
	LoanOfferDto loanOfferDto;
	FinishingRegistrationRequestDto finishingRegistrationRequest;
	
	@Nested
	@DisplayName("Тестирование метода DealController:getLoanOffers()")
	class TestingGetLoanOffersMethod {
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
		@Disabled
		class TestingValidation {
			@Nested
			@DisplayName("Тестирование валидации поля amount")
			class TestingValidationOfAmount {
				@Test
				void getLoanOffers_whenAmountIsNull_thenReturns400() throws Exception {
					loanStatementRequest.setAmount(null);
					
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void getLoanOffers_whenAmountLess30000_thenReturns400() throws Exception {
					loanStatementRequest.setAmount(BigDecimal.valueOf(29_999));
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля term")
			class TestingValidationOfTerm {
				@Test
				void getLoanOffers_whenTermIsNull_thenReturns400() throws Exception {
					loanStatementRequest.setTerm(null);
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void getLoanOffers_whenTermLess6_thenReturns400() throws Exception {
					loanStatementRequest.setTerm(5);
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля firstName")
			class TestingValidationOfFirstName {
				@Test
				void getLoanOffers_whenFirstNameIsNull_thenReturns400() throws Exception {
					loanStatementRequest.setFirstName(null);
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void getLoanOffers_whenFirstNameIsBlank_thenReturns400() throws Exception {
					loanStatementRequest.setFirstName("               ");
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void getLoanOffers_whenFirstNameLessTwo_thenReturns400() throws Exception {
					loanStatementRequest.setFirstName("A");
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void getLoanOffers_whenFirstNameMoreThirty_thenReturns400() throws Exception {
					loanStatementRequest.setFirstName("1234567890123456789012345678901");
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля lastName")
			class TestingValidationOfLastName {
				@Test
				void getLoanOffers_whenLastNameIsNull_thenReturns400() throws Exception {
					loanStatementRequest.setLastName(null);
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void getLoanOffers_whenLastNameIsBlank_thenReturns400() throws Exception {
					loanStatementRequest.setLastName("               ");
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void getLoanOffers_whenLastNameLessTwo_thenReturns400() throws Exception {
					loanStatementRequest.setLastName("A");
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void getLoanOffers_whenLastNameMoreThirty_thenReturns400() throws Exception {
					loanStatementRequest.setLastName("1234567890123456789012345678901");
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля middleName")
			class TestingValidationOfMiddleName {
				@Test
				void getLoanOffers_whenMiddleNameLessTwo_thenReturns400() throws Exception {
					loanStatementRequest.setMiddleName("A");
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void getLoanOffers_whenMiddleNameMoreThirty_thenReturns400() throws Exception {
					loanStatementRequest.setMiddleName("1234567890123456789012345678901");
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля email")
			class TestingValidationOfEmail {
				@Test
				void getLoanOffers_whenEmailIsNull_thenReturns400() throws Exception {
					loanStatementRequest.setEmail(null);
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@ParameterizedTest
				@ValueSource(strings = {"    ", "1234", "fake", "vasya@", "@mail", "vasya@mail!", "vas @mail"})
				void getLoanOffers_whenEmailIsInvalid_thenReturns400(String argument) throws Exception {
					loanStatementRequest.setEmail(argument);
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля birthdate")
			class TestingValidationOfBirthdate {
				@Test
				void getLoanOffers_whenBirthdateIsNull_thenReturns400() throws Exception {
					loanStatementRequest.setBirthDate(null);
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void getLoanOffers_whenAgeIsLessThanEighteen_thenReturns400() throws Exception {
					loanStatementRequest.setBirthDate(LocalDate.now().minusYears(18).plusDays(1));
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля passportSeries")
			class TestingValidationOfPassportSeries {
				@Test
				void getLoanOffers_whenPassportSeriesIsNull_thenReturns400() throws Exception {
					loanStatementRequest.setPassportSeries(null);
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@ParameterizedTest
				@ValueSource(strings = {"    ", "12as", "as12", "fake"})
				void getLoanOffers_whenPassportSeriesIsNotDigits_thenReturns400(String argument) throws Exception {
					loanStatementRequest.setPassportSeries(argument);
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void getLoanOffers_whenPassportSeriesLessFour_thenReturns400() throws Exception {
					loanStatementRequest.setPassportSeries("123");
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void getLoanOffers_whenPassportSeriesMoreFour_thenReturns400() throws Exception {
					loanStatementRequest.setPassportSeries("12345");
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля passportNumber")
			class TestingValidationOfPassportNumber {
				@Test
				void getLoanOffers_whenPassportNumberIsNull_thenReturns400() throws Exception {
					loanStatementRequest.setPassportNumber(null);
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@ParameterizedTest
				@ValueSource(strings = {"      ", "12as12", "as1234", "number"})
				void getLoanOffers_whenPassportNumberIsNotDigits_thenReturns400(String argument) throws Exception {
					loanStatementRequest.setPassportNumber(argument);
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void getLoanOffers_whenPassportNumberLessFour_thenReturns400() throws Exception {
					loanStatementRequest.setPassportNumber("12345");
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void getLoanOffers_whenPassportNumberMoreFour_thenReturns400() throws Exception {
					loanStatementRequest.setPassportNumber("1234567");
					mockMvc.perform(post("/deal/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequest)))
							.andExpect(status().isBadRequest());
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
					verify(dataService, times(1)).writeData(loanStatementRequestDtoCaptor.capture());
					verify(preScoringService, times(1)).getOffers(loanStatementRequestDtoCaptor.capture(), statementCaptor.capture());
					assertThat(loanStatementRequestDtoCaptor.getValue().getAmount().compareTo(loanStatementRequest.getAmount())).isEqualTo(0);
					assertThat(loanStatementRequestDtoCaptor.getValue().getTerm()).isEqualTo(loanStatementRequest.getTerm());
					assertThat(loanStatementRequestDtoCaptor.getValue().getFirstName()).isEqualTo(loanStatementRequest.getFirstName());
					assertThat(loanStatementRequestDtoCaptor.getValue().getLastName()).isEqualTo(loanStatementRequest.getLastName());
					assertThat(loanStatementRequestDtoCaptor.getValue().getMiddleName()).isEqualTo(loanStatementRequest.getMiddleName());
					assertThat(loanStatementRequestDtoCaptor.getValue().getEmail()).isEqualTo(loanStatementRequest.getEmail());
					assertThat(loanStatementRequestDtoCaptor.getValue().getBirthDate().equals(loanStatementRequest.getBirthDate())).isTrue();
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
				when(dataService.writeData(any(LoanStatementRequestDto.class))).thenReturn(statement);
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
				doThrow(InvalidPassportDataException.class).when(dataService).writeData(any(LoanStatementRequestDto.class));
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
					verify(dataService, times(1)).updateStatement(loanOfferDtoCaptor.capture());
					assertThat(loanOfferDtoCaptor.getValue().getStatementId().toString()).isEqualTo(loanOfferDto.getStatementId().toString());
					assertThat(loanOfferDtoCaptor.getValue().getRequestedAmount().compareTo(loanOfferDto.getRequestedAmount())).isEqualTo(0);
					assertThat(loanOfferDtoCaptor.getValue().getTotalAmount().compareTo(loanOfferDto.getTotalAmount())).isEqualTo(0);
					assertThat(loanOfferDtoCaptor.getValue().getTerm()).isEqualTo(loanOfferDto.getTerm());
					assertThat(loanOfferDtoCaptor.getValue().getMonthlyPayment().compareTo(loanOfferDto.getMonthlyPayment())).isEqualTo(0);
					assertThat(loanOfferDtoCaptor.getValue().getRate().compareTo(loanOfferDto.getRate())).isEqualTo(0);
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
				doThrow(StatementNotFoundException.class).when(dataService).updateStatement(any(LoanOfferDto.class));
				mockMvc.perform(post("/deal/offer/select")
								.contentType("application/json")
								.content(objectMapper.writeValueAsString(loanOfferDto)))
						.andExpect(status().isNotFound());
			}
		}
	}
	
	@Nested
	@DisplayName("Тестирование метода DealController:calculateLoanParameters()")
	class TestingCalculateLoanParametersMethod {
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
		@Disabled
		class TestingValidation {
			@Nested
			@DisplayName("Тестирование валидации поля gender")
			class TestingValidationOfGender {
				@Test
				void calculateLoanParameters_whenGenderIsNull_thenReturns400() throws Exception {
					finishingRegistrationRequest.setGender(null);
					
					UUID statementId = UUID.randomUUID();
					mockMvc.perform(post("/deal/calculate/{statementId}", statementId.toString())
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
							.andExpect(status().isBadRequest());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля maritalStatus")
			class TestingValidationOfMaritalStatus {
				@Test
				void calculateLoanParameters_whenMaritalStatusIsNull_thenReturns400() throws Exception {
					finishingRegistrationRequest.setMaritalStatus(null);
					
					UUID statementId = UUID.randomUUID();
					mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
							.andExpect(status().isBadRequest());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля dependentAmount")
			class TestingValidationOfDependentAmount {
				@Test
				void calculateLoanParameters_whenDependentAmountIsNull_thenReturns400() throws Exception {
					finishingRegistrationRequest.setDependentAmount(null);
					
					UUID statementId = UUID.randomUUID();
					mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void calculateLoanParameters_whenDependentAmountIsNegative_thenReturns400() throws Exception {
					finishingRegistrationRequest.setDependentAmount(-5);
					
					UUID statementId = UUID.randomUUID();
					mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
							.andExpect(status().isBadRequest());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля passportIssueDate")
			class TestingValidationOfPassportIssueDate {
				@Test
				void calculateLoanParameters_whenPassportIssueDateIsNull_thenReturns400() throws Exception {
					finishingRegistrationRequest.setPassportIssueDate(null);
					
					UUID statementId = UUID.randomUUID();
					mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
							.andExpect(status().isBadRequest());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля passportIssueBranch")
			class TestingValidationOfPassportIssueBranch {
				@Test
				void calculateLoanParameters_whenPassportIssueBranchIsNull_thenReturns400() throws Exception {
					finishingRegistrationRequest.setPassportIssueBranch(null);
					
					UUID statementId = UUID.randomUUID();
					mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
							.andExpect(status().isBadRequest());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля employment")
			class TestingValidationOfEmployment {
				@Test
				void calculateLoanParameters_whenEmploymentIsNull_thenReturns400() throws Exception {
					finishingRegistrationRequest.setEmployment(null);
					
					UUID statementId = UUID.randomUUID();
					mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
							.andExpect(status().isBadRequest());
				}
				
				@Nested
				@DisplayName("Тестирование валидации поля employmentStatus")
				class TestingValidationOfEmploymentStatus {
					@Test
					void calculateLoanParameters_whenEmploymentStatusIsNull_thenReturns400() throws Exception {
						finishingRegistrationRequest.getEmployment().setEmploymentStatus(null);
						
						UUID statementId = UUID.randomUUID();
						mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
										.contentType("application/json")
										.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
								.andExpect(status().isBadRequest());
					}
				}
				
				@Nested
				@DisplayName("Тестирование валидации поля employmentINN")
				class TestingValidationOfEmploymentINN {
					@Test
					void calculateLoanParameters_whenEmploymentInnIsNull_thenReturns400() throws Exception {
						finishingRegistrationRequest.getEmployment().setEmploymentINN(null);
						
						UUID statementId = UUID.randomUUID();
						mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
										.contentType("application/json")
										.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
								.andExpect(status().isBadRequest());
					}
					
					@ParameterizedTest
					@ValueSource(strings = {"wrong", "   ", "12345678901", "1234567890123", "123412341234a", " 123412341234", "123412341234!"})
					void calculateLoanParameters_whenEmploymentInnIsInvalid_thenReturns400(String employmentINN) throws Exception {
						finishingRegistrationRequest.getEmployment().setEmploymentINN(employmentINN);
						
						UUID statementId = UUID.randomUUID();
						mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
										.contentType("application/json")
										.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
								.andExpect(status().isBadRequest());
					}
				}
				
				@Nested
				@DisplayName("Тестирование валидации поля salary")
				class TestingValidationOfSalary {
					@Test
					void calculateLoanParameters_whenSalaryIsNull_thenReturns400() throws Exception {
						finishingRegistrationRequest.getEmployment().setSalary(null);
						
						UUID statementId = UUID.randomUUID();
						mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
										.contentType("application/json")
										.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
								.andExpect(status().isBadRequest());
					}
					
					@Test
					void calculateLoanParameters_whenSalaryIsNegative_thenReturns400() throws Exception {
						finishingRegistrationRequest.getEmployment().setSalary(new BigDecimal(-5));
						
						UUID statementId = UUID.randomUUID();
						mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
										.contentType("application/json")
										.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
								.andExpect(status().isBadRequest());
					}
				}
				
				@Nested
				@DisplayName("Тестирование валидации поля position")
				class TestingValidationOfPosition {
					@Test
					void calculateLoanParameters_whenPositionIsNull_thenReturns400() throws Exception {
						finishingRegistrationRequest.getEmployment().setPosition(null);
						
						UUID statementId = UUID.randomUUID();
						mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
										.contentType("application/json")
										.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
								.andExpect(status().isBadRequest());
					}
				}
				
				@Nested
				@DisplayName("Тестирование валидации поля workExperienceTotal")
				class TestingValidationOfWorkExperienceTotal {
					@Test
					void calculateLoanParameters_whenWorkExperienceTotalIsNull_thenReturns400() throws Exception {
						finishingRegistrationRequest.getEmployment().setWorkExperienceTotal(null);
						
						UUID statementId = UUID.randomUUID();
						mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
										.contentType("application/json")
										.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
								.andExpect(status().isBadRequest());
					}
					
					@Test
					void calculateLoanParameters_whenWorkExperienceTotalIsNegative_thenReturns400() throws Exception {
						finishingRegistrationRequest.getEmployment().setWorkExperienceTotal(-5);
						
						UUID statementId = UUID.randomUUID();
						mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
										.contentType("application/json")
										.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
								.andExpect(status().isBadRequest());
					}
				}
				
				@Nested
				@DisplayName("Тестирование валидации поля workExperienceCurrent")
				class TestingValidationOfWorkExperienceCurrent {
					@Test
					void calculateLoanParameters_whenWorkExperienceCurrentIsNull_thenReturns400() throws Exception {
						finishingRegistrationRequest.getEmployment().setWorkExperienceCurrent(null);
						
						UUID statementId = UUID.randomUUID();
						mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
										.contentType("application/json")
										.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
								.andExpect(status().isBadRequest());
					}
					
					@Test
					void calculateLoanParameters_whenWorkExperienceCurrentIsNegative_thenReturns400() throws Exception {
						finishingRegistrationRequest.getEmployment().setWorkExperienceCurrent(-5);
						
						UUID statementId = UUID.randomUUID();
						mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
										.contentType("application/json")
										.content(objectMapper.writeValueAsString(finishingRegistrationRequest)))
								.andExpect(status().isBadRequest());
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
					assertThat(finishingRegistrationRequestDtoCaptor.getValue().getPassportIssueDate().compareTo(finishingRegistrationRequest.getPassportIssueDate())).isEqualTo(0);
					assertThat(finishingRegistrationRequestDtoCaptor.getValue().getPassportIssueBranch()).isEqualTo(finishingRegistrationRequest.getPassportIssueBranch());
					assertThat(finishingRegistrationRequestDtoCaptor.getValue().getEmployment().getEmploymentStatus()).isEqualTo(finishingRegistrationRequest.getEmployment().getEmploymentStatus());
					assertThat(finishingRegistrationRequestDtoCaptor.getValue().getEmployment().getEmploymentINN()).isEqualTo(finishingRegistrationRequest.getEmployment().getEmploymentINN());
					assertThat(finishingRegistrationRequestDtoCaptor.getValue().getEmployment().getSalary().compareTo(finishingRegistrationRequest.getEmployment().getSalary())).isEqualTo(0);
					assertThat(finishingRegistrationRequestDtoCaptor.getValue().getEmployment().getPosition()).isEqualTo(finishingRegistrationRequest.getEmployment().getPosition());
					assertThat(finishingRegistrationRequestDtoCaptor.getValue().getEmployment().getWorkExperienceTotal()).isEqualTo(finishingRegistrationRequest.getEmployment().getWorkExperienceTotal());
					assertThat(finishingRegistrationRequestDtoCaptor.getValue().getEmployment().getWorkExperienceCurrent()).isEqualTo(finishingRegistrationRequest.getEmployment().getWorkExperienceCurrent());
					assertThat(finishingRegistrationRequestDtoCaptor.getValue().getAccountNumber().equals(finishingRegistrationRequest.getAccountNumber())).isTrue();
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
		}
	}
}
