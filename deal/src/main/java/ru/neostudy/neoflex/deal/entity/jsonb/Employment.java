package ru.neostudy.neoflex.deal.entity.jsonb;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.neostudy.neoflex.deal.constants.EmploymentStatus;
import ru.neostudy.neoflex.deal.constants.EmploymentPosition;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class Employment implements Serializable
{
	private UUID employmentUuid;
	private EmploymentStatus status;
	private String employerInn;
	private BigDecimal salary;
	private EmploymentPosition position;
	private Integer workExperienceTotal;
	private Integer workExperienceCurrent;
}
