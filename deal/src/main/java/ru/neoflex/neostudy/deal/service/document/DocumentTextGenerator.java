package ru.neoflex.neostudy.deal.service.document;

import ru.neoflex.neostudy.deal.entity.Statement;

public interface DocumentTextGenerator {
	String formClientText(Statement statement);
	String formCreditText(Statement statement);
}
