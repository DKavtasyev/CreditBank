package ru.neostudy.neoflex.calculator.service;

import org.junit.jupiter.api.Test;
import ru.neostudy.neoflex.calculator.dto.PaymentScheduleElementDto;
import ru.neostudy.neoflex.calculator.util.DtoInitializer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SchedulePaymentsCalculatorServiceTest
{
	SchedulePaymentsCalculatorService schedulePaymentsCalculatorService = new SchedulePaymentsCalculatorService();
	List<PaymentScheduleElementDto> expectedScheduleOfPayments = DtoInitializer.initCredit().getPaymentSchedule();
	BigDecimal dailyRate = new BigDecimal("0.12").divide(BigDecimal.valueOf(365), 16, RoundingMode.HALF_EVEN);
	
	@Test
	void countPayment_whenPreviousScheduleElementAndDailyRateReceived_thenReturnAllOtherPaymentScheduleElements()
	{
		List<PaymentScheduleElementDto> actualScheduleOfPayments = new ArrayList<>();
		PaymentScheduleElementDto firstPaymentScheduleElement = expectedScheduleOfPayments.get(0);
		actualScheduleOfPayments.add(firstPaymentScheduleElement);
		schedulePaymentsCalculatorService.countPayment(firstPaymentScheduleElement, actualScheduleOfPayments, dailyRate);
		assertEquals(expectedScheduleOfPayments, actualScheduleOfPayments);
	}
}