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

/**
 * Сервис уровня business services, реализующий общую работу с широким диапазоном данных по кредиту.
 */
@Service
@RequiredArgsConstructor
public class DataService {
	private final ClientEntityService clientEntityService;
	private final StatementEntityService statementEntityService;
	
	/**
	 * Возвращает объект {@code Statement}, сознанный на основании данных из пользовательского запроса кредита
	 * {@code LoanStatementRequestDto}.
	 * @param loanStatementRequest данные запроса кредита от пользователя.
	 * @return созданный объект заявки на кредит {@code Statement}.
	 * @throws InvalidPassportDataException если паспортные данные из LoanStatementRequestDto не совпадают с паспортными
	 * данными клиента из базы данных.
	 */
	public Statement prepareData(LoanStatementRequestDto loanStatementRequest) throws InvalidPassportDataException {
		Optional<Client> optionalClient = clientEntityService.findClientByPassport(loanStatementRequest);
		Client client = clientEntityService.checkAndSaveClient(loanStatementRequest, optionalClient);
		
		Statement statement = new Statement();
		statement.setStatementId(UUID.randomUUID());
		statement.setClient(client);
		return statement;
	}
	
	/**
	 * Возвращает объект {@code Statement} из базы данных по идентификатору statementId, или, если объект не найден,
	 * выкидывает исключение {@code StatementNotFoundException}.
	 * @param statementId идентификатор заявки пользователя {@code Statement}.
	 * @return объект {@code Statement} из базы данных.
	 * @throws StatementNotFoundException выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 * не найден в базе данных.
	 */
	public Statement findStatement(UUID statementId) throws StatementNotFoundException {
		return statementEntityService.findStatement(statementId).orElseThrow(() -> new StatementNotFoundException(String.format("Statement with id = %s not found", statementId)));
	}
	
	/**
	 * Устанавливает для переданного объекта {@code Statement} переданное в аргументах метода значение enum
	 * {@code ApplicationStatus}, сохраняет или обновляет {@code Statement} в базе данных.
	 * @param statement объект-entity, содержащий все данные по кредиту.
	 * @param status значение enum типа {@code ApplicationStatus}, которое необходимо установить объекту
	 * {@code Statement}.
	 * @param changeType значение enum типа {@code ChangeType}, указывающее режим изменения статуса.
	 */
	public void updateStatement(Statement statement, ApplicationStatus status, ChangeType changeType) {
		statementEntityService.setStatus(statement, status, changeType);
		statementEntityService.save(statement);
	}
	
	/**
	 * Применяет выбранное пользователем кредитное предложение. По указанному в предложении идентификатору заявки
	 * statementId получает заявку {@code Statement} из базы данных, устанавливает в неё выбранное кредитное предложение
	 * и обновляет заявку в базе данных со статусом APPROVED.
	 * @param loanOffer выбранное пользователем кредитное предложение.
	 * @throws StatementNotFoundException выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 * не найден в базе данных.
	 */
	public void applyOfferAndSave(LoanOfferDto loanOffer) throws StatementNotFoundException {
		Statement statement = findStatement(loanOffer.getStatementId());
		statement.setAppliedOffer(loanOffer);
		updateStatement(statement, ApplicationStatus.APPROVED, ChangeType.AUTOMATIC);
	}
	
	/**
	 * Получает из базы данных заявку {@code Statement} по указанному идентификатору заявки statementId, устанавливает
	 * для неё статус CLIENT_DENIED, обновляет заявку в базе данных.
	 * @param statementId идентификатор заявки пользователя {@code Statement}.
	 * @return объект {@code Statement} из базы данных.
	 * @throws StatementNotFoundException выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 * не найден в базе данных.
	 */
	public Statement denyOffer(UUID statementId) throws StatementNotFoundException {
		Statement statement = findStatement(statementId);
		updateStatement(statement, ApplicationStatus.CLIENT_DENIED, ChangeType.AUTOMATIC);
		return statement;
	}
	
	/**
	 * Возвращает список объектов {@code Statement} из базы данных постранично, по {@code pageSize} элементов на
	 * страницу.
	 * @param page номер страницы.
	 * @return {@code List<Statement>} с прочитанными из базы данных объектами {@code Statement}.
	 */
	public List<Statement> findAllStatements(Integer page) {
		return statementEntityService.findAllStatements(page);
	}
	
	/**
	 * Сохнаняет или обновляет в базе данных объект {@code Statement}.
	 * @param statement сохраняемый объект-entity.
	 */
	public void saveStatement(Statement statement) {
		statementEntityService.save(statement);
	}
}
