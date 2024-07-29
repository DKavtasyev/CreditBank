package ru.neoflex.neostudy.deal.service.entity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.deal.entity.Client;
import ru.neoflex.neostudy.common.exception.InvalidPassportDataException;
import ru.neoflex.neostudy.deal.mapper.PreScoreClientPersonalIdentificationInformationMapper;
import ru.neoflex.neostudy.deal.repository.ClientRepository;

import java.util.Optional;

/**
 * Сервис уровня business objects для работы с entity типа {@code Client}.
 */
@Service
@RequiredArgsConstructor
public class ClientEntityService {
	private final ClientRepository clientRepository;
	private final PreScoreClientPersonalIdentificationInformationMapper preScoreClientPersonalIdentificationInformationMapper;
	
	/**
	 * Возвращает {@code Optional<Client>} по данным пользовательского запроса кредита.
	 * @param loanStatementRequest данные запроса кредита от пользователя.
	 * @return {@code Optional<Client>}
	 */
	public Optional<Client> findClientByPassport(LoanStatementRequestDto loanStatementRequest) {
		return clientRepository.findClientByPassportSeriesAndPassportNumber(loanStatementRequest.getPassportSeries(), loanStatementRequest.getPassportNumber());
	}
	
	/**
	 * Возвращает entity {@code Client}. Если данный клиент ранее был сохранён в базе данных и паспортные данные,
	 * полученные от пользователя в объекте {@code LoanStatementRequestDto}, соотвествуют паспортным данным объекта
	 * {@code Client} из базы данных, то вернётся полученный в {@code Optional<Client>} объект Client. Если проверка не
	 * будет пройдена, то выкинется исключение {@code InvalidPassportDataException}, которое означает несовпадение
	 * паспортных данных для этого клиента. Если {@code Optional<Client>} окажется пустым - то это значит, что клиент
	 * с данными из {@code LoanStatementRequestDto} в базе данных не найден, тогда по данным из
	 * {@code LoanStatementRequestDto} будет создан возвращён новый объект Client.
	 * @param loanStatementRequest данные запроса кредита от пользователя.
	 * @param optionalClient {@code Optional<Client>}, содержащий или не содержащий в себе объект Client.
	 * @return Client.
	 * @throws InvalidPassportDataException если паспортные данные из {@code LoanStatementRequestDto} не совпадают с
	 * паспортными данными клиента из базы данных.
	 */
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
	
	/**
	 * Проверяет паспортные данные из объекта {@code LoanStatementRequestDto}, содержащего данные, полученные от
	 * пользователя, и паспортные данные из объекта {@code Client}, полученного из базы данных.
	 * @param loanStatementRequest данные запроса кредита от пользователя.
	 * @param client объект {@code Client}, полученный из базы данных.
	 * @throws InvalidPassportDataException выкидывается, если хотя-бы одно из Имени, Фамилии, Отчества или даты
	 * рождения не совпали.
	 */
	private void checkClientPersonalIdentificationInformation(LoanStatementRequestDto loanStatementRequest, Client client) throws InvalidPassportDataException {
		if (!loanStatementRequest.getFirstName().equals(client.getFirstName())
				|| !loanStatementRequest.getLastName().equals(client.getLastName())
				|| !loanStatementRequest.getMiddleName().equals(client.getMiddleName())
				|| !loanStatementRequest.getBirthDate().isEqual(client.getBirthdate())) {
			throw new InvalidPassportDataException("Personal identification information is invalid");
		}
	}
}
