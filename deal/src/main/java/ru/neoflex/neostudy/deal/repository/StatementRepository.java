package ru.neoflex.neostudy.deal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.neoflex.neostudy.deal.entity.Statement;

import java.util.UUID;

/**
 * Интерфейс для работы с сущностью Statement с использованием Java Persistence API.
 */
@Repository
public interface StatementRepository extends JpaRepository<Statement, UUID> {
}
