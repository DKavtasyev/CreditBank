package ru.neoflex.neostudy.deal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.constants.ChangeType;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.service.DataService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${app.rest.admin.prefix}")
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
					@ApiResponse(responseCode = "404", description = "Not found"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			})
	public ResponseEntity<Void> updateStatementStatus(@PathVariable("statementId") @Parameter(description = "Идентификатор заявки Statement") UUID statementId,
													  @RequestBody @Parameter(description = "Устанавливаемый статус заявки") ApplicationStatus status) throws StatementNotFoundException {
		Statement statement = dataService.findStatement(statementId);
		dataService.updateStatement(statement, status, ChangeType.MANUAL);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
