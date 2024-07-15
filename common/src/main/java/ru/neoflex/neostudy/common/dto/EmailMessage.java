package ru.neoflex.neostudy.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.neoflex.neostudy.common.constants.Theme;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessage {
	@Schema(description = "Адрес электронной почты")
	private String address;
	@Schema(description = "Тема письма")
	private Theme theme;
	@Schema(description = "Идентификатор заявки на кредит")
	private UUID statementId;
}
