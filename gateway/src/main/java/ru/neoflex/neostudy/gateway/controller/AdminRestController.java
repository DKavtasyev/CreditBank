package ru.neoflex.neostudy.gateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.entity.Statement;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.gateway.service.RequestService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${app.rest.prefix.admin}")
@Tag(	name = "Администратор",
		description = "Администрирование заявок на кредит")
public class AdminRestController {
	private final RequestService requestService;
	
	@PutMapping("/statement/{statementId}/status")
	@Operation(
			summary = "Устанавливает статус для выбранной заявки",
			description = """
					Перенаправляет запрос в MS deal для установки указанного статуса для выбранной заявки, statementId
					которой должен быть указан в запросе.
					""",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "404", description = "Not found"),
					@ApiResponse(responseCode = "500", description = "Internal server error")})
	public ResponseEntity<Void> updateStatementStatus(@PathVariable("statementId")
													  @Parameter(description = "Идентификатор заявки Statement")
													  UUID statementId,
													  @RequestBody
													  @Parameter(description = "Устанавливаемый статус заявки")
													  ApplicationStatus status) throws StatementNotFoundException, InternalMicroserviceException {
		requestService.updateStatementStatus(statementId, status);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@GetMapping("/statement/{statementId}")
	@Operation(
			summary = "Возвращает заявку Statement по её statementId",
			description = """
					Перенаправляет запрос в MS deal для поиска заявки Statement по её statementId в базе данных.
					Принимает от MS deal найденную заявку. Если заявка не существует, возвращает 404 Not found.
					""",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "404", description = "Not found"),
					@ApiResponse(responseCode = "500", description = "Internal server error")})
	public ResponseEntity<Statement> getStatement(@PathVariable("statementId") UUID statementId) throws StatementNotFoundException, InternalMicroserviceException {
		Statement statement = requestService.getStatement(statementId);
		return ResponseEntity.status(HttpStatus.OK).body(statement);
	}
	
	@GetMapping("/statement")
	@Operation(
			summary = "Возвращает список всех найденных заявок Statement",
			description = """
					Перенаправляет запрос на получение всех заявок Statement в MS deal. Принимает от MS deal результат
					запроса со списком заявок. В случае отсутствия заявок в БД возвращает ответ со статусом 200 ОК и с
					пустым массивом в теле ответа.
					""",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "500", description = "Internal server error")})
	public ResponseEntity<List<Statement>> getAllStatements(@RequestParam(value = "page", required = false) Integer page) throws InternalMicroserviceException {
		List<Statement> statements = requestService.getAllStatements(page);
		return ResponseEntity.status(HttpStatus.OK).body(statements);
	}
	
}
