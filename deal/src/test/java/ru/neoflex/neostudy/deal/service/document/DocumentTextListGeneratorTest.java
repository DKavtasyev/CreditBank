package ru.neoflex.neostudy.deal.service.document;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.util.DateFormatter;
import ru.neoflex.neostudy.deal.util.EntityInitializer;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentTextListGeneratorTest {
	
	@Mock
	private DateFormatter dateFormatter;
	
	@InjectMocks
	private DocumentTextListGenerator documentTextListGenerator;
	
	private final Statement statement = EntityInitializer.initFullStatement();
	
	@Nested
	@DisplayName("Тестирование метода DocumentTextListGenerator:formClientText()")
	class TestingFormClientTextMethod {
		@Test
		void formClientText() {
			String expectedText = """
					Данные заёмщика
					Фамилия: Ivanov
					Имя: Ivan
					Отчество: Vasilievich
					Дата рождения: 15 августа 2024 г.
					Электронный адрес: ivanov@mail.ru
					Пол: мужской
					Семейное положение: не женат/не замужем
					Количество иждивенцев: 0
					Паспорт
					Серия: 1234
					Номер: 123456
					Выдан: ГУ МВД ПО Г. МОСКВА
					Дата выдачи: 15 августа 2024 г.
					Занятость: трудящийся
					ИНН: 123456781234
					""";
			when(dateFormatter.printDate(any(LocalDate.class))).thenReturn("15 августа 2024");
			String actualText = documentTextListGenerator.formClientText(statement);
			assertThat(actualText).isEqualTo(expectedText);
		}
	}
	
	@Nested
	@DisplayName("Тестирование метода DocumentTextListGenerator:formCreditText()")
	class TestingFormCreditTextMethod {
		@Test
		void formCreditText() {
			String expectedText = """
					Данные кредита
					Сумма займа: 1000000 руб. 0 коп.
					Срок: 6 месяцев
					Ежемесячный платёж: 172548 руб. 37 коп.
					Процентная ставка: 12.00 %
					Полная стоимость кредита: 1035417 руб. 88 коп.
					Страховка включена: нет
					Зарплатный клиент: нет
					Платёж № 1
					Дата платежа: 15 августа 2024 г.
					Сумма платежа: 172548 руб. 37 коп.
					Платёж № 2
					Дата платежа: 15 августа 2024 г.
					Сумма платежа: 172548 руб. 37 коп.
					Платёж № 3
					Дата платежа: 15 августа 2024 г.
					Сумма платежа: 172548 руб. 37 коп.
					Платёж № 4
					Дата платежа: 15 августа 2024 г.
					Сумма платежа: 172548 руб. 37 коп.
					Платёж № 5
					Дата платежа: 15 августа 2024 г.
					Сумма платежа: 172548 руб. 37 коп.
					Платёж № 6
					Дата платежа: 15 августа 2024 г.
					Сумма платежа: 172561 руб. 45 коп.
					""";
			when(dateFormatter.printDate(any(LocalDate.class))).thenReturn("15 августа 2024");
			String actualText = documentTextListGenerator.formCreditText(statement);
			assertThat(actualText).isEqualTo(expectedText);
		}
	}
}