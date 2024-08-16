package ru.neoflex.neostudy.deal.mapper;

import org.springframework.stereotype.Component;
import ru.neoflex.neostudy.common.dto.EmploymentDto;
import ru.neoflex.neostudy.common.dto.FinishingRegistrationRequestDto;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.ScoringDataDto;
import ru.neoflex.neostudy.deal.entity.Client;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.entity.jsonb.Employment;
import ru.neoflex.neostudy.deal.entity.jsonb.Passport;

import java.util.UUID;

/**
 * Класс, использующийся для сбора данных и создания объекта DTO с данными от клиента для оформления кредита.
 */
@Component
public class ScoringDataMapper {
	
	public ScoringDataDto formScoringDataDto(FinishingRegistrationRequestDto finishingRegistrationRequestDto, Statement statement) {
		
		EmploymentDto employmentDto = finishingRegistrationRequestDto.getEmployment();
		Client client = statement.getClient();
		LoanOfferDto appliedOffer = statement.getAppliedOffer();
		Passport passport = client.getPassport();
		
		Employment employment = new Employment()
				.setEmploymentUuid(UUID.randomUUID())
				.setStatus(employmentDto.getEmploymentStatus())
				.setEmployerInn(employmentDto.getEmploymentINN())
				.setSalary(employmentDto.getSalary())
				.setPosition(employmentDto.getPosition())
				.setWorkExperienceTotal(employmentDto.getWorkExperienceTotal())
				.setWorkExperienceCurrent(employmentDto.getWorkExperienceCurrent());
		
		client.setGender(finishingRegistrationRequestDto.getGender())
				.setMaritalStatus(finishingRegistrationRequestDto.getMaritalStatus())
				.setDependentAmount(finishingRegistrationRequestDto.getDependentAmount())
				.setEmployment(employment)
				.setAccountNumber(finishingRegistrationRequestDto.getAccountNumber());
		
		
		passport.setIssueDate(finishingRegistrationRequestDto.getPassportIssueDate());
		passport.setIssueBranch(finishingRegistrationRequestDto.getPassportIssueBranch());
		
		return ScoringDataDto.builder()
				.amount(appliedOffer.getRequestedAmount())
				.term(appliedOffer.getTerm())
				.firstName(client.getFirstName())
				.lastName(client.getLastName())
				.middleName(client.getMiddleName())
				.gender(finishingRegistrationRequestDto.getGender())
				.birthdate(client.getBirthdate())
				.passportSeries(passport.getSeries())
				.passportNumber(passport.getNumber())
				.passportIssueDate(finishingRegistrationRequestDto.getPassportIssueDate())
				.passportIssueBranch(finishingRegistrationRequestDto.getPassportIssueBranch())
				.maritalStatus(finishingRegistrationRequestDto.getMaritalStatus())
				.dependentAmount(finishingRegistrationRequestDto.getDependentAmount())
				.employment(employmentDto)
				.accountNumber(finishingRegistrationRequestDto.getAccountNumber())
				.isInsuranceEnabled(appliedOffer.getIsInsuranceEnabled())
				.isSalaryClient(appliedOffer.getIsSalaryClient())
				.build();
	}
}
