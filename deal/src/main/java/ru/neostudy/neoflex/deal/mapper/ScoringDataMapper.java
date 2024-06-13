package ru.neostudy.neoflex.deal.mapper;

import org.springframework.stereotype.Component;
import ru.neostudy.neoflex.deal.dto.EmploymentDto;
import ru.neostudy.neoflex.deal.dto.FinishingRegistrationRequestDto;
import ru.neostudy.neoflex.deal.dto.LoanOfferDto;
import ru.neostudy.neoflex.deal.dto.ScoringDataDto;
import ru.neostudy.neoflex.deal.entity.Client;
import ru.neostudy.neoflex.deal.entity.Statement;
import ru.neostudy.neoflex.deal.entity.jsonb.Employment;
import ru.neostudy.neoflex.deal.entity.jsonb.Passport;

import java.util.UUID;

@Component
public class ScoringDataMapper
{
	
	public ScoringDataDto formScoringDataDto(FinishingRegistrationRequestDto finishingRegistrationRequestDto, Statement statement)
	{
		
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
