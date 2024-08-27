package ru.neoflex.neostudy.deal.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.constants.ChangeType;
import ru.neoflex.neostudy.common.constants.CreditStatus;
import ru.neoflex.neostudy.common.dto.CreditDto;
import ru.neoflex.neostudy.common.dto.FinishingRegistrationRequestDto;
import ru.neoflex.neostudy.common.dto.ScoringDataDto;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.InvalidPreApproveException;
import ru.neoflex.neostudy.common.exception.LoanRefusalException;
import ru.neoflex.neostudy.common.util.DtoInitializer;
import ru.neoflex.neostudy.deal.entity.Credit;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.mapper.CreditMapper;
import ru.neoflex.neostudy.deal.mapper.ScoringDataMapper;
import ru.neoflex.neostudy.deal.requester.CalculatorRequester;
import ru.neoflex.neostudy.deal.service.kafka.KafkaService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScoringServiceTest {
	@Mock
	ScoringDataMapper scoringDataMapper;
	
	@Mock
	CalculatorRequester calculatorRequester;
	
	@Mock
	CreditMapper creditMapper;
	
	@Mock
	DataService dataService;
	
	@Mock
	KafkaService kafkaService;
	
	@InjectMocks
	ScoringService scoringService;
	
	FinishingRegistrationRequestDto finishingRegistrationRequestDto;
	Statement statement;
	ScoringDataDto scoringDataDto;
	CreditDto creditDto;
	Credit credit;
	
	@BeforeEach
	void init() {
		finishingRegistrationRequestDto = DtoInitializer.initFinishingRegistrationRequest();
		statement = new Statement();
		statement.setStatementId(UUID.randomUUID());
		scoringDataDto = DtoInitializer.initScoringData();
		creditDto = DtoInitializer.initCreditDto();
		credit = new Credit();
	}
	
	@Nested
	@DisplayName("Тестирование метода ScoringServiceTest:scoreAndSaveCredit()")
	class TestingSetStatusMethod {
		@Test
		void scoreAndSaveCredit() throws LoanRefusalException, InternalMicroserviceException, InvalidPreApproveException {
			when(scoringDataMapper.formScoringDataDto(finishingRegistrationRequestDto, statement)).thenReturn(scoringDataDto);
			when(calculatorRequester.requestCalculatedCredit(scoringDataDto)).thenReturn(creditDto);
			when(creditMapper.dtoToEntity(creditDto)).thenReturn(credit);
			scoringService.scoreAndSaveCredit(finishingRegistrationRequestDto, statement);
			assertAll(() -> {
				verify(scoringDataMapper, times(1)).formScoringDataDto(finishingRegistrationRequestDto, statement);
				verify(calculatorRequester, times(1)).requestCalculatedCredit(scoringDataDto);
				verify(creditMapper, times(1)).dtoToEntity(creditDto);
				assertThat(credit.getCreditStatus()).isEqualTo(CreditStatus.CALCULATED);
				assertThat(statement.getCredit()).isSameAs(credit);
				verify(dataService, times(1)).updateStatement(statement, ApplicationStatus.CC_APPROVED, ChangeType.AUTOMATIC);
			});
		}
		
		@Test
		void scoreAndSaveCredit_whenConditionsAreNotMet_thenThrowLoanRefusalException() throws LoanRefusalException, InternalMicroserviceException {
			when(scoringDataMapper.formScoringDataDto(finishingRegistrationRequestDto, statement)).thenReturn(scoringDataDto);
			when(calculatorRequester.requestCalculatedCredit(scoringDataDto)).thenThrow(LoanRefusalException.class);
			assertAll(() -> {
				assertThrows(LoanRefusalException.class, () -> scoringService.scoreAndSaveCredit(finishingRegistrationRequestDto, statement));
				verify(dataService, times(1)).updateStatement(statement, ApplicationStatus.CC_DENIED, ChangeType.AUTOMATIC);
				verify(kafkaService, times(1)).sendDenial(statement, "Вам отказано в получении кредита");
			});
		}
		
		@Test
		void scoreAndSaveCredit_whenPreApproveHasNotExecuted_thenThrowInvalidPreApproveException() {
			when(scoringDataMapper.formScoringDataDto(finishingRegistrationRequestDto, statement)).thenThrow(NullPointerException.class);
			assertThrows(InvalidPreApproveException.class, () -> scoringService.scoreAndSaveCredit(finishingRegistrationRequestDto, statement));
		}
	}
}