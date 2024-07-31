package ru.neoflex.neostudy.gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.neostudy.common.exception.DocumentSignatureException;
import ru.neoflex.neostudy.common.exception.SignatureVerificationFailedException;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.gateway.controller.annotations.DocumentsRestControllerInterface;
import ru.neoflex.neostudy.gateway.service.RequestService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DocumentsRestController implements DocumentsRestControllerInterface {
	private final RequestService requestService;
	
	@Override
	public ResponseEntity<Void> createDocuments(UUID statementId) throws StatementNotFoundException, InternalMicroserviceException, DocumentSignatureException {
		requestService.createDocuments(statementId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@Override
	public ResponseEntity<Void> signDocuments(UUID statementId) throws StatementNotFoundException, InternalMicroserviceException, DocumentSignatureException {
		requestService.signDocuments(statementId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@Override
	public ResponseEntity<Void> verifySesCode(UUID statementId, String code) throws SignatureVerificationFailedException, StatementNotFoundException, InternalMicroserviceException, DocumentSignatureException {
		requestService.verifySesCode(statementId, code);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
