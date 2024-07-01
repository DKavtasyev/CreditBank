package ru.neoflex.neostudy.deal.mapper;

import org.springframework.stereotype.Component;
import ru.neoflex.neostudy.common.dto.CreditDto;
import ru.neoflex.neostudy.deal.entity.Credit;

import java.util.UUID;

@Component
public class CreditMapper implements Mapper<Credit, CreditDto> {
	
	@Override
	public Credit dtoToEntity(CreditDto creditDto) {
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
