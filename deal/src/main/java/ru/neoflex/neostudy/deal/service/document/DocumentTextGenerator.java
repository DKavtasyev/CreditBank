package ru.neoflex.neostudy.deal.service.document;

import ru.neoflex.neostudy.deal.entity.Statement;

/**
 * Интерфейс, задающий контракт для формирования текстового содержимого документа на кредит.
 */
public interface DocumentTextGenerator {
	/**
	 * Возвращает строку с данными о клиенте, которые были взяты из объекта {@code Client}, который вложен в объект типа
	 * {@code Statement}, передающийся в метод в качестве аргумента.
	 * @param statement entity-объект, содержащий все данные по кредиту.
	 * @return данные о клиенте в виде текста в формате {@code String}.
	 * @throws NullPointerException если происходит попытка создания документа для заявки на кредит, оформление которой
	 * не было завершено. При этом поле {@code credit} будет равно {@code null}.
	 */
	String formClientText(Statement statement);
	/**
	 * Возвращает строку с данными о кредите, которые были взяты из объекта {@code Statement}, который передаётся в
	 * метод в качестве аргумента.
	 * @param statement entity-объект, содержащий все данные по кредиту.
	 * @return данные о кредите в виде текста в формате {@code String}.
	 * @throws NullPointerException если происходит попытка создания документа для заявки на кредит, оформление которой
	 * не было завершено. При этом поле {@code credit} будет равно {@code null}.
	 */
	String formCreditText(Statement statement);
}
