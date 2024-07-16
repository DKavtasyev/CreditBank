package ru.neoflex.neostudy.deal.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.constants.ChangeType;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.util.DtoInitializer;
import ru.neoflex.neostudy.deal.entity.Client;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.common.exception.InvalidPassportDataException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.deal.mapper.PreScoreClientPersonalIdentificationInformationMapper;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataServiceTest {
	@Mock
	private ClientEntityService clientEntityServiceMock;
	
	@Mock
	private StatementEntityService statementEntityServiceMock;
	
	@InjectMocks
	private DataService dataService;
	
	private final PreScoreClientPersonalIdentificationInformationMapper mapper = new PreScoreClientPersonalIdentificationInformationMapper();
	private LoanStatementRequestDto loanStatementRequestDto;
	private Client client;
	private final Statement expectedStatement = new Statement();
	private final LoanOfferDto loanOffer = new LoanOfferDto();
	
	@BeforeEach
	void init() {
		loanStatementRequestDto = DtoInitializer.initLoanStatementRequest();
		client = mapper.dtoToEntity(loanStatementRequestDto);
		expectedStatement.setClient(client);
		expectedStatement.setStatementId(UUID.randomUUID());
	}
	
	@Nested
	@DisplayName("Тестирование метода DataService:prepareData()")
	class TestingWriteDataMethod {
		@Test
		void prepareData() throws InvalidPassportDataException {
			Optional<Client> optionalClient = Optional.of(client);
			when(clientEntityServiceMock.findClientByPassport(loanStatementRequestDto)).thenReturn(optionalClient);
			when(clientEntityServiceMock.checkAndSaveClient(loanStatementRequestDto, optionalClient)).thenReturn(client);
//			when(statementEntityServiceMock.save(any(Statement.class))).thenReturn(expectedStatement);
			Statement actualStatement = dataService.prepareData(loanStatementRequestDto);
			Assertions.assertAll(() -> {
				verify(clientEntityServiceMock, times(1)).findClientByPassport(loanStatementRequestDto);
				verify(clientEntityServiceMock, times(1)).checkAndSaveClient(loanStatementRequestDto, optionalClient);
				assertThat(actualStatement).isEqualTo(expectedStatement);
			});
		}
	}
	
	@Nested
	@DisplayName("Тестирование метода DataService:findStatement()")
	class TestingFindStatementMethod {
		@Test
		void findStatement_whenStatementExists_thenReturnStatement() throws StatementNotFoundException {
			UUID statementId = UUID.randomUUID();
			expectedStatement.setStatementId(statementId);
			Optional<Statement> optionalStatement = Optional.of(expectedStatement);
			when(statementEntityServiceMock.findStatement(statementId)).thenReturn(optionalStatement);
			Statement actualStatement = dataService.findStatement(statementId);
			assertThat(actualStatement).isSameAs(expectedStatement);
		}
		
		@Test
		void findStatement_whenStatementNotExists_thenThrowStatementNotFoundException() {
			UUID statementId = UUID.randomUUID();
			Optional<Statement> optionalStatement = Optional.empty();
			when(statementEntityServiceMock.findStatement(statementId)).thenReturn(optionalStatement);
			assertThrows(StatementNotFoundException.class, () -> dataService.findStatement(statementId));
		}
	}
	
	@Nested
	@DisplayName("Тестирование метода DataService:updateStatement()")
	class TestingUpdateStatementMethod {
		@Test
		void updateStatement() throws StatementNotFoundException {
			UUID statementId = expectedStatement.getStatementId();
			loanOffer.setStatementId(statementId);
			Optional<Statement> optionalStatement = Optional.of(expectedStatement);
			when(statementEntityServiceMock.findStatement(statementId)).thenReturn(optionalStatement);
			dataService.applyOfferAndSave(loanOffer);
			assertAll(() -> {
				assertThat(expectedStatement.getAppliedOffer()).isSameAs(loanOffer);
				verify(statementEntityServiceMock, times(1)).setStatus(expectedStatement, ApplicationStatus.APPROVED, ChangeType.AUTOMATIC);
				verify(statementEntityServiceMock, times(1)).save(expectedStatement);
			});
			
		}
	}
}