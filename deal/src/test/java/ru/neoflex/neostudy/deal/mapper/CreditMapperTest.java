package ru.neoflex.neostudy.deal.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.neoflex.neostudy.common.dto.CreditDto;
import ru.neoflex.neostudy.common.util.DtoInitializer;
import ru.neoflex.neostudy.deal.entity.Credit;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class CreditMapperTest
{
	private final CreditMapper creditMapper = new CreditMapper();
	private CreditDto creditDto;
	
	@BeforeEach
	void init()
	{
		creditDto = DtoInitializer.initCreditDto();
	}
	
	
	@Nested
	@DisplayName("Тестирование метода CreditMapper:dtoToEntity()")
	class TestingDtoToEntityMethod
	{
		@Test
		void dtoToEntity_whenGivenCreditDto_thenReturnCredit()
		{
			Credit actualCredit = creditMapper.dtoToEntity(creditDto);
			assertAll(() -> {
				assertThat(actualCredit.getAmount().compareTo(creditDto.getAmount()) == 0).isTrue();
				assertThat(actualCredit.getTerm()).isEqualTo(creditDto.getTerm());
				assertThat(actualCredit.getMonthlyPayment().compareTo(creditDto.getMonthlyPayment()) == 0).isTrue();
				assertThat(actualCredit.getRate().compareTo(creditDto.getRate()) == 0).isTrue();
				assertThat(actualCredit.getPsk().compareTo(creditDto.getPsk()) == 0).isTrue();
				assertThat(actualCredit.getPaymentSchedule()).isEqualTo(creditDto.getPaymentSchedule());
				assertThat(actualCredit.isInsuranceEnabled()).isEqualTo(creditDto.getIsInsuranceEnabled());
				assertThat(actualCredit.isSalaryClient()).isEqualTo(creditDto.getIsSalaryClient());
			});
		}
	}
}