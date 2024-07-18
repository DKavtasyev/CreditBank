package ru.neoflex.neostudy.gateway.controller;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${app.rest.prefix.admin}")
public class AdminController {
	
	@PutMapping("/statement/{statementId}/status")
	public ResponseEntity<Void> updateStatementStatus(@PathVariable("statementId")
													  @Parameter(description = "Идентификатор заявки Statement")
													  UUID statementId,
													  @RequestBody
													  @Parameter(description = "Устанавливаемый статус заявки")
													  ApplicationStatus status) {
		return null;
	}
	
	/*@GetMapping("/statement/{statementId}")
	public ResponseEntity<Statement> getStatement(@PathVariable("statementId") UUID statementId) throws StatementNotFoundException {
		return null;
	}
	
	@GetMapping("/statement")
	public ResponseEntity<List<Statement>> getAllStatements() {
		return null;
	}
	*/
	
}
