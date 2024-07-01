package ru.neoflex.neostudy.deal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.constants.ChangeType;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.entity.jsonb.StatusHistory;
import ru.neoflex.neostudy.deal.repository.StatementRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StatementEntityService {
	private final StatementRepository statementRepository;
	
	public Statement save(Statement statement) {
		return statementRepository.save(statement);
	}
	
	public Optional<Statement> findStatement(UUID statementId) {
		return statementRepository.findById(statementId);
	}
	
	public void setStatus(Statement statement, ApplicationStatus status) {
		statement.setStatus(status);
		statement.getStatusHistory().add(new StatusHistory()
				.setStatus(statement.getStatus())
				.setTime(LocalDateTime.now())
				.setChangeType(ChangeType.AUTOMATIC));
	}
}
