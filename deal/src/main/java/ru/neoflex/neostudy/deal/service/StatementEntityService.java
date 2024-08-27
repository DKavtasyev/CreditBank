package ru.neoflex.neostudy.deal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.constants.ChangeType;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.entity.jsonb.StatementStatusHistory;
import ru.neoflex.neostudy.deal.repository.StatementRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StatementEntityService {
	private final StatementRepository statementRepository;
	
	public void save(Statement statement) {
		statementRepository.save(statement);
	}
	
	public Optional<Statement> findStatement(UUID statementId) {
		return statementRepository.findById(statementId);
	}
	
	public void setStatus(Statement statement, ApplicationStatus status, ChangeType changeType) {
		statement.setStatus(status);
		statement.getStatementStatusHistory().add(new StatementStatusHistory()
				.setStatus(statement.getStatus())
				.setTime(LocalDateTime.now())
				.setChangeType(changeType));
	}
}
