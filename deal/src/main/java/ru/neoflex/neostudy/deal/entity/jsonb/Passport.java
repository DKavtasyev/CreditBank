package ru.neoflex.neostudy.deal.entity.jsonb;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

import static ru.neoflex.neostudy.common.constants.DateTimeFormat.DATE_PATTERN;

@Data
@Accessors(chain = true)
public class Passport implements Serializable {
	private UUID passportUuid;
	private String series;
	private String number;
	private String issueBranch;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
	private LocalDate issueDate;
}
