package ru.neostudy.neoflex.deal.mapper;

import org.springframework.stereotype.Component;
import ru.neostudy.neoflex.deal.dto.LoanStatementRequestDto;
import ru.neostudy.neoflex.deal.entity.Client;
import ru.neostudy.neoflex.deal.entity.jsonb.Passport;

import java.util.UUID;

@Component
public class PreScoreClientPersonalIdentificationInformationMapper implements Mapper<Client, LoanStatementRequestDto>
{
	
	@Override
	public Client dtoToEntity(LoanStatementRequestDto loanStatementRequestDto)
	{
		Passport passport = new Passport()
				.setPassportUuid(UUID.randomUUID())
				.setSeries(loanStatementRequestDto.getPassportSeries())
				.setNumber(loanStatementRequestDto.getPassportNumber());
				
		return new Client()
				.setClientIdUuid(UUID.randomUUID())
				.setLastName(loanStatementRequestDto.getLastName())
				.setFirstName(loanStatementRequestDto.getFirstName())
				.setMiddleName(loanStatementRequestDto.getMiddleName())
				.setBirthdate(loanStatementRequestDto.getBirthDate())
				.setEmail(loanStatementRequestDto.getEmail())
				.setPassport(passport);
	}
}
