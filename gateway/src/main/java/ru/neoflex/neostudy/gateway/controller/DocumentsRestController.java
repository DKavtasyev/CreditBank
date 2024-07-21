package ru.neoflex.neostudy.gateway.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.neoflex.neostudy.common.exception.DocumentSigningException;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.gateway.service.RequestService;

import java.util.UUID;

@RestController
@RequestMapping("${app.rest.prefix.document}")
@RequiredArgsConstructor
@Tag(
		name = "Документы",
		description = "Оформление документов на кредит")
public class DocumentsRestController {
	private final RequestService requestService;
	
	@PostMapping("/{statementId}")
	public ResponseEntity<Void> createDocuments(@PathVariable("statementId")
												@Parameter(description = "Идентификатор заявки Statement")
												UUID statementId) throws StatementNotFoundException, InternalMicroserviceException {
		requestService.createDocuments(statementId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@PostMapping("/{statementId}/sign")
	public ResponseEntity<Void> signDocuments(@PathVariable("statementId")
											  @Parameter(description = "Идентификатор заявки Statement")
											  UUID statementId) throws StatementNotFoundException, InternalMicroserviceException {
		requestService.signDocuments(statementId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@PostMapping("/{statementId}/sign/code")
	public ResponseEntity<Void> verifySesCode(@PathVariable("statementId")
											  @Parameter(description = "Идентификатор заявки Statement")
											  UUID statementId,
											  @RequestParam("code") String code) throws DocumentSigningException, StatementNotFoundException, InternalMicroserviceException {
		requestService.verifySesCode(statementId, code);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
