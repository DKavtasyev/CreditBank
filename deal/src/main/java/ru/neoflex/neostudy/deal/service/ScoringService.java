package ru.neoflex.neostudy.deal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.constants.ChangeType;
import ru.neoflex.neostudy.common.constants.CreditStatus;
import ru.neoflex.neostudy.common.constants.Theme;
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

/**
 * Сервис, осуществляющий работу по расчёту и окончанию оформления кредита.
 */
@Service
@RequiredArgsConstructor
public class ScoringService {
	private final CalculatorRequester calculatorRequester;
	private final ScoringDataMapper scoringDataMapper;
	private final CreditMapper creditMapper;
	private final DataService dataService;
	private final KafkaService kafkaService;
	
	/**
	 * Отправляет запрос в МС calculator на расчёт графика и сумм платежей по кредиту. На основе полученной информации
	 * создаёт entity-сущность кредита {@code Credit}, назначает её статус CALCULATED, сохраняет её в entity
	 * {@code Statement}, после чего назначает сущности {@code Statement} статус CC_APPROVED, сохраняет {@code Credit} и
	 * обновляет {@code Statement} в базе данных.
	 * @param finishingRegistrationRequestDto пользовательские данные для завершения оформления кредита.
	 * @param statement объект-entity, содержащий все данные по кредиту.
	 * @throws LoanRefusalException если данные от пользователя не прошли скоринг (ответ от МС calculator пришёл со
	 * статусом 406 Not acceptable).
	 * @throws InternalMicroserviceException если при запросе возникла ошибка или МС calculator недоступен.
	 * @throws InvalidPreApproveException если предварительное одобрение кредита для данной заявки на кредит отсутствует
	 * или недействительно. В этом случае поле {@code appliedOffer} в объекте {@code Statement} будет равно {@code null},
	 * что вызовет исключение {@code NullPointerException} при формировании объекта {@code ScoringDataDto}, которое
	 * будет обёрнуто в InvalidPreApproveException.
	 */
	public void scoreAndSaveCredit(FinishingRegistrationRequestDto finishingRegistrationRequestDto, Statement statement) throws LoanRefusalException, InternalMicroserviceException, InvalidPreApproveException {
		CreditDto creditDto;
		try {
			ScoringDataDto scoringDataDto = scoringDataMapper.formScoringDataDto(finishingRegistrationRequestDto, statement);
			creditDto = calculatorRequester.requestCalculatedCredit(scoringDataDto);
		}
		catch (LoanRefusalException e) {
			dataService.updateStatement(statement, ApplicationStatus.CC_DENIED, ChangeType.AUTOMATIC);
			kafkaService.sendKafkaMessage(statement, Theme.STATEMENT_DENIED, null);
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
