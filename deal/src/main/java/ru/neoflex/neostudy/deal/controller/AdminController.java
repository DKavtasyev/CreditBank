package ru.neoflex.neostudy.deal.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.constants.ChangeType;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.deal.controller.annotations.AdminControllerInterface;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.service.DataService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AdminController implements AdminControllerInterface {
	private final DataService dataService;
	
	@Override
	public ResponseEntity<Void> updateStatementStatus(UUID statementId, ApplicationStatus status) throws StatementNotFoundException {
		Statement statement = dataService.findStatement(statementId);
		dataService.updateStatement(statement, status, ChangeType.MANUAL);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	@Override
	public ResponseEntity<Statement> getStatement(UUID statementId) throws StatementNotFoundException {
		Statement statement = dataService.findStatement(statementId);
		HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(CacheControl.noStore());
		return ResponseEntity.status(HttpStatus.OK).headers(headers).body(statement);
	}
	
	@Override
	public ResponseEntity<List<Statement>> getAllStatements(Integer page) {
		List<Statement> statements = dataService.findAllStatements(page);
		return ResponseEntity.status(HttpStatus.OK).body(statements);
	}
}
