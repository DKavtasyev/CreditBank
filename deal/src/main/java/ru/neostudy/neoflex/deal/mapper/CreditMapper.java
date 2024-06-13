package ru.neostudy.neoflex.deal.mapper;

import org.springframework.stereotype.Component;
import ru.neostudy.neoflex.deal.dto.CreditDto;
import ru.neostudy.neoflex.deal.entity.Credit;

import java.util.UUID;

@Component
public class CreditMapper implements Mapper<Credit, CreditDto>
{
	
	@Override
	public Credit dtoToEntity(CreditDto creditDto)
	{
		return new Credit()
				.setCreditId(UUID.randomUUID())
				.setAmount(creditDto.getAmount())
				.setTerm(creditDto.getTerm())
				.setMonthlyPayment(creditDto.getMonthlyPayment())
				.setRate(creditDto.getRate())
				.setPsk(creditDto.getPsk())
				.setPaymentSchedule(creditDto.getPaymentSchedule())
				.setInsuranceEnabled(creditDto.getIsInsuranceEnabled())
				.setSalaryClient(creditDto.getIsSalaryClient());
	}
}
