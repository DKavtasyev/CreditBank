package ru.neoflex.neostudy.common.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.neoflex.neostudy.common.constants.Gender;
import ru.neoflex.neostudy.common.constants.MaritalStatus;
import ru.neoflex.neostudy.common.entity.jsonb.Employment;
import ru.neoflex.neostudy.common.entity.jsonb.Passport;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import static ru.neoflex.neostudy.common.constants.DateTimeFormat.DATE_PATTERN;

/**
 * Entity class must match the same class in MS deal except hibernate/jpa annotations.
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {
	private UUID clientIdUuid;
	private String lastName;
	private String firstName;
	private String middleName;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
	private LocalDate birthdate;
	private String email;
	private Gender gender;
	private MaritalStatus maritalStatus;
	private Integer dependentAmount;
	private Passport passport;
	private Employment employment;
	private String accountNumber;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Client client)) {
			return false;
		}
		return Objects.equals(lastName, client.lastName)
				&& Objects.equals(firstName, client.firstName)
				&& Objects.equals(middleName, client.middleName)
				&& Objects.equals(birthdate, client.birthdate)
				&& Objects.equals(email, client.email)
				&& gender == client.gender
				&& maritalStatus == client.maritalStatus
				&& Objects.equals(dependentAmount, client.dependentAmount)
				&& Objects.equals(passport, client.passport)
				&& Objects.equals(employment, client.employment)
				&& Objects.equals(accountNumber, client.accountNumber);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(lastName, firstName, middleName, birthdate, email, gender, maritalStatus, dependentAmount, passport, employment, accountNumber);
	}
}
