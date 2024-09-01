package ru.neoflex.neostudy.deal.service.signature;

import ru.neoflex.neostudy.common.exception.SignatureVerificationFailedException;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.DocumentSignatureException;
import ru.neoflex.neostudy.deal.entity.Statement;

/**
 * Интерфейс, являющийся общим и задающий контракт для сервисов по работе с подписями.
 */
public interface SignatureService {
	/**
	 * Создаёт и возвращает подпись в виде строки String.
	 * @return {@code String} с подписью.
	 * @throws InternalMicroserviceException выбрасывается в случае использования неподдерживаемого алгоритма
	 * кодирования.
	 */
	String createSignature() throws InternalMicroserviceException;
	
	/**
	 * Подписывает документ, сохраняет полученную подпись в объект заявки по кредиту {@code Statement}.
	 * @param statement объект-entity, содержащий все данные по кредиту.
	 * @param signature подпись, с помощью которой будет подписан документ.
	 * @throws DocumentSignatureException если документ для текущей заявки ранее не был создан.
	 * @throws InternalMicroserviceException выкидывается в случаях ошибок при создании подписи.
	 */
	void signDocument(Statement statement, String signature) throws InternalMicroserviceException, DocumentSignatureException;
	
	/**
	 * Проверяет на подлинность документ с помощью переданной в метод подписи.
	 * @param statement объект-entity, содержащий в себе проверяемый документ.
	 * @param signature подпись для проверяемого документа, с помощью которой осуществляется проверка документа на
	 * подлинность.
	 * @throws SignatureVerificationFailedException если проверка на подлинность показала, что документ не является
	 * подлинным или подпись для проверки не была передана.
	 * @throws InternalMicroserviceException выкидывается в случаях ошибок при проверке подписи.
	 * @throws DocumentSignatureException если не выполнены условия для проверки подписи:
	 * <ul>
	 *     <li>Документ ранее не был создан.</li>
	 *     <li>Документ ранее не был подписан.</li>
	 *     <li>Подпись для проверки не передана.</li>
	 * </ul>
	 */
	void verifySignature(Statement statement, String signature) throws SignatureVerificationFailedException, InternalMicroserviceException, DocumentSignatureException;
}
