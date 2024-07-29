package ru.neoflex.neostudy.deal.service.entity;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.constants.ChangeType;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.entity.jsonb.StatementStatusHistory;
import ru.neoflex.neostudy.deal.repository.StatementRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;

/**
 * Сервис уровня business objects для работы с entity типа {@code Statement}.
 */
@Service
@RequiredArgsConstructor
public class StatementEntityService {
	private final StatementRepository statementRepository;
	
	/**
	 * Число записей на одной странице при запросе всех объектов типа {@code Statement}.
	 */
	@Value("${app.rest.page-size}")
	private int pageSize;
	
	/**
	 * Сохраняет в базу данных новую или обновляет существующую заявку типа {@code Statement}.
	 * @param statement объект-entity, представляет собой заявку на кредит, содержит все данные по кредиту.
	 */
	public void save(Statement statement) {
		statementRepository.save(statement);
	}
	
	/**
	 * Возвращает по указанному идентификатору {@code statementId} объект типа {@code Optional<Statement>} из базы
	 * данных.
	 * @param statementId идентификатор заявки пользователя {@code Statement}.
	 * @return {@code Optional<Statement>}
	 */
	public Optional<Statement> findStatement(UUID statementId) {
		return statementRepository.findById(statementId);
	}
	
	/**
	 * Устанавливает переданному объекту {@code Statement} указанный статус.
	 * @param statement объект-entity, представляет собой заявку на кредит.
	 * @param status значение enum типа {@code ApplicationStatus}, которое необходимо установить объекту
	 * {@code Statement}.
	 * @param changeType значение enum типа {@code ChangeType}, указывающее режим изменения статуса.
	 */
	public void setStatus(Statement statement, ApplicationStatus status, ChangeType changeType) {
		statement.setStatus(status);
		statement.getStatementStatusHistory().add(new StatementStatusHistory()
				.setStatus(statement.getStatus())
				.setTime(LocalDateTime.now())
				.setChangeType(changeType));
	}
	
	/**
	 * Возвращает список объектов {@code Statement} из базы данных постранично, по {@code pageSize} элементов на
	 * страницу.
	 * @param page номер страницы, нумерация с единицы. Значения null и ноль будут преобразованы к единице.
	 * @return {@code List<Statement>} с прочитанными из базы данных объектами {@code Statement}.
	 */
	public List<Statement> findAllStatements(Integer page) {
		page = isNull(page) || page == 0 ? 0 : page - 1;
		return statementRepository.findAll(PageRequest.of(page, pageSize, Sort.by("creationDate"))).getContent();
	}
}
