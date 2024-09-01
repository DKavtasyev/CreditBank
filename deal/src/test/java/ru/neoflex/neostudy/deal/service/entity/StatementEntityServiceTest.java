package ru.neoflex.neostudy.deal.service.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.constants.ChangeType;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.repository.StatementRepository;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StatementEntityServiceTest {
	@Mock
	StatementRepository statementRepositoryMock;
	
	@InjectMocks
	StatementEntityService statementEntityService;
	
	
	@Nested
	@DisplayName("Тестирование метода StatementEntityService:save()")
	class TestingSaveMethod {
		@Test
		void save() {
			Statement statement = new Statement();
			statementEntityService.save(statement);
			verify(statementRepositoryMock, times(1)).save(statement);
		}
	}
	
	@Nested
	@DisplayName("Тестирование метода StatementEntityService:findStatement()")
	class TestingFindStatementMethod {
		@Test
		void findStatement() {
			UUID statementId = UUID.randomUUID();
			statementEntityService.findStatement(statementId);
			verify(statementRepositoryMock, times(1)).findById(statementId);
		}
	}
	
	@Nested
	@DisplayName("Тестирование метода StatementEntityService:setStatus()")
	class TestingSetStatusMethod {
		@ParameterizedTest
		@MethodSource("argsProvidedFactory")
		void setStatus(ApplicationStatus status) {
			Statement statement = new Statement();
			statementEntityService.setStatus(statement, status, ChangeType.AUTOMATIC);
			assertAll(() -> {
				assertThat(statement.getStatus()).isEqualTo(status);
				assertThat(statement.getStatementStatusHistory()).isNotEmpty();
				assertThat(statement.getStatementStatusHistory().peekLast()).isNotNull();
				assertThat(statement.getStatementStatusHistory().peekLast().getStatus()).isEqualTo(status);
				assertThat(statement.getStatementStatusHistory().peekLast().getTime()).isNotNull();
				assertThat(statement.getStatementStatusHistory().peekLast().getChangeType()).isEqualTo(ChangeType.AUTOMATIC);
			});
		}
		
		static Stream<ApplicationStatus> argsProvidedFactory() {
			return Arrays.stream(ApplicationStatus.values());
		}
	}
	
	
}