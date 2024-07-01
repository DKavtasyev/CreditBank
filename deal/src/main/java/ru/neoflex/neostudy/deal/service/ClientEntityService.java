package ru.neoflex.neostudy.deal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.deal.entity.Client;
import ru.neoflex.neostudy.deal.exception.InvalidPassportDataException;
import ru.neoflex.neostudy.deal.mapper.PreScoreClientPersonalIdentificationInformationMapper;
import ru.neoflex.neostudy.deal.repository.ClientRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientEntityService {
	private final ClientRepository clientRepository;
	private final PreScoreClientPersonalIdentificationInformationMapper preScoreClientPersonalIdentificationInformationMapper;
	
	public Optional<Client> findClientByPassport(LoanStatementRequestDto loanStatementRequest) {
		return clientRepository.findClientByPassportSeriesAndPassportNumber(loanStatementRequest.getPassportSeries(), loanStatementRequest.getPassportNumber());
	}
	
	public Client checkAndSaveClient(LoanStatementRequestDto loanStatementRequest, Optional<Client> optionalClient) throws InvalidPassportDataException {
		Client client;
		if (optionalClient.isPresent()) {
			client = optionalClient.get();
			checkClientPersonalIdentificationInformation(loanStatementRequest, client);
		}
		else {
			client = preScoreClientPersonalIdentificationInformationMapper.dtoToEntity(loanStatementRequest);
			clientRepository.save(client);
		}
		return client;
	}
	
	private void checkClientPersonalIdentificationInformation(LoanStatementRequestDto loanStatementRequest, Client client) throws InvalidPassportDataException {
		if (!loanStatementRequest.getFirstName().equals(client.getFirstName())
				|| !loanStatementRequest.getLastName().equals(client.getLastName())
				|| !loanStatementRequest.getMiddleName().equals(client.getMiddleName())
				|| !loanStatementRequest.getBirthDate().isEqual(client.getBirthdate())) {
			throw new InvalidPassportDataException("Personal identification information is invalid");
		}
	}
}
