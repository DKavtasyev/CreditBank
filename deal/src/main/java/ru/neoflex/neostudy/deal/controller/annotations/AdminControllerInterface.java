package ru.neoflex.neostudy.deal.controller.annotations;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.deal.entity.Statement;

import java.util.List;
import java.util.UUID;

@RequestMapping("${app.rest.admin.prefix}")
@Tag(
		name = "Администратор",
		description = "Администрирование заявок на кредит")
public interface AdminControllerInterface
{
	
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
	ResponseEntity<Void> updateStatementStatus(@PathVariable("statementId")
											   @Parameter(description = "Идентификатор заявки Statement")
											   UUID statementId,
											   @RequestBody
											   @Parameter(description = "Устанавливаемый статус заявки")
											   ApplicationStatus status) throws StatementNotFoundException;
	
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
	ResponseEntity<Statement> getStatement(@PathVariable("statementId") UUID statementId) throws StatementNotFoundException;
	
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
	ResponseEntity<List<Statement>> getAllStatements(@RequestParam(value = "page", required = false) Integer page);
}
