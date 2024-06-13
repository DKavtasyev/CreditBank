package ru.neoflex.neostudy.common.util;

import lombok.experimental.UtilityClass;
import ru.neoflex.neostudy.common.constants.EmploymentPosition;
import ru.neoflex.neostudy.common.constants.EmploymentStatus;
import ru.neoflex.neostudy.common.constants.Gender;
import ru.neoflex.neostudy.common.constants.MaritalStatus;
import ru.neoflex.neostudy.common.dto.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class DtoInitializer
{
	public static LoanStatementRequestDto initLoanStatementRequest()
	{
		return LoanStatementRequestDto.builder()
				.amount(BigDecimal.valueOf(1_000_000))
				.term(6)
				.firstName("Ivan")
				.lastName("Ivanov")
				.middleName("Vasilievich")
				.email("ivanov@mail.ru")
				.birthDate(LocalDate.now().minusYears(25))
				.passportSeries("1234")
				.passportNumber("123456")
				.build();
	}
	
	public static List<LoanOfferDto> initOffers()
	{
		LoanOfferDto loanOfferOne = new LoanOfferDto()
				.setStatementId(UUID.randomUUID())
				.setRequestedAmount(BigDecimal.valueOf(1_000_000))
				.setTotalAmount(new BigDecimal("1035290.2002652885215750"))
				.setTerm(6)
				.setMonthlyPayment(new BigDecimal("172548.3667108814202625"))
				.setRate(new BigDecimal("0.12"))
				.setIsInsuranceEnabled(false)
				.setIsSalaryClient(false);
		
		LoanOfferDto loanOfferTwo = new LoanOfferDto()
				.setStatementId(UUID.randomUUID())
				.setRequestedAmount(BigDecimal.valueOf(1_000_000))
				.setTotalAmount(new BigDecimal("1032327.2853558058770738"))
				.setTerm(6)
				.setMonthlyPayment(new BigDecimal("172054.5475593009795123"))
				.setRate(new BigDecimal("0.11"))
				.setIsInsuranceEnabled(false)
				.setIsSalaryClient(true);
		
		LoanOfferDto loanOfferThree = new LoanOfferDto()
				.setStatementId(UUID.randomUUID())
				.setRequestedAmount(BigDecimal.valueOf(1_000_000))
				.setTotalAmount(new BigDecimal("1077734.1169322857592550"))
				.setTerm(6)
				.setMonthlyPayment(new BigDecimal("179622.3528220476265425"))
				.setRate(new BigDecimal("0.09"))
				.setIsInsuranceEnabled(true)
				.setIsSalaryClient(false);
		
		LoanOfferDto loanOfferFour = new LoanOfferDto()
				.setStatementId(UUID.randomUUID())
				.setRequestedAmount(BigDecimal.valueOf(1_000_000))
				.setTotalAmount(new BigDecimal("1074635.6557205423812320"))
				.setTerm(6)
				.setMonthlyPayment(new BigDecimal("179105.9426200903968720"))
				.setRate(new BigDecimal("0.08"))
				.setIsInsuranceEnabled(true)
				.setIsSalaryClient(true);
		
		List<LoanOfferDto> offers = new ArrayList<>();
		Collections.addAll(offers, loanOfferOne, loanOfferTwo, loanOfferThree, loanOfferFour);
		return offers;
	}
	
	public static ScoringDataDto initScoringData()
	{
		EmploymentDto employment = EmploymentDto.builder()
				.employmentStatus(EmploymentStatus.EMPLOYED)
				.employmentINN("12345678")
				.salary(BigDecimal.valueOf(150_000))
				.position(EmploymentPosition.WORKER)
				.workExperienceTotal(60)
				.workExperienceCurrent(24)
				.build();
		
		return ScoringDataDto.builder()
				.amount(BigDecimal.valueOf(1_000_000))
				.term(6)
				.firstName("Ivan")
				.lastName("Ivanov")
				.middleName("Vasilievich")
				.gender(Gender.MALE)
				.birthdate(LocalDate.now().minusYears(25))
				.passportSeries("1234")
				.passportNumber("123456")
				.passportIssueDate(LocalDate.now().minusYears(5))
				.passportIssueBranch("ГУ МВД РОССИИ ПО Г. МОСКВЕ")
				.maritalStatus(MaritalStatus.SINGLE)
				.dependentAmount(0)
				.employment(employment)
				.accountNumber("1234567890")
				.isInsuranceEnabled(false)
				.isSalaryClient(false)
				.build();
	}
	
	public CreditDto initCredit()
	{
		PaymentScheduleElementDto paymentScheduleElementOne = new PaymentScheduleElementDto()
				.setNumber(1)
				.setDate(LocalDate.now().plusMonths(1))
				.setTotalPayment(new BigDecimal("172548.3667108814202625"))
				.setInterestPayment(new BigDecimal("9863.0136986310000000"))
				.setDebtPayment(new BigDecimal("162685.3530122504202625"))
				.setRemainingDebt(new BigDecimal("837314.6469877495797375"));
		
		PaymentScheduleElementDto paymentScheduleElementTwo = new PaymentScheduleElementDto()
				.setNumber(2)
				.setDate(LocalDate.now().plusMonths(2))
				.setTotalPayment(new BigDecimal("172548.3667108814202625"))
				.setInterestPayment(new BigDecimal("8533.7273610813725549"))
				.setDebtPayment(new BigDecimal("164014.6393498000477076"))
				.setRemainingDebt(new BigDecimal("673300.0076379495320299"));
		
		PaymentScheduleElementDto paymentScheduleElementThree = new PaymentScheduleElementDto()
				.setNumber(3)
				.setDate(LocalDate.now().plusMonths(3))
				.setTotalPayment(new BigDecimal("172548.3667108814202625"))
				.setInterestPayment(new BigDecimal("6862.1261052421682693"))
				.setDebtPayment(new BigDecimal("165686.2406056392519932"))
				.setRemainingDebt(new BigDecimal("507613.7670323102800367"));
		
		PaymentScheduleElementDto paymentScheduleElementFour = new PaymentScheduleElementDto()
				.setNumber(4)
				.setDate(LocalDate.now().plusMonths(4))
				.setTotalPayment(new BigDecimal("172548.3667108814202625"))
				.setInterestPayment(new BigDecimal("5006.6015378533613876"))
				.setDebtPayment(new BigDecimal("167541.7651730280588749"))
				.setRemainingDebt(new BigDecimal("340072.0018592822211618"));
		
		PaymentScheduleElementDto paymentScheduleElementFive = new PaymentScheduleElementDto()
				.setNumber(5)
				.setDate(LocalDate.now().plusMonths(5))
				.setTotalPayment(new BigDecimal("172548.3667108814202625"))
				.setInterestPayment(new BigDecimal("3465.9393066209330306"))
				.setDebtPayment(new BigDecimal("169082.4274042604872319"))
				.setRemainingDebt(new BigDecimal("170989.5744550217339299"));
		
		PaymentScheduleElementDto paymentScheduleElementSix = new PaymentScheduleElementDto()
				.setNumber(6)
				.setDate(LocalDate.now().plusMonths(6))
				.setTotalPayment(new BigDecimal("172676.0469701946985980"))
				.setInterestPayment(new BigDecimal("1686.4725151729646681"))
				.setDebtPayment(new BigDecimal("170861.8941957084555944"))
				.setRemainingDebt(new BigDecimal("127.6802593132783355"));
		
		List<PaymentScheduleElementDto> scheduleOfPayments = new ArrayList<>();
		Collections.addAll(scheduleOfPayments,
				paymentScheduleElementOne,
				paymentScheduleElementTwo,
				paymentScheduleElementThree,
				paymentScheduleElementFour,
				paymentScheduleElementFive,
				paymentScheduleElementSix);
		
		return new CreditDto()
				.setAmount(BigDecimal.valueOf(1_000_000))
				.setTerm(6)
				.setMonthlyPayment(new BigDecimal("172548.3667108814202625"))
				.setRate(new BigDecimal("0.12"))
				.setPsk(new BigDecimal("1035417.8805246017999105"))
				.setIsInsuranceEnabled(false)
				.setIsSalaryClient(false)
				.setPaymentSchedule(scheduleOfPayments);
	}
}
