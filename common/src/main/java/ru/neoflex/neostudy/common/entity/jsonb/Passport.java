package ru.neoflex.neostudy.common.entity.jsonb;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import static ru.neoflex.neostudy.common.constants.DateTimeFormat.DATE_PATTERN;

/**
 * Entity class must match the same class in MS deal one-to-one.
 */
@Data
@Accessors(chain = true)
public class Passport implements Serializable {
	private UUID passportUuid;
	private String series;
	private String number;
	private String issueBranch;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
	private LocalDate issueDate;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Passport passport)) {
			return false;
		}
		return Objects.equals(series, passport.series)
				&& Objects.equals(number, passport.number)
				&& Objects.equals(issueBranch, passport.issueBranch)
				&& Objects.equals(issueDate, passport.issueDate);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(series, number, issueBranch, issueDate);
	}
}
