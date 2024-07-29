package ru.neoflex.neostudy.deal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.neoflex.neostudy.deal.entity.Statement;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StatementRepository extends JpaRepository<Statement, UUID> {
	/**
	 * Запрашивает в базе данных и возвращает email пользователя в формате {@code Optional<String>} по переданному
	 * идентификатору заявки, которая была создана пользователем с искомым email.
	 * @param statementId идентификатор заявки пользователя.
	 * @return {@code Optional<String>}
	 */
	@Query(value = "select s.client.email from Statement s where s.statementId = :statementId")
	Optional<String> getClientEmailByStatementId(@Param("statementId") UUID statementId);
}
