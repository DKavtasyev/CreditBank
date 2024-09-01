package ru.neoflex.neostudy.deal.entity.jsonb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.neoflex.neostudy.common.constants.EmploymentPosition;
import ru.neoflex.neostudy.common.constants.EmploymentStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Employment implements Serializable {
	private UUID employmentUuid;
	private EmploymentStatus status;
	private String employerInn;
	private BigDecimal salary;
	private EmploymentPosition position;
	private Integer workExperienceTotal;
	private Integer workExperienceCurrent;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Employment that)) {
			return false;
		}
		return status == that.status
				&& Objects.equals(employerInn, that.employerInn)
				&& (Objects.equals(salary, that.salary) || salary.compareTo(that.salary) == 0)
				&& position == that.position
				&& Objects.equals(workExperienceTotal, that.workExperienceTotal)
				&& Objects.equals(workExperienceCurrent, that.workExperienceCurrent);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(status, employerInn, salary, position, workExperienceTotal, workExperienceCurrent);
	}
}
