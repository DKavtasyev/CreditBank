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
		return new Client()
				.setClientIdUuid(UUID.randomUUID())
				.setLastName(loanStatementRequestDto.getLastName())
				.setFirstName(loanStatementRequestDto.getFirstName())
				.setMiddleName(loanStatementRequestDto.getMiddleName())
				.setBirthdate(loanStatementRequestDto.getBirthDate())
				.setEmail(loanStatementRequestDto.getEmail())
				.setGender(Gender.MALE)
				.setMaritalStatus(MaritalStatus.SINGLE)
				.setDependentAmount(0)
				.setPassport(initPassport())
				.setEmployment(initEmployment())
				.setAccountNumber("18923749187");
	}
	
	private static Passport initPassport() {
		return new Passport()
				.setPassportUuid(UUID.randomUUID())
				.setSeries(loanStatementRequestDto.getPassportSeries())
				.setNumber(loanStatementRequestDto.getPassportNumber())
				.setIssueBranch("ГУ МВД ПО Г. МОСКВА")
				.setIssueDate(LocalDate.now().minusYears(5).minusMonths(3));
	}
	
	private static Employment initEmployment() {
		return new Employment()
				.setEmploymentUuid(UUID.randomUUID())
				.setStatus(employmentDto.getEmploymentStatus())
				.setEmployerInn(employmentDto.getEmploymentINN())
				.setSalary(employmentDto.getSalary())
				.setPosition(employmentDto.getPosition())
				.setWorkExperienceTotal(employmentDto.getWorkExperienceTotal())
				.setWorkExperienceCurrent(employmentDto.getWorkExperienceCurrent());
	}
	
	public static Credit initCredit() {
		return new Credit()
				.setAmount(creditDto.getAmount())
				.setTerm(creditDto.getTerm())
				.setMonthlyPayment(creditDto.getMonthlyPayment())
				.setRate(creditDto.getRate())
				.setPsk(creditDto.getPsk())
				.setPaymentSchedule(creditDto.getPaymentSchedule())
				.setInsuranceEnabled(creditDto.getIsInsuranceEnabled())
				.setSalaryClient(creditDto.getIsSalaryClient());
	}
	
	public static Statement initStatement() {
		return new Statement()
				.setClient(initClient())
				.setCredit(initCredit())
				.setCreationDate(LocalDateTime.now())
				.setAppliedOffer(DtoInitializer.initLoanOfferDto());
	}
	
	public static Statement initFullStatement() {
		StatementStatusHistory statementStatusHistory1 = new StatementStatusHistory()
				.setStatus(ApplicationStatus.PREPARE_DOCUMENTS)
				.setTime(LocalDateTime.now())
				.setChangeType(ChangeType.AUTOMATIC);
		StatementStatusHistory statementStatusHistory2 = new StatementStatusHistory()
				.setStatus(ApplicationStatus.APPROVED)
				.setTime(LocalDateTime.now())
				.setChangeType(ChangeType.MANUAL);
		
		LinkedList<StatementStatusHistory> statusHistories = new LinkedList<>();
		statusHistories.add(statementStatusHistory1);
		statusHistories.add(statementStatusHistory2);
		
		return new Statement()
				.setClient(initClient())
				.setCredit(initCredit())
				.setCreationDate(LocalDateTime.now())
				.setAppliedOffer(DtoInitializer.initLoanOfferDto())
				.setStatus(ApplicationStatus.DOCUMENT_CREATED)
				.setCreationDate(LocalDateTime.now())
				.setSignDate(LocalDateTime.now())
				.setSessionCode(UUID.randomUUID().toString())
				.setStatementStatusHistory(statusHistories)
				.setPdfFile("Test bytes for testing statement".getBytes());
	}
}
