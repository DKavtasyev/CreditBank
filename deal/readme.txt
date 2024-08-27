Микросервис Deal выполняет следующие функции:
1) принимает от МС Statement заявку на кредит, проверяет, был ли ранее сохранён пользователь с указанными в заявке
серией и номером паспорта, сохраняет его - если не был, и проверяет ФИО - если был. Далее создаёт новый Statement,
связывает его с клиентом, сохраняет Statement в БД. Для созданного заявления (Statement) отправляет запрос на расчёт
предварительных условий кредита, и возвращает ответ с ними в МС Statement.
2) принимает от МС Statement выбранное пользователем предложение кредита, сохраняет его в БД, в ранее созданное заявление
Statement, меняет статус Statement на APPROVED, по окончанию выполнения операций возвращает МС Statement ответ со стату-
сом 200.
3) Принимает от пользователя данные для оформления кредита, запрашивает расчёт графика платежей у МС Calculator, создаёт
сущность Credit, устанавливает ей статус CALCULATED, связывает её с Statement и сохраняет её в БД. По окончанию операций
отвечает статусом 200.

Первая функция реализована в методе ResponseEntity<List<LoanOfferDto>> getLoanOffers(LoanStatementRequestDto loanStatementRequest)
REST-контроллера DealController, в котором вызывается метод Statement writeData(LoanStatementRequestDto loanStatementRequest)
сервиса DataService. В этом методе осуществляется поиск в БД клиента с принятыми серией и номером паспорта с помощью
метода Optional<Client> findClientByPassport(LoanStatementRequestDto loanStatementRequest) сервиса ClientEntityService,
 затем, в методе Client checkAndSaveClient(LoanStatementRequestDto loanStatementRequest, Optional<Client> optionalClient)
сервиса ClientEntityService, либо по полученным данным создаётся новый клиент и сохраняется в БД, либо проверяются ФИО
существующего. В случае несовпадения ФИО полученного и ранее сохранённого клиентов кидается исключение
InvalidPassportDataException("Personal identification information is invalid"). Далее создаётся новый Statement для клиента,
ему устанавливается статус PRE_APPROVAL и он сохраняется в БД и возвращается в контроллер DealController.
В контроллере вызывается метод List<LoanOfferDto> getOffers(LoanStatementRequestDto loanStatementRequest, Statement statement)
сервиса PreScoringService, в котором вызывается метод List<LoanOfferDto> requestLoanOffers(LoanStatementRequestDto loanStatementRequestDto)
сервиса CalculatorRequester, запрашивающий у МС Calculator предварительный расчёт предложений кредита, далее полученные
предложения сортируются от худшего к лучшему и возвращаются в МС Statement для дальнейшего выбора одного из этих
предложений клиентом.

Вторая функция реализована в методе ResponseEntity<Void> applyOffer(@RequestBody LoanOfferDto loanOffer) REST-контроллера
DealController, в котором вызывается метод void updateStatement(LoanOfferDto loanOffer) сервиса DataService, в котором
достаётся из БД Statement по statementId, указанному в выбранном предложении, вызывается метод
void setStatus(Statement statement, ApplicationStatus status) сервиса StatementEntityService, в котором в Statement
устанавливается статус APPROVED и сохраняется запись в истории статусов, далее в Statement сохраняется выбранный
кредитный оффер и обновляется запись Statement в БД. По выполнению операций в МС Statement отправляется ответ со статусом
200. В случае, когда Statement по указанному в выбранном кредитном оффере statementId не найден, кидается исключение
StatementNotFoundException("Statement with id = %s not found"), в МС Statement отсылается ответ со
статусом 404, с сообщением об ошибке. Отклонение кредита клиентом происходит в методе
ResponseEntity<Void> denyOffer(@PathVariable("statementId") UUID statementId) контроллера DealController. В нём
вызывается метод void denyOffer(UUID statementId) сервиса DataService, который находит заявку Statement по переданному
statementId, устанавливает ей статус CLIENT_DENIED и обновляет заявку в базе данных.

Третья функция реализована в методе
ResponseEntity<Void> calculateLoanParameters(@RequestBody FinishingRegistrationRequestDto finishingRegistrationRequestDto)
REST-контроллера DealController. В нём вызывается метод Statement findStatement(UUID statementId) сервиса DataService, в
котором вызывается метод Optional<Statement> findStatement(UUID statementId) сервиса StatementEntityService. При пустом
Optional кидается исключение StatementNotFoundException("Statement with id = %s not found"). Statement возвращается в
контроллер, где передаётся в метод
void scoreAndSaveCredit(FinishingRegistrationRequestDto finishingRegistrationRequestDto, Statement statement) сервиса
ScoringService, в котором формируется ScoringDataDto из FinishingRegistrationRequestDto и Statement с помощью метода
ScoringDataDto formScoringDataDto(FinishingRegistrationRequestDto finishingRegistrationRequestDto, Statement statement)
сервиса ScoringDataMapper, запрашивается расчёт кредита в МС Calculator с помощью метода
CreditDto requestCalculatedLoanTerms(ScoringDataDto scoringDataDto) сервиса CalculatorRequester. Если данные,
отправленные в MS Calculator для расчёта кредита не проходят скоринг, тогда калькулятор присылает сообщение об ошибке
406 Not Acceptable, CalculatorRequester при этом кидает исключение LoanRefusalException, которое перехватывается в
сервисе ScoringService, при этом в заявке Statement устанавливается статус CC_DENIED и заявка обновляется, а исключение
выбрасывается опять. Если данные от пользователя прошли скоринг и MS Calculator вернул рассчитанный кредит в виде
CreditDto, в таком случае формируется entity Credit с помощью метода Credit dtoToEntity(CreditDto creditDto) сервиса
CreditMapper, у Credit устанавливается статус CALCULATED и Credit связывается со своим Statement. У Statement
устанавливается статус CC_APPROVED с помощью метода void setStatus(Statement statement, ApplicationStatus status)
сервиса StatementEntityService, и всё сохраняется в БД.