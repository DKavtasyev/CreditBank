package ru.neostudy.neoflex.calculator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import ru.neostudy.neoflex.calculator.constants.PercentConstants;
import ru.neostudy.neoflex.calculator.dto.*;
import ru.neostudy.neoflex.calculator.exception.LoanRefusalException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static ru.neostudy.neoflex.calculator.constants.PercentConstants.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class CalculatorService
{
	private final MonthlyPaymentCalculatorService monthlyPaymentCalculatorService;
	private final PersonalRateCalculatorService personalRateCalculatorService;
	private final PercentConstants constants;
	
	private BigDecimal BASE_RATE = constants.BASE_RATE;
	
	public List<LoanOfferDto> preScore(LoanStatementRequestDto loanStatementRequest)
	{
		List<LoanOfferDto> offersList = new ArrayList<>();
		
		for (byte i = 0; i <= 3; i++)
		{
			BigDecimal rate = BASE_RATE;
			BigDecimal amount = loanStatementRequest.getAmount();
			boolean isInsuranceEnabled = i >> 1 > 0;
			boolean isSalaryClient = (i & 1) > 0;
			
			if (isInsuranceEnabled)
			{
				amount = amount.multiply(constants.INSURANCE_PERCENT);
				rate = rate.add(constants.INSURANCE_ENABLED);
			}
			if (isSalaryClient)
				rate = rate.add(constants.SALARY_CLIENT);
			
			BigDecimal monthlyPayment = monthlyPaymentCalculatorService.calculate(amount, loanStatementRequest.getTerm(), rate);
			log.info("Monthly payment has been calculated. Value = " + monthlyPayment.doubleValue());
			
			LoanOfferDto loanOffer = new LoanOfferDto()
					.setStatementId(UUID.randomUUID())
					.setRequestedAmount(loanStatementRequest.getAmount())
					.setTotalAmount(monthlyPayment.multiply(BigDecimal.valueOf(loanStatementRequest.getTerm())))
					.setTerm(loanStatementRequest.getTerm())
					.setMonthlyPayment(monthlyPayment)
					.setRate(rate)
					.setIsInsuranceEnabled(isInsuranceEnabled)
					.setIsSalaryClient(isSalaryClient);
					
			offersList.add(loanOffer);
		}
		return offersList;
	}
	
	public CreditDto score(ScoringDataDto scoringData) throws LoanRefusalException
	{
		BigDecimal rate = personalRateCalculatorService.countPersonalRate(scoringData, BASE_RATE);
		BigDecimal monthlyPayment = monthlyPaymentCalculatorService.calculate(scoringData.getIsInsuranceEnabled() ? scoringData.getAmount().multiply(constants.INSURANCE_PERCENT) : scoringData.getAmount(), scoringData.getTerm(), rate);
		List<PaymentScheduleElementDto> scheduleOfPayments = new ArrayList<>();
		BigDecimal dailyRate = rate.divide(BigDecimal.valueOf(365), 16, RoundingMode.HALF_EVEN);
		
		LocalDate firstPaymentDate = LocalDate.now().plusMonths(1);
		BigDecimal firstInterestPayment = countInterestPayment(scoringData.getAmount(), dailyRate, (int) ChronoUnit.DAYS.between(LocalDate.now(), firstPaymentDate));
		BigDecimal firstDebtPayment = monthlyPayment.subtract(firstInterestPayment);
		BigDecimal firstRemainingDebt = scoringData.getAmount().subtract(firstDebtPayment);
		
		PaymentScheduleElementDto firstPaymentScheduleElement = new PaymentScheduleElementDto()
				.setNumber(1)
				.setDate(firstPaymentDate)
				.setTotalPayment(monthlyPayment)
				.setInterestPayment(firstInterestPayment)
				.setDebtPayment(firstDebtPayment)
				.setRemainingDebt(firstRemainingDebt);
		
		scheduleOfPayments.add(firstPaymentScheduleElement);
		
		countPayment(firstPaymentScheduleElement, scheduleOfPayments, dailyRate);
		
		return new CreditDto()
				.setAmount(scoringData.getAmount())
				.setTerm(scoringData.getTerm())
				.setMonthlyPayment(monthlyPayment)
				.setRate(rate)
				.setPsk(scheduleOfPayments.stream().map(PaymentScheduleElementDto::getTotalPayment).reduce(BigDecimal.ZERO, BigDecimal::add))
				.setIsInsuranceEnabled(scoringData.getIsInsuranceEnabled())
				.setIsSalaryClient(scoringData.getIsSalaryClient())
				.setPaymentSchedule(scheduleOfPayments);
	}
	
	private void countPayment(PaymentScheduleElementDto previousScheduleElement, List<PaymentScheduleElementDto> scheduleOfPayments, BigDecimal dailyRate)
	{
		BigDecimal monthlyPayment = previousScheduleElement.getTotalPayment();
		BigDecimal previousRemainingDebt = previousScheduleElement.getRemainingDebt();
		
		if (previousRemainingDebt.add(previousScheduleElement.getInterestPayment()).compareTo(monthlyPayment) <= 0)
		{
			previousScheduleElement.setTotalPayment(monthlyPayment.add(previousRemainingDebt));
		}
		else
		{
			LocalDate date = previousScheduleElement.getDate().plusMonths(1);
			int numberOfDays = (int) ChronoUnit.DAYS.between(previousScheduleElement.getDate(), date);
			BigDecimal interestPayment = countInterestPayment(previousRemainingDebt, dailyRate, numberOfDays);
			BigDecimal debtPayment = monthlyPayment.subtract(interestPayment);
			BigDecimal remainingDebt = previousRemainingDebt.subtract(debtPayment);
			
			PaymentScheduleElementDto paymentScheduleElement = new PaymentScheduleElementDto()
					.setNumber(previousScheduleElement.getNumber() + 1)
					.setDate(date)
					.setTotalPayment(monthlyPayment)
					.setInterestPayment(interestPayment)
					.setDebtPayment(debtPayment)
					.setRemainingDebt(remainingDebt);
			scheduleOfPayments.add(paymentScheduleElement);
			
			countPayment(paymentScheduleElement, scheduleOfPayments, dailyRate);
		}
	}
	
	private BigDecimal countInterestPayment(BigDecimal remainingDebt, BigDecimal dailyRate, int numberOfDays)
	{
		return remainingDebt.multiply(dailyRate.multiply(BigDecimal.valueOf(numberOfDays))).setScale(16, RoundingMode.HALF_EVEN);
	}
	
	
}
