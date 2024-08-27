package ru.neoflex.neostudy.deal.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.constants.ChangeType;
import ru.neoflex.neostudy.common.exception.*;
import ru.neoflex.neostudy.deal.controller.annotations.DocumentsControllerInterface;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.service.DataService;
import ru.neoflex.neostudy.deal.service.document.DocumentCreator;
import ru.neoflex.neostudy.deal.service.kafka.KafkaService;
import ru.neoflex.neostudy.deal.service.signature.SignatureService;

import java.util.Base64;
import java.util.UUID;

@Log4j2
@RestController
@RequiredArgsConstructor
public class DocumentsController implements DocumentsControllerInterface {
	private final DataService dataService;
	private final KafkaService kafkaService;
	private final DocumentCreator pdfDocumentCreator;
	private final SignatureService uuidSignatureService;
	
	@Override
	public ResponseEntity<Void> sendDocuments(UUID statementId) throws StatementNotFoundException, InternalMicroserviceException, UserDocumentException {
		Statement statement = dataService.findStatement(statementId);
		byte[] documentAsBytes = pdfDocumentCreator.createDocument(statement);
		statement.setPdfFile(documentAsBytes);
		dataService.updateStatement(statement, ApplicationStatus.PREPARE_DOCUMENTS, ChangeType.AUTOMATIC);
		String documentAsStringBase64 = Base64.getEncoder().encodeToString(documentAsBytes);
		kafkaService.sendDocumentSigningRequest(statement, documentAsStringBase64);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@Override
	public ResponseEntity<Void> signDocuments(UUID statementId) throws StatementNotFoundException, InternalMicroserviceException, DocumentSignatureException {
		Statement statement = dataService.findStatement(statementId);
		String keyPair = uuidSignatureService.createSignature();
		uuidSignatureService.signDocument(statement, keyPair);
		dataService.saveStatement(statement);
		kafkaService.sendSignature(statementId, statement.getSessionCode());
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@Override
	public ResponseEntity<Void> verifySesCode(UUID statementId, String signature) throws StatementNotFoundException, SignatureVerificationFailedException, InternalMicroserviceException, DocumentSignatureException {
		Statement statement = dataService.findStatement(statementId);
		uuidSignatureService.verifySignature(statement, signature);
		dataService.updateStatement(statement, ApplicationStatus.DOCUMENT_SIGNED, ChangeType.AUTOMATIC);
		dataService.updateStatement(statement, ApplicationStatus.CREDIT_ISSUED, ChangeType.AUTOMATIC);
		kafkaService.sendCreditIssuedMessage(statement);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
