package ru.neoflex.neostudy.deal.util;

import lombok.experimental.UtilityClass;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
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
}
