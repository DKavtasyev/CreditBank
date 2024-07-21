package ru.neoflex.neostudy.deal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.constants.ChangeType;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.exception.InvalidPassportDataException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.deal.entity.Client;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.service.entity.ClientEntityService;
import ru.neoflex.neostudy.deal.service.entity.StatementEntityService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DataService {
	private final ClientEntityService clientEntityService;
	private final StatementEntityService statementEntityService;
	
	public Statement prepareData(LoanStatementRequestDto loanStatementRequest) throws InvalidPassportDataException {
		Optional<Client> optionalClient = clientEntityService.findClientByPassport(loanStatementRequest);
		Client client = clientEntityService.checkAndSaveClient(loanStatementRequest, optionalClient);
		
		Statement statement = new Statement();
		statement.setStatementId(UUID.randomUUID());
		statement.setClient(client);
		return statement;
	}
	
	public Statement findStatement(UUID statementId) throws StatementNotFoundException {
		return statementEntityService.findStatement(statementId).orElseThrow(() -> new StatementNotFoundException(String.format("Statement with id = %s not found", statementId)));
	}
	
	public void updateStatement(Statement statement, ApplicationStatus status, ChangeType changeType) {
		statementEntityService.setStatus(statement, status, changeType);
		statementEntityService.save(statement);
	}
	
	public void applyOfferAndSave(LoanOfferDto loanOffer) throws StatementNotFoundException {
		Statement statement = findStatement(loanOffer.getStatementId());
		statement.setAppliedOffer(loanOffer);
		updateStatement(statement, ApplicationStatus.APPROVED, ChangeType.AUTOMATIC);
	}
	
	public Statement denyOffer(UUID statementId) throws StatementNotFoundException {
		Statement statement = findStatement(statementId);
		updateStatement(statement, ApplicationStatus.CLIENT_DENIED, ChangeType.AUTOMATIC);
		return statement;
	}
	
	public List<Statement> findAllStatements(Integer page) {
		return statementEntityService.findAllStatements(page);
	}
}
