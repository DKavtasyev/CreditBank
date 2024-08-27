package ru.neoflex.neostudy.common.entity.jsonb;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.constants.ChangeType;

import java.io.Serializable;
import java.time.LocalDateTime;

import static ru.neoflex.neostudy.common.constants.DateTimeFormat.DATETIME_PATTERN;

/**
 * Entity class must match the same class in MS deal one-to-one.
 */
@Data
@Accessors(chain = true)
public class StatementStatusHistory implements Serializable {
	private ApplicationStatus status;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATETIME_PATTERN)
	private LocalDateTime time;
	private ChangeType changeType;
}
