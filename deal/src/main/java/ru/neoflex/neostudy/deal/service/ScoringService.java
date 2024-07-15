package ru.neoflex.neostudy.deal.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.constants.CreditStatus;
import ru.neoflex.neostudy.common.dto.CreditDto;
import ru.neoflex.neostudy.common.dto.FinishingRegistrationRequestDto;
import ru.neoflex.neostudy.common.dto.ScoringDataDto;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.LoanRefusalException;
import ru.neoflex.neostudy.deal.entity.Credit;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.mapper.CreditMapper;
import ru.neoflex.neostudy.deal.mapper.ScoringDataMapper;
import ru.neoflex.neostudy.deal.requester.CalculatorRequester;

@Service
@RequiredArgsConstructor
public class ScoringService {
	private final CalculatorRequester calculatorRequester;
	private final ScoringDataMapper scoringDataMapper;
	private final CreditMapper creditMapper;
	private final StatementEntityService statementEntityService;
	private final KafkaService kafkaService;
	
	public void scoreAndSaveCredit(FinishingRegistrationRequestDto finishingRegistrationRequestDto, Statement statement) throws JsonProcessingException, LoanRefusalException, InternalMicroserviceException {
		ScoringDataDto scoringDataDto = scoringDataMapper.formScoringDataDto(finishingRegistrationRequestDto, statement);
		CreditDto creditDto;
		try {
			creditDto = calculatorRequester.requestCalculatedLoanTerms(scoringDataDto);
		}
		catch (LoanRefusalException e) {
			statementEntityService.setStatus(statement, ApplicationStatus.CC_DENIED);
			statementEntityService.save(statement);
			kafkaService.sendDenial(statement);
			throw e;
		}
		Credit credit = creditMapper.dtoToEntity(creditDto);
		
		credit.setCreditStatus(CreditStatus.CALCULATED);
		statement.setCredit(credit);
		statementEntityService.setStatus(statement, ApplicationStatus.CC_APPROVED);
		
		statementEntityService.save(statement);
	}
}
