package ru.neoflex.neostudy.deal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.constants.ChangeType;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.service.DataService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${app.rest.admin.prefix}")
@Tag(
		name = "Администратор",
		description = "Администрирование заявок на кредит")
public class AdminController {
	private final DataService dataService;
	
	@PutMapping("/statement/{statementId}/status")
	@Operation(
			summary = "Устанавливает статус для выбранной заявки",
			description = """
					Устанавливает статус для выбранной заявки и сохраняет её.
					""",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "404", description = "Not found")
			})
	public ResponseEntity<Void> updateStatementStatus(@PathVariable("statementId")
													  @Parameter(description = "Идентификатор заявки Statement")
													  UUID statementId,
													  @RequestBody
													  @Parameter(description = "Устанавливаемый статус заявки")
													  ApplicationStatus status) throws StatementNotFoundException {
		Statement statement = dataService.findStatement(statementId);
		dataService.updateStatement(statement, status, ChangeType.MANUAL);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	@GetMapping("/statement/{statementId}")
	@Operation(
			summary = "Возвращает заявку Statement по её statementId",
			description = """
					Осуществляет поиск заявки Statement по её statementId в базе данных. Возвращает найденную заявку.
					Если заявка не существует, возвращает 404 Not found.
					""",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "404", description = "Not found")
			})
	public ResponseEntity<Statement> getStatement(@PathVariable("statementId") UUID statementId) throws StatementNotFoundException {
		Statement statement = dataService.findStatement(statementId);
		HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(CacheControl.noStore());
		return ResponseEntity.status(HttpStatus.OK).headers(headers).body(statement);
	}
	
	@GetMapping("/statement")
	@Operation(
			summary = "Возвращает список всех найденных заявок Statement",
			description = """
					Считывает и возвращает все заявки Statement, имеющиеся в базе данных. В случае отсутствия заявок в
					БД возвращает 200 OK с пустым массивом в теле ответа.
					""",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success")
			})
	public ResponseEntity<List<Statement>> getAllStatements() {
		List<Statement> statements = dataService.findAllStatements();
		return ResponseEntity.status(HttpStatus.OK).body(statements);
	}
}
