[![codecov ms calculator](https://codecov.io/github/DKavtasyev/CreditBank/branch/feature%2Fadd_docker_and_ci/graph/badge.svg?flag=calculator&token=EG63IGUXHV)](https://codecov.io/github/DKavtasyev/CreditBank)
# MS Calculator
### Документация API
Для просмотра и тестирования доступных API методов используется Swagger UI, который доступен по следующему адресу:
> http://localhost:8081/swagger-ui/index.html

### Функции:
1. Предварительный расчёт кредитных предложений.
2. Расчёт кредита с указанием дат, сумм платежей. 

### 1. Предварительный расчёт кредитных предложений
#### 1.1 Сведения о запросе и ответе

- Путь: /calculator/offers
- Метод: POST
- Тело запроса: LoanStatementRequestDto
- Тело ответа: List&lt;LoanOfferDto>

Структура LoanStatementRequestDto

<table>
<thead>
    <tr><th>Поле</th><th>Тип</th><th>Описание</th><th>Требования</th></tr>
</thead>
<tbody>
    <tr><td>amount</td><td>BigDecimal</td><td>Запрашиваемая сумма, руб.</td><td>30000 руб., не менее </td></tr>
    <tr><td>term</td><td>Integer</td><td>Запрашиваемый срок кредитования, мес.</td><td>6 мес., не менее</td></tr>
    <tr><td>firstName</td><td>String</td><td>Имя</td><td>От 2 до 30 символов включ.</td></tr>
    <tr><td>lastName</td><td>String</td><td>Фамилия</td><td>От 2 до 30 символов включ.</td></tr>
    <tr><td>middleName</td><td>String</td><td>Отчество</td><td>От 2 до 30 символов включ.</td></tr>
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
- 200 OK - успешный расчёт кредитных предложений.
- 500 Internal server error - ошибка валидации входных данных LoanStatementDto.

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
Для каждого случая корректируется процентная ставка. При включении страховки к общей сумме кредита прибавляется 5 %.
Затем рассчитывается ежемесячный платёж по формуле (1) с точностью до 16 знаков после запятой:

<!-- https://editor.codecogs.com/ -->

<p style="display: flex; align-items: center; justify-content: center;">
  <img src="https://latex.codecogs.com/png.image?%5Cinline%20%5Cdpi%7B110%7D%7B%5Ccolor%7BWhite%7DX=S%5Ctimes%20K%7D" title="{\color{White}X = S \times K}" style="margin: 0 auto;"  alt="monthlyPayment"/>(1)
</p>

где *X* - аннуитетный платёж;\
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*S* - общая сумма кредита;\
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*K* - коэффициент аннуитета.


Аннуитетный коэффициент *K* рассчитывается по формуле:


<p style="display: flex; align-items: center; justify-content: center;">
  <img src="https://latex.codecogs.com/png.image?%5Cinline%20%5Cdpi%7B110%7D%7B%5Ccolor%7BWhite%7DK=p&plus;%5Cfrac%7Bp%7D%7B(1&plus;p)%5E%7BN%7D-1%7D%7D" title=" {\color{White}K = P + \frac{P}{(1 + P)^{N} - 1}}" style="margin: 0 auto;"  alt="monthlyPayment"/>(2)
</p>

где *p* - процентная ставка за месяц в виде десятичной дроби;\
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*N* - срок кредитования в месяцах.

### 2 Расчёт графика платежей по кредиту
#### 2.1 Сведения о запросе и ответе

- Путь: /calculator/calc
- Метод: POST
- Тело запроса: ScoringDataDto
- Тело ответа: CreditDto

Структура ScoringDataDto

<table>
<thead>
    <tr><th>Поле</th><th>Тип</th><th>Описание</th><th>Требования</th></tr>
</thead>
<tbody>
    <tr><td>amount</td> <td>BigDecimal</td> <td>Запрашиваемая сумма</td><td>30000 руб., не менее </td></tr>
    <tr><td>term</td> <td>Integer</td> <td>Запрашиваемый срок кредитования</td><td>6 мес., не менее </td></tr>
    <tr><td>firstName</td> <td>String</td> <td>Имя</td><td>От 2 до 30 символов включ.</td></tr>
    <tr><td>lastName</td> <td>String</td> <td>Фамилия</td><td>От 2 до 30 символов включ.</td></tr>
    <tr><td>middleName</td> <td>String</td> <td>Отчество</td><td>От 2 до 30 символов включ.</td></tr>
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
- 200 OK - успешный расчёт графика платежей.
- 406 Not acceptable - данные пользователя не соответствуют требованиям для получения кредита (неудачный скоринг).
- 500 Internal server error - ошибка валидации входных данных LoanStatementDto.

#### 2.2 Сведения о методике расчёта
1. На основании данных пользователя рассчитывается индивидуальная процентная ставка.
2. Рассчитывается ежемесячный платёж по формулам (1, 2).
3. Рассчитывается первый элемент графика платежей*.
    1. Рассчитываются проценты по формуле, начисленные на момент платежа за период пользования денежными средствами:
       <br><br>
       <p style="display: flex; align-items: center; justify-content: center;">
         <img src="https://latex.codecogs.com/png.image?%5Cinline%20%5Cdpi%7B110%7D%7B%5Ccolor%7BWhite%7DI=S_%7Bn%7D%5Ctimes%5Cfrac%7BP%7D%7B365%7D%5Ctimes%20t%7D" title="{\color{White}I=S_{n}\times\frac{P}{365}\times t}" style="margin: 0 auto;" alt="percents"/> (3)
       </p>
       <br>
       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;где <i>P</i> - процентная ставка в виде десятичной дроби; <br>   
       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
       <img src="https://latex.codecogs.com/png.image?%5Cinline%20%5Cdpi%7B110%7D%7B%5Ccolor%7BWhite%7DS_%7Bn%7D%7D" title="{\color{White}S_{n}}"  alt="remainingAmount"/> - остаток тела кредита после каждого ежемесячного платежа;<br>
       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i>t</i> - количество дней, в течение которых начислялись проценты.
    
    2. Часть платежа, идущая на погашение основного долга, рассчитывается по формуле (4):
       <br><br>
       <p style="display: flex; align-items: center; justify-content: center;">
         <img src="https://latex.codecogs.com/png.image?%5Cinline%20%5Cdpi%7B110%7D%7B%5Ccolor%7BWhite%7DD=X-I%7D" title="{\color{White}D=X-I}" style="margin: 0 auto;" alt="percents"/> (4)
       </p>
       <br>
       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;где <i>X</i> - сумма ежемесячного платежа; <br>
       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i>I</i> - часть платежа в счёт погашения процентов.
       
    3. Остаток тела кредита после проведения платежа вычисляется по формуле (5):
       <br><br>
       <p style="display: flex; align-items: center; justify-content: center;">
         <img src="https://latex.codecogs.com/png.image?%5Cinline%20%5Cdpi%7B110%7D%7B%5Ccolor%7BWhite%7DS_%7Bn&plus;1%7D=S_%7Bn%7D-D%7D" title="{\color{White}S_{n+1}=S_{n}-D}" style="margin: 0 auto;" alt="newRemainingDebt"/> (5)
       </p>
       <br>

4. На основании первого элемента рекурсивно рассчитываются все последующие элементы графика платежей до полного
погашения кредита.

---
&#x002A; все расчёты проводятся с точностью до 16 знаков после запятой.  

[К основному README](./../README.md)