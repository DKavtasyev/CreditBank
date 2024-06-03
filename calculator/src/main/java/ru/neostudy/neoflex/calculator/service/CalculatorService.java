package ru.neostudy.neoflex.calculator.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.neostudy.neoflex.calculator.constants.EmploymentStatus;
import ru.neostudy.neoflex.calculator.constants.Gender;
import ru.neostudy.neoflex.calculator.constants.MaritalStatus;
import ru.neostudy.neoflex.calculator.constants.Position;
import ru.neostudy.neoflex.calculator.dto.*;
import ru.neostudy.neoflex.calculator.exception.LoanRefusalException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
@Service
public class CalculatorService
{
	@Value("${base_rate}")
	private BigDecimal BASE_RATE;
	
	private final BigDecimal onePoint = BigDecimal.valueOf(0.01);
	private final BigDecimal twoPoints = BigDecimal.valueOf(0.02);
	private final BigDecimal threePoints = BigDecimal.valueOf(0.03);
	private final BigDecimal sevenPoints = BigDecimal.valueOf(0.07);
	
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
				amount = amount.multiply(BigDecimal.valueOf(1.05));
				rate = rate.subtract(threePoints);
			}
			if (isSalaryClient)
				rate = rate.subtract(onePoint);
			
			BigDecimal monthlyPayment = calculateMonthlyPayment(amount, loanStatementRequest.getTerm(), rate);
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
	
	private BigDecimal calculateMonthlyPayment(BigDecimal amount, Integer term, BigDecimal rate)
	{
		BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(12), 16, RoundingMode.HALF_EVEN);
		BigDecimal denominator = BigDecimal.ONE.add(monthlyRate).pow(term).subtract(BigDecimal.ONE).setScale(16, RoundingMode.HALF_EVEN);
		return monthlyRate.divide(denominator, 22, RoundingMode.HALF_EVEN).add(monthlyRate).multiply(amount).setScale(16, RoundingMode.HALF_EVEN);
	}
	
	public CreditDto score(ScoringDataDto scoringData) throws LoanRefusalException
	{
		BigDecimal rate = countPersonalRate(scoringData, BASE_RATE);
		BigDecimal monthlyPayment = calculateMonthlyPayment(scoringData.getIsInsuranceEnabled() ? scoringData.getAmount().multiply(BigDecimal.valueOf(1.05)) : scoringData.getAmount(), scoringData.getTerm(), rate);
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
		if (previousScheduleElement.getRemainingDebt().add(previousScheduleElement.getInterestPayment()).compareTo(previousScheduleElement.getTotalPayment()) <= 0)
		{
			BigDecimal monthlyPayment = previousScheduleElement.getTotalPayment();
			BigDecimal remainingDebt = previousScheduleElement.getRemainingDebt();
			previousScheduleElement.setTotalPayment(monthlyPayment.add(remainingDebt));
		}
		else
		{
			LocalDate date = previousScheduleElement.getDate().plusMonths(1);
			int numberOfDays = (int) ChronoUnit.DAYS.between(previousScheduleElement.getDate(), date);
			BigDecimal interestPayment = countInterestPayment(previousScheduleElement.getRemainingDebt(), dailyRate, numberOfDays);
			BigDecimal debtPayment = previousScheduleElement.getTotalPayment().subtract(interestPayment);
			BigDecimal remainingDebt = previousScheduleElement.getRemainingDebt().subtract(debtPayment);
			
			PaymentScheduleElementDto paymentScheduleElement = new PaymentScheduleElementDto()
					.setNumber(previousScheduleElement.getNumber() + 1)
					.setDate(date)
					.setTotalPayment(previousScheduleElement.getTotalPayment())
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
	
	private BigDecimal countPersonalRate(ScoringDataDto scoringData, BigDecimal rate) throws LoanRefusalException
	{
		EmploymentDto employmentDto = scoringData.getEmployment();
		int age = (int) ChronoUnit.YEARS.between(scoringData.getBirthdate(), LocalDate.now());
		
		boolean inappropriateWorkExperience = employmentDto.getWorkExperienceTotal() < 18 || employmentDto.getWorkExperienceCurrent() < 3;
		boolean inappropriateAge = age < 20 || age > 65;
		boolean inappropriateAmount = scoringData.getAmount().compareTo(employmentDto.getSalary().multiply(BigDecimal.valueOf(25))) > 0;
		boolean isUnemployed = employmentDto.getEmploymentStatus().equals(EmploymentStatus.UNEMPLOYED);
		
		if (inappropriateWorkExperience || inappropriateAge || inappropriateAmount || isUnemployed)
			throw new LoanRefusalException();
		else
		{
			if (employmentDto.getEmploymentStatus().equals(EmploymentStatus.SELF_EMPLOYED))
				rate = rate.add(onePoint);
			else if (employmentDto.getEmploymentStatus().equals(EmploymentStatus.BUSINESS_OWNER))
				rate = rate.add(twoPoints);
			
			if (employmentDto.getPosition().equals(Position.MIDDLE_MANAGER))
				rate = rate.subtract(twoPoints);
			else if (employmentDto.getPosition().equals(Position.TOP_MANAGER))
				rate = rate.subtract(threePoints);
			
			if (scoringData.getMaritalStatus().equals(MaritalStatus.MARRIED))
				rate = rate.subtract(threePoints);
			else if (scoringData.getMaritalStatus().equals(MaritalStatus.DIVORCED))
				rate = rate.add(onePoint);
			
			if (((scoringData.getGender().equals(Gender.FEMALE) && age > 32 && age < 60)) || ((scoringData.getGender().equals(Gender.MALE) && age > 30 && age < 55)))
				rate = rate.subtract(threePoints);
			else if (scoringData.getGender().equals(Gender.NON_BINARY))
				rate = rate.add(sevenPoints);
			
			if (scoringData.getIsInsuranceEnabled())
				rate = rate.subtract(BigDecimal.valueOf(0.03));
			if (scoringData.getIsSalaryClient())
				rate = rate.subtract(BigDecimal.valueOf(0.01));
		}
		return rate.max(BigDecimal.ZERO);
	}
}
