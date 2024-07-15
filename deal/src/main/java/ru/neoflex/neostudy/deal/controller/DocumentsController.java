package ru.neoflex.neostudy.deal.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.exception.DocumentSigningException;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.entity.sign.SignData;
import ru.neoflex.neostudy.deal.service.DataService;
import ru.neoflex.neostudy.deal.service.KafkaService;
import ru.neoflex.neostudy.deal.service.StatementEntityService;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("${app.rest.document.prefix}")
@RequiredArgsConstructor
public class DocumentsController {
	private final DataService dataService;
	private final KafkaService kafkaService;
	private final StatementEntityService statementEntityService;
	
	@PostMapping("/{statementId}/send")
	public ResponseEntity<Void> requestToSendingDocuments(@PathVariable("statementId") UUID statementId) throws StatementNotFoundException, InternalMicroserviceException {
		Statement statement = dataService.findStatement(statementId);
		dataService.updateStatement(statement, ApplicationStatus.PREPARE_DOCUMENTS);
		kafkaService.sendDocumentSigningRequest(statement);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@PostMapping("/{statementId}/sign")
	public ResponseEntity<Void> requestToSigningDocuments(@PathVariable("statementId") UUID statementId, HttpServletRequest request) throws StatementNotFoundException, InternalMicroserviceException {
		Statement statement = dataService.findStatement(statementId);
		SignData signData = new SignData(statement);
		request.getSession().setAttribute("token", signData);
		statement.setSessionCode(signData.getToken());
		statementEntityService.save(statement);
		kafkaService.sendSignature(signData);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@PostMapping("/{statementId}/code")
	public ResponseEntity<Void> signDocuments(@PathVariable("statementId") UUID statementId,
							  @SessionAttribute(value = "token", required = false) SignData signData,
							  SessionStatus sessionStatus,
							  HttpSession session) throws StatementNotFoundException, DocumentSigningException, InternalMicroserviceException {
		if (signData == null) {
			throw new DocumentSigningException("Missing session code");
		}
		Statement statement = dataService.findStatement(statementId);
		if (signData.getToken().equals(statement.getSessionCode())) {
			statement.setSignDate(LocalDateTime.now());
			dataService.updateStatement(statement, ApplicationStatus.DOCUMENT_SIGNED);
			dataService.updateStatement(statement, ApplicationStatus.CREDIT_ISSUED);
			kafkaService.sendCreditIssuedMessage(statement);
			session.invalidate();
			sessionStatus.setComplete();
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		throw new DocumentSigningException("Wrong session code");
	}
}
