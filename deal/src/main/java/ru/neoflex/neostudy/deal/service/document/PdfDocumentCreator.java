package ru.neoflex.neostudy.deal.service.document;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.UserDocumentException;
import ru.neoflex.neostudy.deal.entity.Statement;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Сервис для работы с .pdf форматом.
 */
@Service
@RequiredArgsConstructor
public class PdfDocumentCreator implements DocumentCreator {
	private final DocumentTextGenerator documentTextService;
	
	@Value("${app.font.path}")
	private String fontPath;
	
	/**
	 * Создаёт документ по кредиту в формате .pdf и возвращает его в виде массива байтов {@code byte[]}. Текстовая часть
	 * документа состоит из двух частей: данные клиента и данные по кредиту. В ней описаны все значимые условия кредита.
	 * @param statement объект-entity, содержащий все данные по кредиту.
	 * @return сформированный документ в виде массива байт.
	 * @throws InternalMicroserviceException если шрифт, используемый в документе повреждён или не найден.
	 * @throws UserDocumentException документ не может быть создан, если не получены все данные для оформления
	 * кредита.
	 */
	@Override
	public byte[] createDocument(Statement statement) throws InternalMicroserviceException, UserDocumentException {
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
			PdfWriter writer = new PdfWriter(byteArrayOutputStream);
			
			PdfDocument pdfDoc = new PdfDocument(writer);
			PdfFont font = PdfFontFactory.createFont(fontPath, "Identity-H", PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED, true);
			Document document = new Document(pdfDoc);
			document.add(new Paragraph(documentTextService.formClientText(statement)).setFont(font).setFontSize(14));
			document.add(new Paragraph(documentTextService.formCreditText(statement)).setFont(font).setFontSize(14));
			document.close();
			
			byte[] pdfDocumentBytes = byteArrayOutputStream.toByteArray();
			statement.setPdfFile(pdfDocumentBytes);
			return pdfDocumentBytes;
		}
		catch (IOException e) {
			throw new InternalMicroserviceException("Creating document error", e);
		}
		catch (NullPointerException e) {
			throw new UserDocumentException("Document can't be created before finishing registration of the credit");
		}
	}
}
