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
import ru.neoflex.neostudy.deal.entity.Statement;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class PdfDocumentCreator implements DocumentCreator {
	private final DocumentTextGenerator documentTextService;
	
	@Value("${app.font.path}")
	private String fontPath;
	
	@Override
	public byte[] createDocument(Statement statement) throws InternalMicroserviceException {
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
		
		
		
	}
}
