package ru.neoflex.neostudy.deal.mapper;

import org.springframework.stereotype.Component;
import ru.neoflex.neostudy.common.dto.CreditDto;
import ru.neoflex.neostudy.deal.entity.Credit;

import java.util.UUID;

/**
 * Класс, использующийся для маппинга объектов DTO и entity кредита.
 */
@Component
public class CreditMapper implements Mapper<Credit, CreditDto> {
	
	@Override
	public Credit dtoToEntity(CreditDto creditDto) {
		return Credit.builder()
				.creditId(UUID.randomUUID())
				.amount(creditDto.getAmount())
				.term(creditDto.getTerm())
				.monthlyPayment(creditDto.getMonthlyPayment())
				.rate(creditDto.getRate())
				.psk(creditDto.getPsk())
				.paymentSchedule(creditDto.getPaymentSchedule())
				.insuranceEnabled(creditDto.getIsInsuranceEnabled())
				.salaryClient(creditDto.getIsSalaryClient())
				.build();
	}
}
