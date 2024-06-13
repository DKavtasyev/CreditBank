package ru.neostudy.neoflex.deal.entity.jsonb;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.neostudy.neoflex.deal.constants.ApplicationStatus;
import ru.neostudy.neoflex.deal.constants.ChangeType;

import java.io.Serializable;
import java.time.LocalDateTime;

import static ru.neostudy.neoflex.deal.constants.DateTimeFormat.DATETIME_PATTERN;

@Data
@Accessors(chain = true)
public class StatusHistory implements Serializable
{
	private ApplicationStatus status;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATETIME_PATTERN)
	private LocalDateTime time;
	private ChangeType changeType;
}
