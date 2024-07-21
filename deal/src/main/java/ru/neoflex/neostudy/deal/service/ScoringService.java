package ru.neoflex.neostudy.deal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.constants.ChangeType;
import ru.neoflex.neostudy.common.constants.CreditStatus;
import ru.neoflex.neostudy.common.dto.CreditDto;
import ru.neoflex.neostudy.common.dto.FinishingRegistrationRequestDto;
import ru.neoflex.neostudy.common.dto.ScoringDataDto;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.InvalidPreApproveException;
import ru.neoflex.neostudy.common.exception.LoanRefusalException;
import ru.neoflex.neostudy.deal.entity.Credit;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.mapper.CreditMapper;
import ru.neoflex.neostudy.deal.mapper.ScoringDataMapper;
import ru.neoflex.neostudy.deal.requester.CalculatorRequester;
import ru.neoflex.neostudy.deal.service.kafka.KafkaService;

@Service
@RequiredArgsConstructor
public class ScoringService {
	private final CalculatorRequester calculatorRequester;
	private final ScoringDataMapper scoringDataMapper;
	private final CreditMapper creditMapper;
	private final DataService dataService;
	private final KafkaService kafkaService;
	
	public void scoreAndSaveCredit(FinishingRegistrationRequestDto finishingRegistrationRequestDto, Statement statement) throws LoanRefusalException, InternalMicroserviceException, InvalidPreApproveException {
		CreditDto creditDto;
		try {
			ScoringDataDto scoringDataDto = scoringDataMapper.formScoringDataDto(finishingRegistrationRequestDto, statement);
			creditDto = calculatorRequester.requestCalculatedLoanTerms(scoringDataDto);
		}
		catch (LoanRefusalException e) {
			dataService.updateStatement(statement, ApplicationStatus.CC_DENIED, ChangeType.AUTOMATIC);
			kafkaService.sendDenial(statement, "Вам отказано в получении кредита");
			throw e;
		}
		catch (NullPointerException e) {
			throw new InvalidPreApproveException("Invalid pre-approval of the loan", e);
		}
		Credit credit = creditMapper.dtoToEntity(creditDto);
		credit.setCreditStatus(CreditStatus.CALCULATED);
		statement.setCredit(credit);
		dataService.updateStatement(statement, ApplicationStatus.CC_APPROVED, ChangeType.AUTOMATIC);
	}
}
