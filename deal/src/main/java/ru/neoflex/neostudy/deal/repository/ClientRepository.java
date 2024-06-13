package ru.neoflex.neostudy.deal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.neoflex.neostudy.deal.entity.Client;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID>
{
	@Query(value = "SELECT * FROM client c WHERE c.passport ->> 'series' = :passport_series and c.passport ->> 'number' = :passport_number", nativeQuery = true)
	Optional<Client> findClientByPassportSeriesAndPassportNumber(@Param("passport_series") String passportSeries, @Param("passport_number") String passportNumber);
}
