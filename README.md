# REST API Currency Exchange <img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Telegram-Animated-Emojis/main/Symbols/Currency%20Exchange.webp" alt="Currency Exchange" width="25" height="25" />

## Description 


This api allow you to watch and edit currency and exchange rates,
calculate and convert one currency to another. I implemented crud interface, used integrated sqlite db.

### <img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Telegram-Animated-Emojis/main/Symbols/Check%20Mark.webp" alt="Check Mark" width="25" height="25" />I used:

* Java Servlets
* MVC pattern
* Http requests
* Cloud hosting
* SQLite
* Tomcat

## Database Diagram

![DatabaseDiagrams](https://github.com/oleggabanov/CurrencyExchange/blob/main/src/main/resources/img/%D0%A1%D0%BD%D0%B8%D0%BC%D0%BE%D0%BA%20%D1%8D%D0%BA%D1%80%D0%B0%D0%BD%D0%B0%202024-11-15%20125658.png)

## Crud methods


### Currencies

***GET `/currencies`***

Returns list of all currencies. Example of response:

```json
  {
    "id": 1,
    "code": "USD",
    "fullName": "US Dollar",
    "sign": "$"
  },
  {
    "id": 2,
    "code": "EUR",
    "fullName": "Euro",
    "sign": "€"
  },
  {
    "id": 3,
    "code": "RUB",
    "fullName": "Russian Rubble",
    "sign": "₽"
  }
```

***GET `/currency/`RUB***

Returns particular currency:

```json
  {
    "id": 3,
    "code": "RUB",
    "fullName": "Russian Rubble",
    "sign": "₽"
  }
```

***POST `/currencies`?code=KZT&full_name=Tenge&sign=₸***

Add new currency by `x-www-form-urlencoded` params `name, code, sign`. Method will return json response:

```json
{
  "id": 4,
  "code": "KZT",
  "fullName": "Tenge",
  "sign": "₸"
}
```

## Exchange Rates

***GET `/exchangeRates`***

Returns list of all exchange rates. Example of response:

```json
{
    "id": 1,
    "baseCurrency": {
      "id": 1,
      "code": "USD",
      "fullName": "US Dollar",
      "sign": "$"
    },
    "targetCurrency": {
      "id": 2,
      "code": "EUR",
      "fullName": "Euro",
      "sign": "€"
    },
    "rate": 0.9288
  },
  {
    "id": 2,
    "baseCurrency": {
      "id": 2,
      "code": "EUR",
      "fullName": "Euro",
      "sign": "€"
    },
    "targetCurrency": {
      "id": 1,
      "code": "USD",
      "fullName": "US Dollar",
      "sign": "$"
    },
    "rate": 1.0769
  }
```

***GET `/exchangeRate/`USDRUB***

Returns particular exchange rate by currency codes:

```json
{
  "id": 3,
  "baseCurrency": {
    "id": 1,
    "code": "USD",
    "fullName": "US Dollar",
    "sign": "$"
  },
  "targetCurrency": {
    "id": 3,
    "code": "RUB",
    "fullName": "Russian Rubble",
    "sign": "₽"
  },
  "rate": 98.2491
}
```

***POST `/exchangeRates`?baseCurrencyCode=KZT&targetCurrencyCode=RUB&rate=0.20***

Add new exchange rate. The data is transferred in the request body as form fields `x-www-form-urlencoded`. Form fields: `baseCurrencyCode`, `targetCurrencyCode`, `rate`. JSON response is the db insertion:

```json
{
  "id": 4,
  "baseCurrency": {
    "id": 5,
    "code": "KZT",
    "fullName": "Tenge",
    "sign": "₸"
  },
  "targetCurrency": {
    "id": 3,
    "code": "RUB",
    "fullName": "Russian Rubble",
    "sign": "₽"
  },
  "rate": 0.2
}
```

***PATCH `/exchangeRate/`USDRUB?rate=99.83***

Update of exchange rate, response is the db insertion:

```json
{
  "id": 3,
  "baseCurrency": {
    "id": 1,
    "code": "USD",
    "fullName": "US Dollar",
    "sign": "$"
  },
  "targetCurrency": {
    "id": 3,
    "code": "RUB",
    "fullName": "Russian Rubble",
    "sign": "₽"
  },
  "rate": 99.83
}
```

## Exchange sums of money

***GET `/exchange`?from=USD&to=RUB&amount=1000***

Calculate amount of funds from one currency to another. Response example:

```json
{
  "baseCurrency": {
    "id": 1,
    "code": "USD",
    "fullName": "US Dollar",
    "sign": "$"
  },
  "targetCurrency": {
    "id": 3,
    "code": "RUB",
    "fullName": "Russian Rubble",
    "sign": "₽"
  },
  "rate": 99.83,
  "amount": 1000,
  "convertedAmount": 99830.00
}
```

