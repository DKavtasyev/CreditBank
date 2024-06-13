package ru.neostudy.neoflex.deal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neostudy.neoflex.deal.constants.ApplicationStatus;
import ru.neostudy.neoflex.deal.dto.LoanOfferDto;
import ru.neostudy.neoflex.deal.dto.LoanStatementRequestDto;
import ru.neostudy.neoflex.deal.entity.Client;
import ru.neostudy.neoflex.deal.entity.Statement;
import ru.neostudy.neoflex.deal.exception.InvalidPassportDataException;
import ru.neostudy.neoflex.deal.exception.StatementNotFoundException;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DataService
{
	private final ClientEntityService clientEntityService;
	private final StatementEntityService statementEntityService;
	
	public Statement writeData(LoanStatementRequestDto loanStatementRequest) throws InvalidPassportDataException
	{
		Optional<Client> optionalClient = clientEntityService.findClientByPassport(loanStatementRequest);
		Client client = clientEntityService.checkAndSaveClient(loanStatementRequest, optionalClient);
		
		Statement statement = new Statement();
		statement.setStatementId(UUID.randomUUID());
		statement.setClient(client);
		return statementEntityService.save(statement);
	}
	
	
	public Statement findStatement(UUID statementId) throws StatementNotFoundException
	{
		return statementEntityService.findStatement(statementId).orElseThrow(() -> new StatementNotFoundException("Statement not found"));
	}
	
	public void updateStatement(LoanOfferDto loanOffer) throws StatementNotFoundException
	{
		Statement statement = findStatement(loanOffer.getStatementId());
		statementEntityService.setStatus(statement, ApplicationStatus.APPROVED);
		statement.setAppliedOffer(loanOffer);
		statementEntityService.save(statement);
	}
}
