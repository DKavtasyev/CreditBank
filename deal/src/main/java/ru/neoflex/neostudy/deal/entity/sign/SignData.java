package ru.neoflex.neostudy.deal.entity.sign;

import lombok.Getter;
import lombok.ToString;
import ru.neoflex.neostudy.deal.entity.Statement;

import java.io.Serializable;
import java.util.UUID;

@Getter
@ToString
public class SignData implements Serializable {
	private final Statement statement;
	private final String token;
	
	public SignData(Statement statement) {
		this.token = UUID.randomUUID().toString();
		this.statement = statement;
	}
}
