package ru.neoflex.neostudy.deal.entity.sign;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;
import ru.neoflex.neostudy.deal.entity.Statement;

import java.io.Serializable;
import java.util.UUID;

@Getter
@ToString
public class SignData implements Serializable {
	@Schema(description = "Подписываемая заявка на кредит")
	private final Statement statement;
	@Schema(description = "Код для подписания")
	private final String token;
	
	public SignData(Statement statement) {
		this.token = UUID.randomUUID().toString();
		this.statement = statement;
	}
}
