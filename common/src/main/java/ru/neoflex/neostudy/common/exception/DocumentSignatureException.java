package ru.neoflex.neostudy.common.exception;

/**
 * Выбрасывается, когда происходит попытка подписи ещё не созданного документа, либо проверка подписи у неподписанного
 * документа
 */
public class DocumentSignatureException extends UserDocumentException {
	public DocumentSignatureException(String message) {
		super(message);
	}
	
	public DocumentSignatureException(String message, Throwable cause) {
		super(message, cause);
	}
}
