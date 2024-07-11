package ru.neoflex.neostudy.statement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.exception.InvalidPassportDataException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.common.util.DtoInitializer;
import ru.neoflex.neostudy.statement.service.StatementService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.neoflex.neostudy.statement.custom.ResponseBodyMatcher.responseBody;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = StatementController.class)
public class StatementControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	StatementService statementService;
	
	private LoanStatementRequestDto loanStatementRequestDto;
	private LoanOfferDto loanOfferDto;
	
	@Nested
	@DisplayName("Тестирование метода StatementController:getLoanOffers()")
	class TestingGetLoanOffersMethod {
		@BeforeEach
		void initLoanStatementRequest() {
			loanStatementRequestDto = DtoInitializer.initLoanStatementRequest();
		}
		
		@Nested
		@DisplayName("Тестирование прослушивания HTTP запросов")
		class TestingListeningHttpRequests {
			@Test
			void getLoanOffers_whenValidInput_returns200() throws Exception {
				mockMvc.perform(post("/statement")
								.contentType("application/json")
								.content(objectMapper.writeValueAsString(loanStatementRequestDto)))
						.andExpect(status().isOk());
			}
			
			@Test
			void getLoanOffers_whenNotAllowedMethod_returns405() throws Exception {
				mockMvc.perform(get("/statement")
								.contentType("application/json")
								.content(objectMapper.writeValueAsString(loanStatementRequestDto)))
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
				void getLoanOffers_whenAmountIsNull_thenReturns400() throws Exception {
					loanStatementRequestDto.setAmount(null);
					
					mockMvc.perform(post("/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequestDto)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void getLoanOffers_whenAmountLess30000_thenReturns400() throws Exception {
					loanStatementRequestDto.setAmount(BigDecimal.valueOf(29_999));
					mockMvc.perform(post("/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequestDto)))
							.andExpect(status().isBadRequest());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля term")
			class TestingValidationOfTerm {
				@Test
				void getLoanOffers_whenTermIsNull_thenReturns400() throws Exception {
					loanStatementRequestDto.setTerm(null);
					mockMvc.perform(post("/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequestDto)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void getLoanOffers_whenTermLess6_thenReturns400() throws Exception {
					loanStatementRequestDto.setTerm(5);
					mockMvc.perform(post("/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequestDto)))
							.andExpect(status().isBadRequest());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля firstName")
			class TestingValidationOfFirstName {
				@Test
				void getLoanOffers_whenFirstNameIsNull_thenReturns400() throws Exception {
					loanStatementRequestDto.setFirstName(null);
					mockMvc.perform(post("/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequestDto)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void getLoanOffers_whenFirstNameIsBlank_thenReturns400() throws Exception {
					loanStatementRequestDto.setFirstName("               ");
					mockMvc.perform(post("/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequestDto)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void getLoanOffers_whenFirstNameLessTwo_thenReturns400() throws Exception {
					loanStatementRequestDto.setFirstName("A");
					mockMvc.perform(post("/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequestDto)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void getLoanOffers_whenFirstNameMoreThirty_thenReturns400() throws Exception {
					loanStatementRequestDto.setFirstName("1234567890123456789012345678901");
					mockMvc.perform(post("/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequestDto)))
							.andExpect(status().isBadRequest());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля lastName")
			class TestingValidationOfLastName {
				@Test
				void getLoanOffers_whenLastNameIsNull_thenReturns400() throws Exception {
					loanStatementRequestDto.setLastName(null);
					mockMvc.perform(post("/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequestDto)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void getLoanOffers_whenLastNameIsBlank_thenReturns400() throws Exception {
					loanStatementRequestDto.setLastName("               ");
					mockMvc.perform(post("/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequestDto)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void getLoanOffers_whenLastNameLessTwo_thenReturns400() throws Exception {
					loanStatementRequestDto.setLastName("A");
					mockMvc.perform(post("/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequestDto)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void getLoanOffers_whenLastNameMoreThirty_thenReturns400() throws Exception {
					loanStatementRequestDto.setLastName("1234567890123456789012345678901");
					mockMvc.perform(post("/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequestDto)))
							.andExpect(status().isBadRequest());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля middleName")
			class TestingValidationOfMiddleName {
				@Test
				void getLoanOffers_whenMiddleNameLessTwo_thenReturns400() throws Exception {
					loanStatementRequestDto.setMiddleName("A");
					mockMvc.perform(post("/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequestDto)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void getLoanOffers_whenMiddleNameMoreThirty_thenReturns400() throws Exception {
					loanStatementRequestDto.setMiddleName("1234567890123456789012345678901");
					mockMvc.perform(post("/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequestDto)))
							.andExpect(status().isBadRequest());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля email")
			class TestingValidationOfEmail {
				@Test
				void getLoanOffers_whenEmailIsNull_thenReturns400() throws Exception {
					loanStatementRequestDto.setEmail(null);
					mockMvc.perform(post("/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequestDto)))
							.andExpect(status().isBadRequest());
				}
				
				@ParameterizedTest
				@ValueSource(strings = {"    ", "1234", "fake", "vasya@", "@mail", "vasya@mail!", "vas @mail"})
				void getLoanOffers_whenEmailIsInvalid_thenReturns400(String argument) throws Exception {
					loanStatementRequestDto.setEmail(argument);
					mockMvc.perform(post("/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequestDto)))
							.andExpect(status().isBadRequest());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля birthdate")
			class TestingValidationOfBirthdate {
				@Test
				void getLoanOffers_whenBirthdateIsNull_thenReturns400() throws Exception {
					loanStatementRequestDto.setBirthDate(null);
					mockMvc.perform(post("/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequestDto)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void getLoanOffers_whenAgeIsLessThanEighteen_thenReturns400() throws Exception {
					loanStatementRequestDto.setBirthDate(LocalDate.now().minusYears(18).plusDays(1));
					mockMvc.perform(post("/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequestDto)))
							.andExpect(status().isBadRequest());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля passportSeries")
			class TestingValidationOfPassportSeries {
				@Test
				void getLoanOffers_whenPassportSeriesIsNull_thenReturns400() throws Exception {
					loanStatementRequestDto.setPassportSeries(null);
					mockMvc.perform(post("/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequestDto)))
							.andExpect(status().isBadRequest());
				}
				
				@ParameterizedTest
				@ValueSource(strings = {"    ", "12as", "as12", "fake"})
				void getLoanOffers_whenPassportSeriesIsNotDigits_thenReturns400(String argument) throws Exception {
					loanStatementRequestDto.setPassportSeries(argument);
					mockMvc.perform(post("/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequestDto)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void getLoanOffers_whenPassportSeriesLessFour_thenReturns400() throws Exception {
					loanStatementRequestDto.setPassportSeries("123");
					mockMvc.perform(post("/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequestDto)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void getLoanOffers_whenPassportSeriesMoreFour_thenReturns400() throws Exception {
					loanStatementRequestDto.setPassportSeries("12345");
					mockMvc.perform(post("/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequestDto)))
							.andExpect(status().isBadRequest());
				}
			}
			
			@Nested
			@DisplayName("Тестирование валидации поля passportNumber")
			class TestingValidationOfPassportNumber {
				@Test
				void getLoanOffers_whenPassportNumberIsNull_thenReturns400() throws Exception {
					loanStatementRequestDto.setPassportNumber(null);
					mockMvc.perform(post("/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequestDto)))
							.andExpect(status().isBadRequest());
				}
				
				@ParameterizedTest
				@ValueSource(strings = {"      ", "12as12", "as1234", "number"})
				void getLoanOffers_whenPassportNumberIsNotDigits_thenReturns400(String argument) throws Exception {
					loanStatementRequestDto.setPassportNumber(argument);
					mockMvc.perform(post("/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequestDto)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void getLoanOffers_whenPassportNumberLessFour_thenReturns400() throws Exception {
					loanStatementRequestDto.setPassportNumber("12345");
					mockMvc.perform(post("/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequestDto)))
							.andExpect(status().isBadRequest());
				}
				
				@Test
				void getLoanOffers_whenPassportNumberMoreFour_thenReturns400() throws Exception {
					loanStatementRequestDto.setPassportNumber("1234567");
					mockMvc.perform(post("/statement")
									.contentType("application/json")
									.content(objectMapper.writeValueAsString(loanStatementRequestDto)))
							.andExpect(status().isBadRequest());
				}
			}
		}
		
		@Nested
		@DisplayName("Тестирование десериализации входных данных и вызова методов сервисов")
		class TestingDeserialization {
			@Test
			void getLoanOffers_whenValidInput_thenMapsToBusinessModel() throws Exception {
				mockMvc.perform(post("/statement")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(loanStatementRequestDto)));
				
				ArgumentCaptor<LoanStatementRequestDto> loanStatementRequestDtoCaptor = ArgumentCaptor.forClass(LoanStatementRequestDto.class);
				assertAll(() -> {
					verify(statementService, times(1)).getLoanOffers(loanStatementRequestDtoCaptor.capture());
					assertThat(loanStatementRequestDtoCaptor.getValue().getAmount().compareTo(loanStatementRequestDto.getAmount())).isEqualTo(0);
					assertThat(loanStatementRequestDtoCaptor.getValue().getTerm()).isEqualTo(loanStatementRequestDto.getTerm());
					assertThat(loanStatementRequestDtoCaptor.getValue().getFirstName()).isEqualTo(loanStatementRequestDto.getFirstName());
					assertThat(loanStatementRequestDtoCaptor.getValue().getLastName()).isEqualTo(loanStatementRequestDto.getLastName());
					assertThat(loanStatementRequestDtoCaptor.getValue().getMiddleName()).isEqualTo(loanStatementRequestDto.getMiddleName());
					assertThat(loanStatementRequestDtoCaptor.getValue().getEmail()).isEqualTo(loanStatementRequestDto.getEmail());
					assertThat(loanStatementRequestDtoCaptor.getValue().getBirthDate().equals(loanStatementRequestDto.getBirthDate())).isTrue();
					assertThat(loanStatementRequestDtoCaptor.getValue().getPassportSeries()).isEqualTo(loanStatementRequestDto.getPassportSeries());
					assertThat(loanStatementRequestDtoCaptor.getValue().getPassportNumber()).isEqualTo(loanStatementRequestDto.getPassportNumber());
				});
			}
		}
		
		@Nested
		@DisplayName("Тестирование сериализации посчитанных предложений")
		class TestingSerializationOfLoanOfferDtoList {
			@Test
			void getLoanOffers_whenValidInput_thenMapsToBusinessModel() throws Exception {
				List<LoanOfferDto> offers = DtoInitializer.initOffers();
				when(statementService.getLoanOffers(any(LoanStatementRequestDto.class))).thenReturn(offers);
				
				ResultActions response = mockMvc.perform(post("/statement")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(loanStatementRequestDto)));
				
				response.andExpect(responseBody().containsListAsJson(offers, LoanOfferDto.class));
			}
		}
		
		@Nested
		@DisplayName("Тестирование обработки исключений")
		class TestingExceptions {
			@Test
			void getLoanOffers_whenPassportDataIsInvalid_thenReturn400() throws Exception {
				doThrow(InvalidPassportDataException.class).when(statementService).getLoanOffers(any(LoanStatementRequestDto.class));
				mockMvc.perform(post("/statement")
								.contentType("application/json")
								.content(objectMapper.writeValueAsString(loanStatementRequestDto)))
						.andExpect(status().isBadRequest());
			}
		}
	}
	
	@Nested
	@DisplayName("Тестирование метода StatementController:applyOffer()")
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
				mockMvc.perform(post("/statement/offer")
								.contentType("application/json")
								.content(objectMapper.writeValueAsString(loanOfferDto)))
						.andExpect(status().isOk());
			}
			
			@Test
			void applyOffer_whenNotAllowedMethod_returns405() throws Exception {
				mockMvc.perform(get("/statement/offer")
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
				mockMvc.perform(post("/statement/offer")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(loanOfferDto)));
				
				ArgumentCaptor<LoanOfferDto> loanOfferDtoCaptor = ArgumentCaptor.forClass(LoanOfferDto.class);
				assertAll(() -> {
					verify(statementService, times(1)).applyChosenOffer(loanOfferDtoCaptor.capture());
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
				doThrow(StatementNotFoundException.class).when(statementService).applyChosenOffer(any(LoanOfferDto.class));
				mockMvc.perform(post("/statement/offer")
								.contentType("application/json")
								.content(objectMapper.writeValueAsString(loanOfferDto)))
						.andExpect(status().isNotFound());
			}
		}
	}
}
