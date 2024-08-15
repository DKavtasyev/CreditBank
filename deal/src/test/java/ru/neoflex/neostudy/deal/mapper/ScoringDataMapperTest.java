package ru.neoflex.neostudy.deal.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.neoflex.neostudy.common.dto.EmploymentDto;
import ru.neoflex.neostudy.common.dto.FinishingRegistrationRequestDto;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.ScoringDataDto;
import ru.neoflex.neostudy.common.util.DtoInitializer;
import ru.neoflex.neostudy.deal.entity.Client;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.entity.jsonb.Passport;
import ru.neoflex.neostudy.deal.util.EntityInitializer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ScoringDataMapperTest {
	private final FinishingRegistrationRequestDto finishingRegistrationRequestDto = DtoInitializer.initFinishingRegistrationRequest();
	private final Statement statement = EntityInitializer.initStatement();
	ScoringDataMapper mapper = new ScoringDataMapper();
	
	@Nested
	@DisplayName("Тестирование метода ScoringDataMapper:formScoringDataDto()")
	class TestingFormScoringDataDtoMethod {
		@Test
		void formScoringDataDto() {
			ScoringDataDto actualScoringDataDto = mapper.formScoringDataDto(finishingRegistrationRequestDto, statement);
			LoanOfferDto expectedLoanOfferDto = statement.getAppliedOffer();
			Client expectedClient = statement.getClient();
			Passport expectedPassport = expectedClient.getPassport();
			EmploymentDto actualEmployment = actualScoringDataDto.getEmployment();
			EmploymentDto expectedEmploymentDto = finishingRegistrationRequestDto.getEmployment();
			assertAll(() -> {
				assertThat(actualScoringDataDto.getAmount()).isEqualByComparingTo(expectedLoanOfferDto.getRequestedAmount());
				assertThat(actualScoringDataDto.getTerm()).isEqualTo(expectedLoanOfferDto.getTerm());
				assertThat(actualScoringDataDto.getFirstName()).isEqualTo(expectedClient.getFirstName());
				assertThat(actualScoringDataDto.getLastName()).isEqualTo(expectedClient.getLastName());
				assertThat(actualScoringDataDto.getMiddleName()).isEqualTo(expectedClient.getMiddleName());
				assertThat(actualScoringDataDto.getGender()).isEqualTo(expectedClient.getGender());
				assertThat(actualScoringDataDto.getBirthdate()).isEqualTo(expectedClient.getBirthdate());
				assertThat(actualScoringDataDto.getPassportSeries()).isEqualTo(expectedPassport.getSeries());
				assertThat(actualScoringDataDto.getPassportNumber()).isEqualTo(expectedPassport.getNumber());
				assertThat(actualScoringDataDto.getPassportIssueDate()).isEqualTo(finishingRegistrationRequestDto.getPassportIssueDate());
				assertThat(actualScoringDataDto.getPassportIssueBranch()).isEqualTo(finishingRegistrationRequestDto.getPassportIssueBranch());
				assertThat(actualScoringDataDto.getMaritalStatus()).isEqualTo(finishingRegistrationRequestDto.getMaritalStatus());
				assertThat(actualScoringDataDto.getDependentAmount()).isEqualTo(finishingRegistrationRequestDto.getDependentAmount());
				assertThat(actualEmployment.getEmploymentStatus()).isEqualTo(expectedEmploymentDto.getEmploymentStatus());
				assertThat(actualEmployment.getEmploymentINN()).isEqualTo(expectedEmploymentDto.getEmploymentINN());
				assertThat(actualEmployment.getSalary()).isEqualByComparingTo(expectedEmploymentDto.getSalary());
				assertThat(actualEmployment.getPosition()).isEqualTo(expectedEmploymentDto.getPosition());
				assertThat(actualEmployment.getWorkExperienceTotal()).isEqualTo(expectedEmploymentDto.getWorkExperienceTotal());
				assertThat(actualEmployment.getWorkExperienceCurrent()).isEqualTo(expectedEmploymentDto.getWorkExperienceCurrent());
				assertThat(actualScoringDataDto.getAccountNumber()).isEqualTo(finishingRegistrationRequestDto.getAccountNumber());
				assertThat(actualScoringDataDto.getIsInsuranceEnabled()).isEqualTo(statement.getAppliedOffer().getIsInsuranceEnabled());
				assertThat(actualScoringDataDto.getIsSalaryClient()).isEqualTo(statement.getAppliedOffer().getIsSalaryClient());
			});
			
		}
	}
	
}