package ru.neoflex.neostudy.deal.service.document;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.dto.PaymentScheduleElementDto;
import ru.neoflex.neostudy.deal.entity.Client;
import ru.neoflex.neostudy.deal.entity.Credit;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.entity.jsonb.Employment;
import ru.neoflex.neostudy.deal.entity.jsonb.Passport;
import ru.neoflex.neostudy.deal.util.DateFormatter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class DocumentTextListGenerator implements DocumentTextGenerator {
	private final DateFormatter dateFormatter;
	
	@Override
	public String formClientText(Statement statement) {
		StringBuilder sb = new StringBuilder();
		Client client = statement.getClient();
		Passport passport = client.getPassport();
		Employment employment = client.getEmployment();
		
		return sb.append("Данные заёмщика").append("\n")
				.append("Фамилия: ").append(client.getLastName()).append("\n")
				.append("Имя: ").append(client.getFirstName()).append("\n")
				.append("Отчество: ").append(client.getMiddleName()).append("\n")
				.append("Дата рождения: ").append(dateFormatter.printDate(client.getBirthdate())).append(" г.").append("\n")
				.append("Электронный адрес: ").append(client.getEmail()).append("\n")
				.append("Пол: ").append(client.getGender().getValue()).append("\n")
				.append("Семейное положение: ").append(client.getMaritalStatus().getValue()).append("\n")
				.append("Количество иждивенцев: ").append(client.getDependentAmount()).append("\n")
				.append("Паспорт").append("\n")
				.append("Серия: ").append(passport.getSeries()).append("\n")
				.append("Номер: ").append(passport.getNumber()).append("\n")
				.append("Выдан: ").append(passport.getIssueBranch()).append("\n")
				.append("Дата выдачи: ").append(dateFormatter.printDate(passport.getIssueDate())).append(" г.").append("\n")
				.append("Занятость: ").append(employment.getStatus().getValue()).append("\n")
				.append("ИНН: ").append(employment.getEmployerInn()).append("\n").toString();
	}
	
	@Override
	public String formCreditText(Statement statement) {
		StringBuilder sb = new StringBuilder();
		Credit credit = statement.getCredit();
		
		sb.append("Данные кредита").append("\n")
				.append("Сумма займа: ").append(getRubles(credit.getAmount())).append(" руб. ").append(getPennies(credit.getAmount())).append(" коп.").append("\n")
				.append("Срок: ").append(credit.getTerm()).append(" месяцев").append("\n")
				.append("Ежемесячный платёж: ").append(getRubles(credit.getMonthlyPayment())).append(" руб. ").append(getPennies(credit.getMonthlyPayment())).append(" коп.").append("\n")
				.append("Процентная ставка: ").append(credit.getRate().multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_EVEN)).append(" %").append("\n")
				.append("Полная стоимость кредита: ").append(getRubles(credit.getPsk())).append(" руб. ").append(getPennies(credit.getPsk())).append(" коп.").append("\n")
				.append("Страховка включена: ").append(credit.isInsuranceEnabled() ? "да" : "нет").append("\n")
				.append("Зарплатный клиент: ").append(credit.isSalaryClient() ? "да" : "нет").append("\n");
		
		for (PaymentScheduleElementDto paymentScheduleElement : credit.getPaymentSchedule()) {
			sb.append("Платёж № ").append(paymentScheduleElement.getNumber()).append("\n")
					.append("Дата платежа: ").append(dateFormatter.printDate(paymentScheduleElement.getDate())).append(" г.").append("\n")
					.append("Сумма платежа: ").append(getRubles(paymentScheduleElement.getTotalPayment())).append(" руб. ").append(getPennies(paymentScheduleElement.getTotalPayment())).append(" коп.").append("\n");
		}
		return sb.toString();
	}
	
	private BigDecimal getRubles(BigDecimal amount) {
		return amount.divide(BigDecimal.ONE, 0, RoundingMode.DOWN);
	}
	
	private BigDecimal getPennies(BigDecimal amount) {
		return amount.remainder(BigDecimal.ONE).abs().setScale(2, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.DOWN);
	}
}
