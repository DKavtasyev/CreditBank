package ru.neoflex.neostudy.deal;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.service.DataService;
import ru.neoflex.neostudy.deal.service.signature.sds.DigitalSignatureUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.UUID;
@SuppressWarnings("all")

@Log4j2
@SpringBootApplication
public class DealApplication {
	
	public static void main(String[] args) throws StatementNotFoundException, IOException, InterruptedException {
		ConfigurableApplicationContext context = SpringApplication.run(DealApplication.class, args);
		DataService service = context.getBean(DataService.class);
		DigitalSignatureUtil digitalSignatureUtil = context.getBean(DigitalSignatureUtil.class);
		Thread.sleep(30000L);
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
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
				
				PublicKey publicKey = digitalSignatureUtil.getPublicKeyFromBase64(publicKeyAsString);
				boolean isValid = digitalSignatureUtil.verifySignature(documentAsBytes, signature, publicKey);
				System.out.println(isValid ? "Документ оригинальный!" : "Документ подделан!");
			}
		}
		catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		catch (InvalidKeySpecException e) {
			throw new RuntimeException(e);
		}
		catch (SignatureException e) {
			throw new RuntimeException(e);
		}
		catch (InvalidKeyException e) {
			throw new RuntimeException(e);
		}
	}
}