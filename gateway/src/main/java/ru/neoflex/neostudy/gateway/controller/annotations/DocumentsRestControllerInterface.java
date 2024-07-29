package ru.neoflex.neostudy.gateway.controller.annotations;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.neoflex.neostudy.common.exception.SignatureVerificationFailedException;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;

import java.util.UUID;

@RequestMapping("${app.rest.prefix.document}")
@Tag(
		name = "Документы",
		description = "Оформление документов на кредит")
public interface DocumentsRestControllerInterface {
	
	@PostMapping("/{statementId}")
	@Operation(
			summary = "Запрос на формирование документов и отправку их пользователю.",
			description = """
					Отправляет запрос на формирование документов в MS deal.
					""",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "404", description = "Not found"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			})
	ResponseEntity<Void> createDocuments(@PathVariable("statementId")
												@Parameter(description = "Идентификатор заявки Statement")
												UUID statementId) throws StatementNotFoundException, InternalMicroserviceException;
	
	@PostMapping("/{statementId}/sign")
	@Operation(
			summary = "Запрос на формирование кода ПЭП и на подписание документов пользователем.",
			description = """
					Отправляет запрос на формирование кода ПЭП в MS deal.
					""",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "404", description = "Not found"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			})
	ResponseEntity<Void> signDocuments(@PathVariable("statementId")
											  @Parameter(description = "Идентификатор заявки Statement")
											  UUID statementId) throws StatementNotFoundException, InternalMicroserviceException;
	
	@PostMapping("/{statementId}/sign/code")
	@Operation(
			summary = "Подписание документов пользователем.",
			description = """
					Отправляет запрос, содержащий код ПЭП, в MS deal.
					
					Примечание: в поле statementId необходимо установить id нужной заявки Statement.
					Добавить параметр запроса "code" со значением параметра, равным подписи документа.
					""",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "404", description = "Not found"),
					@ApiResponse(responseCode = "406", description = "Not acceptable"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			})
	ResponseEntity<Void> verifySesCode(@PathVariable("statementId")
											  @Parameter(description = "Идентификатор заявки Statement")
											  UUID statementId,
											  @Parameter(description = "Подпись документа")
											  @RequestParam("code") String code) throws SignatureVerificationFailedException, StatementNotFoundException, InternalMicroserviceException;
}
