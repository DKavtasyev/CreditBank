package ru.neoflex.neostudy.gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.entity.Statement;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.gateway.controller.annotations.AdminRestControllerInterface;
import ru.neoflex.neostudy.gateway.service.RequestService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AdminRestController implements AdminRestControllerInterface {
	private final RequestService requestService;
	
	@Override
	public ResponseEntity<Void> updateStatementStatus(UUID statementId, ApplicationStatus status) throws StatementNotFoundException, InternalMicroserviceException {
		requestService.updateStatementStatus(statementId, status);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@Override
	public ResponseEntity<Statement> getStatement(UUID statementId) throws StatementNotFoundException, InternalMicroserviceException {
		Statement statement = requestService.getStatement(statementId);
		return ResponseEntity.status(HttpStatus.OK).body(statement);
	}
	
	@Override
	public ResponseEntity<List<Statement>> getAllStatements(Integer page) throws InternalMicroserviceException {
		List<Statement> statements = requestService.getAllStatements(page);
		return ResponseEntity.status(HttpStatus.OK).body(statements);
	}
}
