package ru.neoflex.neostudy.deal.controller.annotations;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.neoflex.neostudy.common.exception.*;

import java.util.UUID;

@RequestMapping("${app.rest.document.prefix}")
@Tag(
		name = "Документы",
		description = "Оформление документов на кредит")
public interface DocumentsControllerInterface {
	
	@PostMapping("/{statementId}/send")
	@Operation(
			summary = "Запрос на формирование документов и отправку их пользователю. Kafka топик: SEND_DOCUMENTS",
			description = """
					Принимает запрос на формирование документов, статус кредита меняет на "PREPARE_DOCUMENTS".
					Отсылает сообщение с документом в MS dossier, который отправляет пользователю email с документами
					на кредит.
					""",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "404", description = "Not found"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			})
	ResponseEntity<Void> sendDocuments(@PathVariable("statementId")
											  @Parameter(description = "Идентификатор заявки Statement")
											  UUID statementId) throws StatementNotFoundException, InternalMicroserviceException, UserDocumentException;
	
	@PostMapping("/{statementId}/sign")
	@Operation(
			summary = "Запрос на формирование кода ПЭП и на подписание документов пользователем. Kafka топик: SEND_SES",
			description = """
					Формирует код ПЭП, отсылает сообщение в МС dossier для отправки ссылки на подписание пользователю.
					""",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "404", description = "Not found"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			})
	ResponseEntity<Void> signDocuments(@PathVariable("statementId")
											  @Parameter(description = "Идентификатор заявки Statement")
											  UUID statementId) throws StatementNotFoundException, InternalMicroserviceException, SignatureVerificationFailedException, DocumentSignatureException;
	
	@PostMapping("/{statementId}/code")
	@Operation(
			summary = "Подписание документов пользователем. Kafka топик: CREDIT_ISSUED",
			description = """
					Принимает код ПЭП и верифицирует его. В случае успеха меняет статус заявки сначала на
					"DOCUMENTS_SIGNED", затем на "CREDIT_CREATED" Отправляет оповещение в МС dossier о выпущенном
					кредите.
					
					Примечание: в поле statementId необходимо установить id нужной заявки Statement.
					В поле token необходимо установить значение графы ses_code таблицы statement базы данных.
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
											  @RequestParam(value = "code") String signature) throws StatementNotFoundException, SignatureVerificationFailedException, InternalMicroserviceException, DocumentSignatureException;
}
