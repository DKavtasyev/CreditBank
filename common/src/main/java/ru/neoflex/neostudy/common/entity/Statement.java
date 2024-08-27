package ru.neoflex.neostudy.common.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.entity.jsonb.StatementStatusHistory;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Objects;
import java.util.UUID;

import static ru.neoflex.neostudy.common.constants.DateTimeFormat.DATETIME_PATTERN;

/**
 * Entity class must match the same class in MS deal except hibernate/jpa annotations.
 */
@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Statement {
	private UUID statementId;
	private Client client;
	private Credit credit;
	private ApplicationStatus status;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATETIME_PATTERN)
	private LocalDateTime creationDate;
	private LoanOfferDto appliedOffer;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATETIME_PATTERN)
	private LocalDateTime signDate;
	private String sessionCode;
	private final LinkedList<StatementStatusHistory> statementStatusHistory = new LinkedList<>();
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Statement statement)) {
			return false;
		}
		return Objects.equals(client, statement.client)
				&& Objects.equals(credit, statement.credit)
				&& status == statement.status
				&& Objects.equals(creationDate, statement.creationDate)
				&& Objects.equals(appliedOffer, statement.appliedOffer)
				&& Objects.equals(signDate, statement.signDate)
				&& Objects.equals(sessionCode, statement.sessionCode)
				&& Objects.equals(statementStatusHistory, statement.statementStatusHistory);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(client, credit, status, creationDate, appliedOffer, signDate, sessionCode, statementStatusHistory);
	}
}
