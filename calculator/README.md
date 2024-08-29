[![codecov ms calculator](https://codecov.io/github/DKavtasyev/CreditBank/branch/feature%2Fadd_docker_and_ci/graph/badge.svg?flag=calculator&token=EG63IGUXHV)](https://codecov.io/github/DKavtasyev/CreditBank)
# MS Calculator
Функции:
1. Производит предварительный расчёт кредитных предложений.
2. Производит расчёт кредита с указанием дат, сумм платежей. 


### 1. Предварительный расчёт кредитных предложений
#### 1.1 Сведения о запросе и ответе
<table>
<tbody>
    <tr><td>Путь</td><td>/calculator/offers</td></tr>
    <tr><td>Метод</td><td>POST</td></tr>
    <tr><td>Тело запроса</td><td>LoanStatementRequestDto</td></tr>
    <tr><td>Тело ответа</td><td>List< LoanOfferDto></td></tr>
</tbody>
</table>

Структура LoanStatementRequestDto

<table>
<thead>
    <tr><th>Поле</th><th>Тип</th><th>Описание</th><th>Требования</th></tr>
</thead>
<tbody>
    <tr><td>amount</td><td>BigDecimal</td><td>Запрашиваемая сумма, руб.</td><td>Не менее 30000 руб. </td></tr>
    <tr><td>term</td><td>Integer</td><td>Запрашиваемый срок кредитования, мес.</td><td>Не менее 6 мес.</td></tr>
    <tr><td>firstName</td><td>String</td><td>Имя</td><td>От 2 до 30 символов</td></tr>
    <tr><td>lastName</td><td>String</td><td>Фамилия</td><td>От 2 до 30 символов</td></tr>
    <tr><td>middleName</td><td>String</td><td>Отчество</td><td>От 2 до 30 символов</td></tr>
    <tr><td>email</td><td>String</td><td>Электронный адрес</td><td>Формат электронных адресов</td></tr>
    <tr><td>birthDate</td><td>LocalDate</td><td>Дата рождения</td><td>Возраст не менее 18 лет</td></tr>
    <tr><td>passportSeries</td><td>String</td><td>Серия паспорта</td><td>Должна состоять из четырёх цифр</td></tr>
    <tr><td>passportNumber</td><td>String</td><td>Номер паспорта</td><td>Должен состоять из шести цифр</td></tr>
</tbody>
</table>

Структура LoanOfferDto

<table>
<thead>
    <tr><th>Поле</th><th>Тип</th><th>Описание</th></tr>
</thead>
<tbody>
    <tr><td>statementId</td>        <td>UUID</td>       <td>Идентификатор заявки на кредит</td></tr>
    <tr><td>requestedAmount</td>    <td>BigDecimal</td> <td>Запрашиваемая сумма, руб.</td></tr>
    <tr><td>totalAmount</td>        <td>BigDecimal</td> <td>Полная стоимость кредита, руб.</td></tr>
    <tr><td>term</td>               <td>Integer</td>    <td>Срок кредитования, мес.</td></tr>
    <tr><td>monthlyPayment</td>     <td>BigDecimal</td> <td>Ежемесячный платёж, руб.</td></tr>
    <tr><td>rate</td>               <td>BigDecimal</td> <td>Процентная ставка, ед.</td></tr>
    <tr><td>birthDate</td>          <td>LocalDate</td>  <td>Дата рождения</td></tr>
    <tr><td>passportSeries</td>     <td>String</td>     <td>Серия паспорта</td></tr>
    <tr><td>passportNumber</td>     <td>String</td>     <td>Номер паспорта</td></tr>
</tbody>
</table>

Возможные коды ответа:
- 200 OK с посчитанными кредитными предложениями.
- 500 Internal server error - в случае неудачной валидации входных данных LoanStatementDto.

#### 1.2 Сведения о методике расчёта
Производится расчёт четырёх кредитных предложений для случаев:
<table>
<thead>
    <tr><th>Зарплатный клиент</th><th>Страховка включена</th></tr>
</thead>
<tbody>
    <tr><th>-</th><th>-</th></tr>
    <tr><th>-</th><th>+</th></tr>
    <tr><th>+</th><th>-</th></tr>
    <tr><th>+</th><th>+</th></tr>
</tbody>
</table>
Для каждого случая корректируется процентная ставка. Страховка прибавляет 5 % к общей сумме вклада.
Далее высчитывается ежемесячный платёж по формуле с точностью до 16 знаков после запятой:

<p style="display: flex; align-items: center; justify-content: center;">
  <img src="https://latex.codecogs.com/png.image?\inline&nbsp;\dpi{110}{\color{White}X=S\times K}" title="{\color{White}X=S\times K}" style="margin: 0 auto;"  alt="monthlyPayment"/>(1)
</p>

где X - аннуитетный платёж;\
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;S - общая сумма кредита;\
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;K - коэффициент аннуитета.


Аннуитетный коэффициент рассчитывается по формуле:


<p style="display: flex; align-items: center; justify-content: center;">
  <img src="https://latex.codecogs.com/png.image?\inline \dpi{110}{\color{White}K=P&plus;\frac{P}{(1&plus;P)^{N}-1}}" title="{\color{White}K=P+\frac{P}{(1+P)^{N}-1}}" style="margin: 0 auto;"  alt="monthlyPayment"/>(2)
</p>

где P - процентная ставка за месяц в виде десятичной дроби;\
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;N - срок кредитования в месяцах.

### 2 Расчёт графика платежей по кредиту
#### 2.1 Сведения о запросе и ответе
<table>
<tbody>
    <tr><td>Путь</td><td>/calculator/calc</td></tr>
    <tr><td>Метод</td><td>POST</td></tr>
    <tr><td>Тело запроса</td><td>ScoringDataDto</td></tr>
    <tr><td>Тело ответа</td><td>CreditDto</td></tr>
</tbody>
</table>

Структура ScoringDataDto

<table>
<thead>
    <tr><th>Поле</th><th>Тип</th><th>Описание</th><th>Требования</th></tr>
</thead>
<tbody>
    <tr><td>amount</td> <td>BigDecimal</td> <td>Запрашиваемая сумма</td><td>Не менее 30000 руб. </td></tr>
    <tr><td>term</td> <td>Integer</td> <td>Запрашиваемый срок кредитования</td><td>Не менее 6 мес.</td></tr>
    <tr><td>firstName</td> <td>String</td> <td>Имя</td><td>От 2 до 30 символов</td></tr>
    <tr><td>lastName</td> <td>String</td> <td>Фамилия</td><td>От 2 до 30 символов</td></tr>
    <tr><td>middleName</td> <td>String</td> <td>Отчество</td><td>От 2 до 30 символов</td></tr>
    <tr><td>gender</td> <td>Enum</td> <td>Пол</td><th>-</th></tr>
    <tr><td>birthDate</td> <td>LocalDate</td> <td>Дата рождения</td><td>Возраст не менее 18 лет</td></tr>
    <tr><td>passportSeries</td> <td>String</td> <td>Серия паспорта</td><td>Должна состоять из четырёх цифр</td></tr>
    <tr><td>passportNumber</td> <td>String</td> <td>Номер паспорта</td><td>Должен состоять из шести цифр</td></tr>
    <tr> <td>passportIssueDate</td> <td>LocalDate</td> <td>Дата выдачи паспорта</td> <th>-</th> </tr>
    <tr> <td>passportIssueBranch</td> <td>String</td> <td>Место выдачи паспорта</td> <th>-</th> </tr>
    <tr> <td>maritalStatus</td> <td>Enum</td> <td>Семейное положение</td> <th>-</th> </tr>
    <tr> <td>dependentAmount</td> <td>Integer</td> <td>Число иждивенцев</td> <td> Неотрицательное число</td> </tr>
    <tr> <td>employmentDto</td> <td>EmploymentDto</td> <td>DTO с информацией о работе</td> <th>-</th> </tr>
    <tr> <td>accountNumber</td> <td>String</td> <td>Номер пользователя</td> <td>Должен состоять из цифр</td> </tr>
    <tr> <td>isInsuranceEnabled</td> <td>boolean</td> <td>Страховка включена</td> <th>-</th> </tr>
    <tr> <td>isSalaryClient</td> <td>boolean</td> <td>Зарплатный клиент</td> <th>-</th> </tr>
</tbody>
</table>

Структура CreditDto

<table>
<thead>
    <tr><th>Поле</th><th>Тип</th><th>Описание</th></tr>
</thead>
<tbody>
    <tr><td>amount</td> <td>BigDecimal</td> <td>Запрашиваемая сумма</td></tr>
    <tr><td>term</td> <td>Integer</td> <td>Запрашиваемый срок кредитования</td></tr>
    <tr> <td>monthlyPayment</td> <td>BigDecimal</td> <td>Ежемесячный платёж</td> </tr>
    <tr> <td>rate</td> <td>BigDecimal</td> <td>Процентная ставка</td> </tr>
    <tr> <td>psk</td> <td>BigDecimal</td> <td>Полная стоимость кредита</td> </tr>
    <tr> <td>isInsuranceEnabled</td> <td>boolean</td> <td>Страховка включена</td>  </tr>
    <tr> <td>isSalaryClient</td> <td>boolean</td> <td>Зарплатный клиент</td>  </tr>
    <tr> <td>paymentSchedule</td> <td> List&lt;PaymentScheduleElementDto&gt;</td> <td>График платежей</td> </tr>
</tbody>
</table>

Возможные коды ответа:
- 200 OK с посчитанными кредитными предложениями.
- 406 Not acceptable - в случае неудачного скоринга.
- 500 Internal server error - в случае неудачной валидации входных данных LoanStatementDto.

#### 2.2 Сведения о методике расчёта
1. На основании предоставленных пользователем данных рассчитывается индивидуальная процентная ставка.
2. Рассчитывается ежемесячный платёж по формулам (1, 2).