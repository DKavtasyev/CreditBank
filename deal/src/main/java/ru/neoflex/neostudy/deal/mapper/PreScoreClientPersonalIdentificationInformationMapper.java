package ru.neoflex.neostudy.deal.mapper;

import org.springframework.stereotype.Component;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.deal.entity.Client;
import ru.neoflex.neostudy.deal.entity.jsonb.Passport;

import java.util.UUID;

/**
 * Класс, использующийся для маппинга объектов DTO и entity пользовательских данных для создания заявки и
 * предварительного расчёта кредита.
 */
@Component
public class PreScoreClientPersonalIdentificationInformationMapper implements Mapper<Client, LoanStatementRequestDto> {
	
	@Override
	public Client dtoToEntity(LoanStatementRequestDto loanStatementRequestDto) {
		Passport passport = Passport.builder()
				.passportUuid(UUID.randomUUID())
				.series(loanStatementRequestDto.getPassportSeries())
				.number(loanStatementRequestDto.getPassportNumber())
				.build();
		
		return Client.builder()
				.clientIdUuid(UUID.randomUUID())
				.lastName(loanStatementRequestDto.getLastName())
				.firstName(loanStatementRequestDto.getFirstName())
				.middleName(loanStatementRequestDto.getMiddleName())
				.birthdate(loanStatementRequestDto.getBirthDate())
				.email(loanStatementRequestDto.getEmail())
				.passport(passport)
				.build();
	}
}
