package ru.neoflex.neostudy.gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
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
	public ResponseEntity<Void> createDocuments(UUID statementId) throws StatementNotFoundException, InternalMicroserviceException {
		requestService.createDocuments(statementId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@Override
	public ResponseEntity<Void> signDocuments(UUID statementId) throws StatementNotFoundException, InternalMicroserviceException {
		requestService.signDocuments(statementId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@Override
	public ResponseEntity<Void> verifySesCode(UUID statementId, String code) throws SignatureVerificationFailedException, StatementNotFoundException, InternalMicroserviceException {
		requestService.verifySesCode(statementId, code); 			// TODO уточнить по поводу подписи и изменить описание в swagger
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
