package ru.neoflex.neostudy.deal.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.util.DtoInitializer;
import ru.neoflex.neostudy.deal.entity.Client;
import ru.neoflex.neostudy.deal.exception.InvalidPassportDataException;
import ru.neoflex.neostudy.deal.repository.ClientRepository;
import ru.neoflex.neostudy.deal.util.EntityInitializer;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientEntityServiceTest
{
	@Mock
	ClientRepository clientRepository;
	
	@InjectMocks
	ClientEntityService clientEntityService;
	
	private LoanStatementRequestDto loanStatementRequestDto;
	private Client expectedClient;
	
	
	@BeforeEach
	void init()
	{
		loanStatementRequestDto = DtoInitializer.initLoanStatementRequest();
		expectedClient = EntityInitializer.initClient();
	}
	
	@Nested
	@DisplayName("Тестирование метода ClientEntityService:findClientByPassport()")
	class TestingFindClientByPassportMethod
	{
		@Test
		void findClientByPassport_whenGivenLoanStatementRequestDto_thenReturnOptionalClient()
		{
			when(clientRepository.findClientByPassportSeriesAndPassportNumber(loanStatementRequestDto.getPassportSeries(), loanStatementRequestDto.getPassportNumber())).thenReturn(Optional.of(expectedClient));
			Client actualClient = clientEntityService.findClientByPassport(loanStatementRequestDto).orElse(null);
			assertAll(() -> {
				assertThat(actualClient).isNotNull();
				assertThat(actualClient.getPassport()).isNotNull();
				assertThat(actualClient.getPassport().getSeries()).isEqualTo(expectedClient.getPassport().getSeries());
				assertThat(actualClient.getPassport().getNumber()).isEqualTo(expectedClient.getPassport().getNumber());
			});
		}
	}
	
	@Nested
	@DisplayName("Тестирование метода ClientEntityService:checkAndSaveClient()")
	class TestingCheckAndSaveClientMethod
	{
		@Test
		void checkAndSaveClient_whenClientIsPresentAndValid_thenReturnClient() throws InvalidPassportDataException
		{
			Client actualClient = clientEntityService.checkAndSaveClient(loanStatementRequestDto, Optional.of(expectedClient));
			assertThat(actualClient).isEqualTo(expectedClient);
		}
		
		@Test
		void checkAndSaveClient_whenClientIsPresentAndFirstNameInvalid_thenThrowInvalidPassportDataException()
		{
			expectedClient.setFirstName("Vasya");
			assertThrows(InvalidPassportDataException.class, () -> clientEntityService.checkAndSaveClient(loanStatementRequestDto, Optional.of(expectedClient)), "Personal identification information is invalid");
		}
		
		@Test
		void checkAndSaveClient_whenClientIsPresentAndLastNameInvalid_thenThrowInvalidPassportDataException()
		{
			expectedClient.setLastName("Petrov");
			assertThrows(InvalidPassportDataException.class, () -> clientEntityService.checkAndSaveClient(loanStatementRequestDto, Optional.of(expectedClient)), "Personal identification information is invalid");
		}
		
		@Test
		void checkAndSaveClient_whenClientIsPresentAndMiddleNameInvalid_thenThrowInvalidPassportDataException()
		{
			expectedClient.setMiddleName(expectedClient.getMiddleName() + "a");
			assertThrows(InvalidPassportDataException.class, () -> clientEntityService.checkAndSaveClient(loanStatementRequestDto, Optional.of(expectedClient)), "Personal identification information is invalid");
		}
		
		@Test
		void checkAndSaveClient_whenClientIsPresentAndBirthdateInvalid_thenThrowInvalidPassportDataException()
		{
			expectedClient.setBirthdate(expectedClient.getBirthdate().minusDays(1));
			assertThrows(InvalidPassportDataException.class, () -> clientEntityService.checkAndSaveClient(loanStatementRequestDto, Optional.of(expectedClient)), "Personal identification information is invalid");
		}
	}
	
	
}