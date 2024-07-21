package ru.neoflex.neostudy.gateway.controller;

import io.swagger.v3.oas.annotations.Parameter;
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
public class AdminRestController {
	private final RequestService requestService;
	
	@PutMapping("/statement/{statementId}/status")
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
	public ResponseEntity<Statement> getStatement(@PathVariable("statementId") UUID statementId) throws StatementNotFoundException, InternalMicroserviceException {
		Statement statement = requestService.getStatement(statementId);
		return ResponseEntity.status(HttpStatus.OK).body(statement);
	}
	
	@GetMapping("/statement")
	public ResponseEntity<List<Statement>> getAllStatements() throws InternalMicroserviceException {
		List<Statement> statements = requestService.getAllStatements(); 		// TODO сделать пагинацию
		return ResponseEntity.status(HttpStatus.OK).body(statements);
	}
	
}
