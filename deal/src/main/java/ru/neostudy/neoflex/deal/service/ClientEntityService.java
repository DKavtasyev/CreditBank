package ru.neostudy.neoflex.deal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neostudy.neoflex.deal.dto.LoanStatementRequestDto;
import ru.neostudy.neoflex.deal.entity.Client;
import ru.neostudy.neoflex.deal.exception.InvalidPassportDataException;
import ru.neostudy.neoflex.deal.mapper.PreScoreClientPersonalIdentificationInformationMapper;
import ru.neostudy.neoflex.deal.repository.ClientRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientEntityService
{
	private final ClientRepository clientRepository;
	private final PreScoreClientPersonalIdentificationInformationMapper preScoreClientPersonalIdentificationInformationMapper;
	
	public Optional<Client> findClientByPassport(LoanStatementRequestDto loanStatementRequest)
	{
		return clientRepository.findClientByPassportSeriesAndPassportNumber(loanStatementRequest.getPassportSeries(), loanStatementRequest.getPassportNumber());
	}
	
	public Client checkAndSaveClient(LoanStatementRequestDto loanStatementRequest, Optional<Client> optionalClient) throws InvalidPassportDataException
	{
		Client client;
		if (optionalClient.isPresent())
		{
			client = optionalClient.get();
			checkClientPersonalIdentificationInformation(loanStatementRequest, client);
		}
		else
		{
			client = preScoreClientPersonalIdentificationInformationMapper.dtoToEntity(loanStatementRequest);
			clientRepository.save(client);
		}
		return client;
	}
	
	private void checkClientPersonalIdentificationInformation(LoanStatementRequestDto loanStatementRequest, Client client) throws InvalidPassportDataException
	{
		if (!loanStatementRequest.getFirstName().equals(client.getFirstName())
				|| !loanStatementRequest.getLastName().equals(client.getLastName())
				|| !loanStatementRequest.getMiddleName().equals(client.getMiddleName())
				|| !loanStatementRequest.getBirthDate().isEqual(client.getBirthdate()))
			throw new InvalidPassportDataException("Personal identification information is invalid");
	}
}
