package ru.neoflex.neostudy.deal.util;

import lombok.experimental.UtilityClass;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.constants.ChangeType;
import ru.neoflex.neostudy.common.constants.Gender;
import ru.neoflex.neostudy.common.constants.MaritalStatus;
import ru.neoflex.neostudy.common.dto.CreditDto;
import ru.neoflex.neostudy.common.dto.EmploymentDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.util.DtoInitializer;
import ru.neoflex.neostudy.deal.entity.Client;
import ru.neoflex.neostudy.deal.entity.Credit;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.entity.jsonb.Employment;
import ru.neoflex.neostudy.deal.entity.jsonb.Passport;
import ru.neoflex.neostudy.deal.entity.jsonb.StatementStatusHistory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.UUID;

@UtilityClass
public class EntityInitializer {
	private final LoanStatementRequestDto loanStatementRequestDto = DtoInitializer.initLoanStatementRequest();
	private final EmploymentDto employmentDto = DtoInitializer.initEmploymentDto();
	private final CreditDto creditDto = DtoInitializer.initCreditDto();
	
	public static Client initClient() {
		return Client.builder()
				.clientIdUuid(UUID.randomUUID())
				.lastName(loanStatementRequestDto.getLastName())
				.firstName(loanStatementRequestDto.getFirstName())
				.middleName(loanStatementRequestDto.getMiddleName())
				.birthdate(loanStatementRequestDto.getBirthDate())
				.email(loanStatementRequestDto.getEmail())
				.gender(Gender.MALE)
				.maritalStatus(MaritalStatus.SINGLE)
				.dependentAmount(0)
				.passport(initPassport())
				.employment(initEmployment())
				.accountNumber("18923749187")
				.build();
	}
	
	private static Passport initPassport() {
		return Passport.builder()
				.passportUuid(UUID.randomUUID())
				.series(loanStatementRequestDto.getPassportSeries())
				.number(loanStatementRequestDto.getPassportNumber())
				.issueBranch("ГУ МВД ПО Г. МОСКВА")
				.issueDate(LocalDate.now().minusYears(5).minusMonths(3))
				.build();
	}
	
	private static Employment initEmployment() {
		return Employment.builder()
				.employmentUuid(UUID.randomUUID())
				.status(employmentDto.getEmploymentStatus())
				.employerInn(employmentDto.getEmploymentINN())
				.salary(employmentDto.getSalary())
				.position(employmentDto.getPosition())
				.workExperienceTotal(employmentDto.getWorkExperienceTotal())
				.workExperienceCurrent(employmentDto.getWorkExperienceCurrent())
				.build();
	}
	
	public static Credit initCredit() {
		return Credit.builder()
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
	
	public static Statement initStatement() {
		return Statement.builder()
				.client(initClient())
				.credit(initCredit())
				.creationDate(LocalDateTime.now())
				.appliedOffer(DtoInitializer.initLoanOfferDto())
				.build();
	}
	
	public static Statement initFullStatement() {
		StatementStatusHistory statementStatusHistory1 = StatementStatusHistory.builder()
				.status(ApplicationStatus.PREPARE_DOCUMENTS)
				.time(LocalDateTime.now())
				.changeType(ChangeType.AUTOMATIC)
				.build();
		StatementStatusHistory statementStatusHistory2 = StatementStatusHistory.builder()
				.status(ApplicationStatus.APPROVED)
				.time(LocalDateTime.now())
				.changeType(ChangeType.MANUAL)
				.build();
		
		LinkedList<StatementStatusHistory> statusHistories = new LinkedList<>();
		statusHistories.add(statementStatusHistory1);
		statusHistories.add(statementStatusHistory2);
		
		Statement statement = Statement.builder()
				.client(initClient())
				.credit(initCredit())
				.creationDate(LocalDateTime.now())
				.appliedOffer(DtoInitializer.initLoanOfferDto())
				.status(ApplicationStatus.DOCUMENT_CREATED)
				.creationDate(LocalDateTime.now())
				.signDate(LocalDateTime.now())
				.sessionCode(UUID.randomUUID().toString())
				.pdfFile("Test bytes for testing statement".getBytes())
				.build();
		statement.getStatementStatusHistory().addAll(statusHistories);
		return statement;
	}
}
