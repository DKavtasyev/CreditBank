package ru.neoflex.neostudy.gateway.controller.annotations;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(
		name = "Редирект",
		description = "Вспомогательный контроллер, позволяет подписывать документы с помощью запроса, отсылаемого из " +
				"электронного письма")
public interface UIControllerInterface {
	
	@GetMapping("/document/{statementId}/sign/code")
	@Operation(
			summary = "Подписание документов на кредит",
			description = """
					Вспомогательный метод, необходим для приёма запроса подписания документов из электронного письма
					клиенту. Принимает GET-запрос и отправляет POST-запрс на эндпойнт /document/{statementId}/sign/code.
					""",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "404", description = "Not found"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			})
	String signPage(@PathVariable("statementId")
						   @Parameter(description = "Идентификатор заявки Statement")
						   String statementId,
						   @RequestParam("code")
						   @Parameter(description = "Подпись документов")
						   String sesCode,
						   Model model);
}
