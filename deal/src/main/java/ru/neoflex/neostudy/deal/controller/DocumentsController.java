package ru.neoflex.neostudy.deal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.constants.ChangeType;
import ru.neoflex.neostudy.common.exception.DocumentSigningException;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.service.DataService;
import ru.neoflex.neostudy.deal.service.document.DocumentCreator;
import ru.neoflex.neostudy.deal.service.kafka.KafkaService;
import ru.neoflex.neostudy.deal.service.signature.SignatureService;

import java.util.Base64;
import java.util.UUID;

@Log4j2
@RestController
@RequestMapping("${app.rest.document.prefix}")
@RequiredArgsConstructor
@Tag(
		name = "Документы",
		description = "Оформление документов на кредит")
public class DocumentsController {
	private final DataService dataService;
	private final KafkaService kafkaService;
	private final DocumentCreator pdfDocumentCreator;
	private final SignatureService signatureService;
	
	@PostMapping("/{statementId}/send")
	@Operation(
			summary = "Запрос на формирование документов и отправку их пользователю. Kafka топик: SEND_DOCUMENTS",
			description = """
					Является запросом на формирование документов, статус кредита меняет на "PREPARE_DOCUMENTS".
					После получения документов микросервисом dossier, он отправляет PUT-запрос на измерение статуса
					кредита на "DOCUMENTS_CREATED". После этого МС dossier отправляет пользователю email с документами
					на кредит.
					""",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "404", description = "Not found"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			})
	public ResponseEntity<Void> sendDocuments(@PathVariable("statementId")
											  @Parameter(description = "Идентификатор заявки Statement")
											  UUID statementId) throws StatementNotFoundException, InternalMicroserviceException {
		Statement statement = dataService.findStatement(statementId);
		byte[] documentAsBytes = pdfDocumentCreator.createDocument(statement);
		statement.setPdfFile(documentAsBytes);
		dataService.updateStatement(statement, ApplicationStatus.PREPARE_DOCUMENTS, ChangeType.AUTOMATIC);
		String documentAsStringBase64 = Base64.getEncoder().encodeToString(documentAsBytes);
		kafkaService.sendDocumentSigningRequest(statement, documentAsStringBase64);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
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
	public ResponseEntity<Void> signDocuments(@PathVariable("statementId")
											  @Parameter(description = "Идентификатор заявки Statement")
											  UUID statementId) throws StatementNotFoundException, InternalMicroserviceException {
		String signature = signatureService.createSignature();
		
		kafkaService.sendSignature(statementId, signature);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
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
	public ResponseEntity<Void> verifySesCode(@PathVariable("statementId")
											  @Parameter(description = "Идентификатор заявки Statement")
											  UUID statementId,
											  @RequestParam("code") String signature) throws StatementNotFoundException, DocumentSigningException, InternalMicroserviceException {
		Statement statement = dataService.findStatement(statementId);
		signatureService.signDocument(statement, signature);
		dataService.updateStatement(statement, ApplicationStatus.DOCUMENT_SIGNED, ChangeType.AUTOMATIC);
		dataService.updateStatement(statement, ApplicationStatus.CREDIT_ISSUED, ChangeType.AUTOMATIC);
		kafkaService.sendCreditIssuedMessage(statement);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
