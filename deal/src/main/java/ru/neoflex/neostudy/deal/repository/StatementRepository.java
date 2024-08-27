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
	@Query(value = "select s.client.email from Statement s where s.statementId = :statementId")
	Optional<String> getClientEmailByStatementId(@Param("statementId") UUID statementId);
}
