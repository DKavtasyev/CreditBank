package ru.neoflex.neostudy.gateway.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${app.rest.prefix.document}")
@Tag(
		name = "Документы",
		description = "Оформление документов на кредит")
public class DocumentsController {
	
	@PostMapping("/{statementId}")
	public ResponseEntity<Void> createDocuments(@PathVariable("statementId") @Parameter(description = "Идентификатор заявки Statement") UUID statementId) {
		return null; // to deal
	}
	
	@PostMapping("/{statementId}/sign")
	public ResponseEntity<Void> signDocuments(@PathVariable("statementId") @Parameter(description = "Идентификатор заявки Statement") UUID statementId, HttpServletRequest request) {
		return null; // to deal
	}
	
	@PostMapping("/{statementId}/sign/code")
	public ResponseEntity<Void> verifySesCode(@PathVariable("statementId") @Parameter(description = "Идентификатор заявки Statement") UUID statementId) {
		
		return null;
	}
}
