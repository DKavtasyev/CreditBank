package ru.neoflex.neostudy.common.util;

import lombok.experimental.UtilityClass;
import ru.neoflex.neostudy.common.constants.EmploymentPosition;
import ru.neoflex.neostudy.common.constants.EmploymentStatus;
import ru.neoflex.neostudy.common.constants.Gender;
import ru.neoflex.neostudy.common.constants.MaritalStatus;
import ru.neoflex.neostudy.common.dto.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class DtoInitializer {
	public static LoanStatementRequestDto initLoanStatementRequest() {
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
	
	public static List<LoanOfferDto> initOffers() {
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
	
	public static ScoringDataDto initScoringData() {
		EmploymentDto employment = initEmploymentDto();
		
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
	
	public static EmploymentDto initEmploymentDto() {
		return EmploymentDto.builder()
				.employmentStatus(EmploymentStatus.EMPLOYED)
				.employmentINN("123456781234")
				.salary(BigDecimal.valueOf(150_000))
				.position(EmploymentPosition.WORKER)
				.workExperienceTotal(60)
				.workExperienceCurrent(24)
				.build();
	}
	
	public CreditDto initCreditDto() {
		LocalDate date = LocalDate.of(2024, Month.JULY, 10);
		PaymentScheduleElementDto paymentScheduleElementOne = new PaymentScheduleElementDto()
				.setNumber(1)
				.setDate(date.plusMonths(1))
				.setTotalPayment(new BigDecimal("172548.3667108814202625"))
				.setInterestPayment(new BigDecimal("9863.0136986310000000"))
				.setDebtPayment(new BigDecimal("162685.3530122504202625"))
				.setRemainingDebt(new BigDecimal("837314.6469877495797375"));
		
		PaymentScheduleElementDto paymentScheduleElementTwo = new PaymentScheduleElementDto()
				.setNumber(2)
				.setDate(date.plusMonths(2))
				.setTotalPayment(new BigDecimal("172548.3667108814202625"))
				.setInterestPayment(new BigDecimal("8533.7273610813725549"))
				.setDebtPayment(new BigDecimal("164014.6393498000477076"))
				.setRemainingDebt(new BigDecimal("673300.0076379495320299"));
		
		PaymentScheduleElementDto paymentScheduleElementThree = new PaymentScheduleElementDto()
				.setNumber(3)
				.setDate(date.plusMonths(3))
				.setTotalPayment(new BigDecimal("172548.3667108814202625"))
				.setInterestPayment(new BigDecimal("6640.7671986214531639"))
				.setDebtPayment(new BigDecimal("165907.5995122599670986"))
				.setRemainingDebt(new BigDecimal("507392.4081256895649313"));
		
		PaymentScheduleElementDto paymentScheduleElementFour = new PaymentScheduleElementDto()
				.setNumber(4)
				.setDate(date.plusMonths(4))
				.setTotalPayment(new BigDecimal("172548.3667108814202625"))
				.setInterestPayment(new BigDecimal("5171.2322143225488706"))
				.setDebtPayment(new BigDecimal("167377.1344965588713919"))
				.setRemainingDebt(new BigDecimal("340015.2736291306935394"));
		
		PaymentScheduleElementDto paymentScheduleElementFive = new PaymentScheduleElementDto()
				.setNumber(5)
				.setDate(date.plusMonths(5))
				.setTotalPayment(new BigDecimal("172548.3667108814202625"))
				.setInterestPayment(new BigDecimal("3353.5753015478838399"))
				.setDebtPayment(new BigDecimal("169194.7914093335364226"))
				.setRemainingDebt(new BigDecimal("170820.4822197971571168"));
		
		PaymentScheduleElementDto paymentScheduleElementSix = new PaymentScheduleElementDto()
				.setNumber(6)
				.setDate(date.plusMonths(6))
				.setTotalPayment(new BigDecimal("172561.4471344757900662"))
				.setInterestPayment(new BigDecimal("1740.9649146786329494"))
				.setDebtPayment(new BigDecimal("170807.4017962027873131"))
				.setRemainingDebt(new BigDecimal("13.0804235943698037"));
		
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
	
	public static LoanOfferDto initLoanOfferDto() {
		return new LoanOfferDto()
				.setStatementId(UUID.randomUUID())
				.setRequestedAmount(BigDecimal.valueOf(1_000_000))
				.setTotalAmount(new BigDecimal("1032327.2853558058770738"))
				.setTerm(6)
				.setMonthlyPayment(new BigDecimal("172054.5475593009795123"))
				.setRate(new BigDecimal("0.11"))
				.setIsInsuranceEnabled(false)
				.setIsSalaryClient(true);
	}
	
	public static FinishingRegistrationRequestDto initFinishingRegistrationRequest() {
		return new FinishingRegistrationRequestDto()
				.setGender(Gender.MALE)
				.setMaritalStatus(MaritalStatus.SINGLE)
				.setDependentAmount(0)
				.setPassportIssueDate(LocalDate.now().minusYears(5))
				.setPassportIssueBranch("ГУ МВД ПО Г. МОСКВА")
				.setEmployment(initEmploymentDto())
				.setAccountNumber("1234151234");
	}
}
