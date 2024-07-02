Микросервис Calculator выполняет следующие функции:
1) осуществляет предварительный расчёт кредитных предложений по заявке от пользователя;
2) осуществляет основной расчёт кредита с указанием дат, суммы платежей, частей платежа, идущих на погашение основного
долга и на погашение процентов.

REST-контроллер CalculatorController
1) Метод ResponseEntity<List<LoanOfferDto>> calculateLoanOffers(LoanStatementRequestDto loanStatementRequest).
Получает на вход заявку на кредит от пользователя.
Осуществляет вызов метода List<LoanOfferDto> preScore(LoanStatementRequestDto loanStatementRequest) сервиса
CalculatorService. Отправляет ответ со статусом 200 и списком предварительно рассчитанных предложений в теле ответа.
2) Метод ResponseEntity<CreditDto> calculateLoanTerms(ScoringDataDto scoringData)
Получает на вход пользовательские данные для расчёта графика платежей.
Осуществляет вызов метода CreditDto score(ScoringDataDto scoringData) сервиса CalculatorService. В случае отказа в
выдаче кредита кидает исключение LoanRefusalException. Отправляет ответ со статусом 200 и графиком платежей в теле ответа.
3) Метод ResponseEntity<String> refuseLoan(). Служит перехватчиком исключения LoanRefusalException. Отправляет ответ со
статусом 400.

Сервис CalculatorService
1) Метод void init().
Сохраняет параметры расчёта кредита в переменные типа BigDecimal сервиса из класса конфигурации RateConfig.
2) Метод List<LoanOfferDto> preScore(LoanStatementRequestDto loanStatementRequest). Производит расчёт четырёх предложений
кредита. В цикле для i равного от 0 до 3 по значению i перебирает все комбинации значений параметров "Зарплатный клиент",
"Страховка активна" . В соотвествии с данными параметрами делает поправки кредитной ставки. Вызывает метод
BigDecimal calculate(BigDecimal amount, Integer term, BigDecimal rate) сервиса MonthlyPaymentCalculatorService. По
полученным данным формирует одно предложение LoanOfferDto. Цикл повторяется для нового значения i.
3) Метод CreditDto score(ScoringDataDto scoringData). Производит расчёт графика и суммы платежей. Если страховка активна,
добавляет к сумме займа 5 %. Вызывает метод BigDecimal countPersonalRate(ScoringDataDto scoringData, BigDecimal rate)
сервиса PersonalRateCalculatorService. Вызывает метод BigDecimal calculate(BigDecimal amount, Integer term, BigDecimal rate)
сервиса MonthlyPaymentCalculatorService. Высчитывает дневную процентную ставку dailyRate. Высчитывает параметры первого
платежа firstPaymentDate, firstInterestPayment, firstDebtPayment, firstRemainingDebt, формирует первый элемент графика
платежей firstPaymentScheduleElement, добавляет его в ArrayList графиков платежей scheduleOfPayments. Вызывает
рекурсивную функцию void countPayment(PaymentScheduleElementDto previousScheduleElement, List<PaymentScheduleElementDto>
scheduleOfPayments, BigDecimal dailyRate) сервиса SchedulePaymentsCalculatorService, которая наполняет график платежей
остальными элементами PaymentScheduleElementDto графика платежей. Из полученных данных формирует и возвращает CreditDto.

Сервис MonthlyPaymentCalculatorService.
1) Метод BigDecimal calculate(BigDecimal amount, Integer term, BigDecimal rate). Высчитывает ежемесячный платёж для
указанных параметров кредита с точностью до 16 знаков после запятой, с округлением до ближайшего целого.

Сервис PersonalRateCalculatorService.
Хранит все системные параметры кредита. При создании бина переводит строковые значения параметров из классов
конфигурации в значения типа BigDecimal.
1) Метод PersonalRateCalculatorService(RefusalConfig refusalConfig, RateConfig rateConfig). Высчитывает персональную
процентную ставку. Сначала высчитывает возраст клиента, высчитывает булевые значения inappropriateWorkExperience,
inappropriateAge, inappropriateAmount, isUnemployed. Если хотя-бы одно из перечисленных значений равно true, тогда
кидает исключение LoanRefusalException. Далее, в зависимости от данных от пользователя, корректирует ставку в
соответствии с системными параметрами кредита.

Сервис SchedulePaymentsCalculatorService.
1) Метод void countPayment(PaymentScheduleElementDto previousScheduleElement, List<PaymentScheduleElementDto>
scheduleOfPayments, BigDecimal dailyRate). Добавляет рассчитанный на основании предыдущего платежа новый элемент платежа
в ArrayList со всеми платежами по кредиту. Если остаток долга с процентами по кредиту меньше, чем сумма месячного
платежа, то остаток прибавляется к предыдущему платежу (который оказывается последним). Это условие является условием
выхода из рекурсии. Иначе производится расчёт даты, количества дней с момента предыдущего платежа до даты рассчитываемого
платежа, суммы, идущей на погашение процентов по кредиту, суммы, идущей на погашение основного долга по кредиту,
оставшейся суммы долга. По рассчитанным данным формируется PaymentScheduleElementDto, добавляется в список платежей
scheduleOfPayments и передаётся в рекурсивный вызов описываемой функции countPayment в качестве предыдущего платежа.
2) Метод BigDecimal countInterestPayment(BigDecimal remainingDebt, BigDecimal dailyRate, int numberOfDays). Рассчитывает
долю платежа в счёт уплаты процентов по кредиту.