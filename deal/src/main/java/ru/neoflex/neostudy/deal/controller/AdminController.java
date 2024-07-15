package ru.neoflex.neostudy.deal.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
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
	public ResponseEntity<Void> updateStatementStatus(@PathVariable("statementId") UUID statementId,
													  @RequestBody ApplicationStatus status) throws StatementNotFoundException {
		Statement statement = dataService.findStatement(statementId);
		dataService.updateStatement(statement, status);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
