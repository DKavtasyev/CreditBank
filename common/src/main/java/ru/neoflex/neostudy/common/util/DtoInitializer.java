package ru.neoflex.neostudy.common.util;

import lombok.experimental.UtilityClass;
import org.springframework.context.annotation.Profile;
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

/**
 * Утилитный класс, используется для создания и инициализации сущностей entity и dto, которые содержат в себе
 * пользовательские данные. Используется при проведении unit-тестирования микросервисов для инициализации ожидаемых
 * объектов, с которыми сравниваются результаты тестирования.
 */
@UtilityClass
@Profile("test")
public class DtoInitializer {
	public static final BigDecimal BASE_RATE = new BigDecimal("0.12");
	public static final BigDecimal OFFER_0_TOTAL_AMOUNT = new BigDecimal("1035290.2002652885215750");
	public static final BigDecimal OFFER_0_MONTHLY_PAYMENT = new BigDecimal("172548.3667108814202625");
	public static final BigDecimal OFFER_1_MONTHLY_PAYMENT = new BigDecimal("172054.5475593009795123");
	public static final BigDecimal OFFER_2_MONTHLY_PAYMENT = new BigDecimal("179622.3528220476265425");
	public static final BigDecimal OFFER_3_MONTHLY_PAYMENT = new BigDecimal("179105.9426200903968720");
	public static final LocalDate DATE = LocalDate.of(2024, Month.JULY, 10);
	public static final int AGE = 25;
	
	/**
	 * Инициализирует LoanStatementRequestDto.
	 * @return initialized LoanStatementRequestDto
	 */
	public static LoanStatementRequestDto initLoanStatementRequest() {
		return LoanStatementRequestDto.builder()
				.amount(BigDecimal.valueOf(1_000_000))
				.term(6)
				.firstName("Ivan")
				.lastName("Ivanov")
				.middleName("Vasilievich")
				.email("ivanov@mail.ru")
				.birthDate(LocalDate.now().minusYears(AGE))
				.passportSeries("1234")
				.passportNumber("123456")
				.build();
	}
	
	/**
	 * Инициализирует список из четырёх предложений кредита LoanOfferDto.
	 * @return List, содержащий четыре предложения LoanOfferDto.
	 */
	public static List<LoanOfferDto> initOffers() {
		LoanOfferDto loanOfferOne = LoanOfferDto.builder()
				.requestedAmount(BigDecimal.valueOf(1_000_000))
				.totalAmount(OFFER_0_TOTAL_AMOUNT)
				.term(6)
				.monthlyPayment(OFFER_0_MONTHLY_PAYMENT)
				.rate(BASE_RATE)
				.isInsuranceEnabled(false)
				.isSalaryClient(false)
				.build();
		
		LoanOfferDto loanOfferTwo = LoanOfferDto.builder()
				.requestedAmount(BigDecimal.valueOf(1_000_000))
				.totalAmount(new BigDecimal("1032327.2853558058770738"))
				.term(6)
				.monthlyPayment(OFFER_1_MONTHLY_PAYMENT)
				.rate(new BigDecimal("0.11"))
				.isInsuranceEnabled(false)
				.isSalaryClient(true)
				.build();
		
		LoanOfferDto loanOfferThree = LoanOfferDto.builder()
				.requestedAmount(BigDecimal.valueOf(1_000_000))
				.totalAmount(new BigDecimal("1077734.1169322857592550"))
				.term(6)
				.monthlyPayment(OFFER_2_MONTHLY_PAYMENT)
				.rate(new BigDecimal("0.09"))
				.isInsuranceEnabled(true)
				.isSalaryClient(false)
				.build();
		
		LoanOfferDto loanOfferFour = LoanOfferDto.builder()
				.requestedAmount(BigDecimal.valueOf(1_000_000))
				.totalAmount(new BigDecimal("1074635.6557205423812320"))
				.term(6)
				.monthlyPayment(OFFER_3_MONTHLY_PAYMENT)
				.rate(new BigDecimal("0.08"))
				.isInsuranceEnabled(true)
				.isSalaryClient(true)
				.build();
		
		List<LoanOfferDto> offers = new ArrayList<>();
		Collections.addAll(offers, loanOfferOne, loanOfferTwo, loanOfferThree, loanOfferFour);
		return offers;
	}
	
	/**
	 * Инициализирует данные, необходимые для оценки условий кредита.
	 * @return ScoringDataDto.
	 */
	public static ScoringDataDto initScoringData() {
		EmploymentDto employment = initEmploymentDto();
		
		return ScoringDataDto.builder()
				.amount(BigDecimal.valueOf(1_000_000))
				.term(6)
				.firstName("Ivan")
				.lastName("Ivanov")
				.middleName("Vasilievich")
				.gender(Gender.MALE)
				.birthdate(LocalDate.now().minusYears(AGE))
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
	
	/**
	 * Инициализирует dto с информацией о месте работы пользователя.
	 * @return EmploymentDto
	 */
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
	
	/**
	 * Инициализирует dto, содержащее полную информацию о графике и суммах платежей по кредиту. По причине разного
	 * количества дней в месяцах, для успешного прохождения тестов указана фиксированная дата взятия кредита.
	 * @return CreditDto.
	 */
	public CreditDto initCreditDto() {
		PaymentScheduleElementDto paymentScheduleElementOne = PaymentScheduleElementDto.builder()
				.number(1)
				.date(DATE.plusMonths(1))
				.totalPayment(OFFER_0_MONTHLY_PAYMENT)
				.interestPayment(new BigDecimal("9863.0136986310000000"))
				.debtPayment(new BigDecimal("162685.3530122504202625"))
				.remainingDebt(new BigDecimal("837314.6469877495797375"))
				.build();
		
		PaymentScheduleElementDto paymentScheduleElementTwo = PaymentScheduleElementDto.builder()
				.number(2)
				.date(DATE.plusMonths(2))
				.totalPayment(OFFER_0_MONTHLY_PAYMENT)
				.interestPayment(new BigDecimal("8533.7273610813725549"))
				.debtPayment(new BigDecimal("164014.6393498000477076"))
				.remainingDebt(new BigDecimal("673300.0076379495320299"))
				.build();
		
		PaymentScheduleElementDto paymentScheduleElementThree = PaymentScheduleElementDto.builder()
				.number(3)
				.date(DATE.plusMonths(3))
				.totalPayment(OFFER_0_MONTHLY_PAYMENT)
				.interestPayment(new BigDecimal("6640.7671986214531639"))
				.debtPayment(new BigDecimal("165907.5995122599670986"))
				.remainingDebt(new BigDecimal("507392.4081256895649313"))
				.build();
		
		PaymentScheduleElementDto paymentScheduleElementFour = PaymentScheduleElementDto.builder()
				.number(4)
				.date(DATE.plusMonths(4))
				.totalPayment(OFFER_0_MONTHLY_PAYMENT)
				.interestPayment(new BigDecimal("5171.2322143225488706"))
				.debtPayment(new BigDecimal("167377.1344965588713919"))
				.remainingDebt(new BigDecimal("340015.2736291306935394"))
				.build();
		
		PaymentScheduleElementDto paymentScheduleElementFive = PaymentScheduleElementDto.builder()
				.number(5)
				.date(DATE.plusMonths(5))
				.totalPayment(OFFER_0_MONTHLY_PAYMENT)
				.interestPayment(new BigDecimal("3353.5753015478838399"))
				.debtPayment(new BigDecimal("169194.7914093335364226"))
				.remainingDebt(new BigDecimal("170820.4822197971571168"))
				.build();
		
		PaymentScheduleElementDto paymentScheduleElementSix = PaymentScheduleElementDto.builder()
				.number(6)
				.date(DATE.plusMonths(6))
				.totalPayment(new BigDecimal("172561.4471344757900662"))
				.interestPayment(new BigDecimal("1740.9649146786329494"))
				.debtPayment(new BigDecimal("170807.4017962027873131"))
				.remainingDebt(new BigDecimal("13.0804235943698037"))
				.build();
		
		List<PaymentScheduleElementDto> scheduleOfPayments = new ArrayList<>();
		Collections.addAll(scheduleOfPayments,
				paymentScheduleElementOne,
				paymentScheduleElementTwo,
				paymentScheduleElementThree,
				paymentScheduleElementFour,
				paymentScheduleElementFive,
				paymentScheduleElementSix);
		
		return CreditDto.builder()
				.amount(BigDecimal.valueOf(1_000_000))
				.term(6)
				.monthlyPayment(OFFER_0_MONTHLY_PAYMENT)
				.rate(BASE_RATE)
				.psk(new BigDecimal("1035417.8805246017999105"))
				.isInsuranceEnabled(false)
				.isSalaryClient(false)
				.paymentSchedule(scheduleOfPayments)
				.build();
	}
	
	/**
	 * Инициализирует один экземплят LoanOfferDto.
	 * @return LoanOfferDto.
	 */
	public static LoanOfferDto initLoanOfferDto() {
		return LoanOfferDto.builder()
				.statementId(UUID.randomUUID())
				.requestedAmount(BigDecimal.valueOf(1_000_000))
				.totalAmount(new BigDecimal("1032327.2853558058770738"))
				.term(6)
				.monthlyPayment(OFFER_1_MONTHLY_PAYMENT)
				.rate(new BigDecimal("0.11"))
				.isInsuranceEnabled(false)
				.isSalaryClient(true)
				.build();
	}
	
	/**
	 * Инициализирует dto, содержащий информацию для конечного оформления кредита.
	 * @return FinishingRegistrationRequestDto.
	 */
	public static FinishingRegistrationRequestDto initFinishingRegistrationRequest() {
		return FinishingRegistrationRequestDto.builder()
				.gender(Gender.MALE)
				.maritalStatus(MaritalStatus.SINGLE)
				.dependentAmount(0)
				.passportIssueDate(LocalDate.now().minusYears(5))
				.passportIssueBranch("ГУ МВД ПО Г. МОСКВА")
				.employment(initEmploymentDto())
				.accountNumber("1234151234")
				.build();
	}
}
