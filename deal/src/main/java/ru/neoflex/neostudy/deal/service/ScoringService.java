package ru.neoflex.neostudy.deal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.constants.CreditStatus;
import ru.neoflex.neostudy.common.dto.CreditDto;
import ru.neoflex.neostudy.common.dto.FinishingRegistrationRequestDto;
import ru.neoflex.neostudy.common.dto.ScoringDataDto;
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
	
	public void scoreAndSaveCredit(FinishingRegistrationRequestDto finishingRegistrationRequestDto, Statement statement) {
		ScoringDataDto scoringDataDto = scoringDataMapper.formScoringDataDto(finishingRegistrationRequestDto, statement);
		CreditDto creditDto = calculatorRequester.requestCalculatedLoanTerms(scoringDataDto);
		Credit credit = creditMapper.dtoToEntity(creditDto);
		
		credit.setCreditStatus(CreditStatus.CALCULATED);
		statement.setCredit(credit);
		statementEntityService.setStatus(statement, ApplicationStatus.CC_APPROVED);
		
		statementEntityService.save(statement);
	}
}
