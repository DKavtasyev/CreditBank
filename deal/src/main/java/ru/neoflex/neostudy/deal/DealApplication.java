package ru.neoflex.neostudy.deal;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.service.DataService;
import ru.neoflex.neostudy.deal.service.signature.DigitalSignatureUtil;

import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

@Log4j2
@SpringBootApplication
public class DealApplication {
	
	public static void main(String[] args) throws StatementNotFoundException, IOException, InterruptedException {
		ConfigurableApplicationContext context = SpringApplication.run(DealApplication.class, args);
		DataService service = context.getBean(DataService.class);
		DigitalSignatureUtil digitalSignatureUtil = context.getBean(DigitalSignatureUtil.class);
		Thread.sleep(30000L);
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			 BufferedOutputStream writerCheck = new BufferedOutputStream(new FileOutputStream("file_check.pdf"), 5000)) {
			while (true) {
				System.out.print("Введите statementId: ");
				String statementId = reader.readLine();
				if (statementId.equalsIgnoreCase("exit")){
					break;
				}
				Statement statement = service.findStatement(UUID.fromString(statementId));
				
				byte[] documentAsBytes = statement.getPdfFile();
				String signature = statement.getSessionCode();
				
				System.out.print("Введите public key: ");
				String publicKeyAsString = reader.readLine();
				
				byte[] publicKeyAsBytes = Base64.getDecoder().decode(publicKeyAsString);
				X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyAsBytes);
				KeyFactory keyFactory = KeyFactory.getInstance("RSA");
				PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
				log.warn("public key received: {}", Arrays.toString(publicKey.getEncoded()));
				writerCheck.write(statement.getPdfFile());
				
				boolean isValid = digitalSignatureUtil.verifySignature(documentAsBytes, signature, publicKey);
				System.out.println(isValid ? "Документ оригинальный!" : "Документ подделан!");
			}
		}
		catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException | InvalidKeySpecException e) {
			throw new RuntimeException(e);
		}
	}
}