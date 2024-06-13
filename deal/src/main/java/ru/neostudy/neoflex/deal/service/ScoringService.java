package ru.neostudy.neoflex.deal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neostudy.neoflex.deal.constants.ApplicationStatus;
import ru.neostudy.neoflex.deal.constants.CreditStatus;
import ru.neostudy.neoflex.deal.dto.CreditDto;
import ru.neostudy.neoflex.deal.dto.FinishingRegistrationRequestDto;
import ru.neostudy.neoflex.deal.dto.ScoringDataDto;
import ru.neostudy.neoflex.deal.entity.Credit;
import ru.neostudy.neoflex.deal.entity.Statement;
import ru.neostudy.neoflex.deal.mapper.CreditMapper;
import ru.neostudy.neoflex.deal.mapper.ScoringDataMapper;
import ru.neostudy.neoflex.deal.requester.CalculatorRequester;

@Service
@RequiredArgsConstructor
public class ScoringService
{
	private final CalculatorRequester calculatorRequester;
	private final ScoringDataMapper scoringDataMapper;
	private final CreditMapper creditMapper;
	private final StatementEntityService statementEntityService;
	
	public void scoreAndSaveCredit(FinishingRegistrationRequestDto finishingRegistrationRequestDto, Statement statement)
	{
		ScoringDataDto scoringDataDto = scoringDataMapper.formScoringDataDto(finishingRegistrationRequestDto, statement);
		CreditDto creditDto = calculatorRequester.requestCalculatedLoanTerms(scoringDataDto);
		Credit credit = creditMapper.dtoToEntity(creditDto);
		
		credit.setCreditStatus(CreditStatus.CALCULATED);
		statement.setCredit(credit);
		statementEntityService.setStatus(statement, ApplicationStatus.CC_APPROVED);
		
		statementEntityService.save(statement);
	}
}
