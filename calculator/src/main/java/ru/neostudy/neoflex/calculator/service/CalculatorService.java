package ru.neostudy.neoflex.calculator.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.neostudy.neoflex.calculator.config.CreditConfig;
import ru.neostudy.neoflex.calculator.config.RateConfig;
import ru.neostudy.neoflex.calculator.dto.*;
import ru.neostudy.neoflex.calculator.exception.LoanRefusalException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


@Log4j2
@Service
@RequiredArgsConstructor
public class CalculatorService
{
	private final MonthlyPaymentCalculatorService monthlyPaymentCalculatorService;
	private final PersonalRateCalculatorService personalRateCalculatorService;
	private final SchedulePaymentsCalculatorService schedulePaymentsCalculatorService;
	private final RateConfig rateConfig;
	private final CreditConfig creditConfig;
	
	private BigDecimal BASE_RATE;
	private BigDecimal INSURANCE_PERCENT;
	private BigDecimal INSURANCE_ENABLED;
	private BigDecimal SALARY_CLIENT;
	
	@PostConstruct
	private void init()
	{
		BASE_RATE = new BigDecimal(rateConfig.getBaseRate());
		INSURANCE_PERCENT = new BigDecimal(creditConfig.getInsurancePercent());
		INSURANCE_ENABLED = new BigDecimal(rateConfig.getInsuranceEnabled());
		SALARY_CLIENT = new BigDecimal(rateConfig.getSalaryClient());
	}
	
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
				amount = amount.multiply(INSURANCE_PERCENT);
				rate = rate.add(INSURANCE_ENABLED);
			}
			if (isSalaryClient)
				rate = rate.add(SALARY_CLIENT);
			
			BigDecimal monthlyPayment = monthlyPaymentCalculatorService.calculate(amount, loanStatementRequest.getTerm(), rate);
			log.info("Monthly payment has been calculated. Value = " + monthlyPayment.doubleValue());
			
			LoanOfferDto loanOffer = new LoanOfferDto()
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
		if (scoringData.getIsInsuranceEnabled())
			scoringData.setAmount(scoringData.getAmount().multiply(INSURANCE_PERCENT));
		
		BigDecimal rate = personalRateCalculatorService.countPersonalRate(scoringData, BASE_RATE);
		BigDecimal monthlyPayment = monthlyPaymentCalculatorService.calculate(scoringData.getAmount(), scoringData.getTerm(), rate);
		List<PaymentScheduleElementDto> scheduleOfPayments = new ArrayList<>();
		BigDecimal dailyRate = rate.divide(BigDecimal.valueOf(365), 16, RoundingMode.HALF_EVEN);
		
		LocalDate firstPaymentDate = LocalDate.now().plusMonths(1);
		BigDecimal firstInterestPayment = schedulePaymentsCalculatorService.countInterestPayment(scoringData.getAmount(), dailyRate, (int) ChronoUnit.DAYS.between(LocalDate.now(), firstPaymentDate));
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
		
		schedulePaymentsCalculatorService.countPayment(firstPaymentScheduleElement, scheduleOfPayments, dailyRate);
		
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
}
