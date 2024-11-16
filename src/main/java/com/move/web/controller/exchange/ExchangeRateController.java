package com.move.web.controller.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.move.context.AppContext;
import com.move.dto.ExchangeRateDto;
import com.move.exception.ParamAbsenceException;
import com.move.exception.WrongParamException;
import com.move.model.ExchangeRate;
import com.move.service.exchange.ExchangeRateService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchangeRate/*")
public class ExchangeRateController extends HttpServlet {

  private ObjectMapper objectMapper = AppContext.getInstance().getObjectMapper();
  private ExchangeRateService exchangeRateService = new ExchangeRateService();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String[] requestUri = request.getRequestURI().split("/");
    if (requestUri.length != 3 || requestUri[2].length() != 6) {
      throw new ParamAbsenceException("Коды валют пары отсутствуют в адресе");
    }
    String exchangeRateCodes = requestUri[2];
    String baseCurrencyCode = exchangeRateCodes.substring(0, 3);
    String targetCurrencyCode = exchangeRateCodes.substring(3);

    ExchangeRate exchangeRate = exchangeRateService.getExchangeRateByCurrencyCodes(
            ExchangeRateDto.builder()
                    .baseCurrencyCode(baseCurrencyCode)
                    .targetCurrencyCode(targetCurrencyCode)
                    .build()
    );

    response.setStatus(HttpServletResponse.SC_OK);
    objectMapper.writerWithDefaultPrettyPrinter()
            .writeValue(response.getWriter(), exchangeRate);
  }

  @Override
  protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String stringRate = request.getReader()
            .lines()
            .reduce("", (a, b) -> a + b);

    if (stringRate.isEmpty()) {
      throw new ParamAbsenceException("Отсутствует нужное поле формы");
    }

    BigDecimal rate = new BigDecimal(stringRate.split("=")[1]);
    String[] requestURI = request.getRequestURI().split("/");
    String exchangeRateCodes = requestURI[requestURI.length - 1];
    boolean isLengthOfCodesFit = exchangeRateCodes.length() == 6;
    String baseCurrencyCode = exchangeRateCodes.substring(0, 3);
    String targetCurrencyCode = exchangeRateCodes.substring(3);

    if (!isLengthOfCodesFit || rate.compareTo(BigDecimal.ZERO) < 0 || baseCurrencyCode.equals(targetCurrencyCode)) {
      throw new WrongParamException("Каждый код валюты должен состоять из 3 символов и не повторять другой. Курс валютной пары должен быть положительным числом");
    }

    ExchangeRate exchangeRate = exchangeRateService.updateExchangeRate(
            ExchangeRateDto.builder()
                    .baseCurrencyCode(baseCurrencyCode)
                    .targetCurrencyCode(targetCurrencyCode)
                    .rate(rate)
                    .build()
    );
    response.setStatus(HttpServletResponse.SC_OK);
    objectMapper.writerWithDefaultPrettyPrinter().writeValue(response.getWriter(), exchangeRate);
  }

  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
    String requestURI = request.getRequestURI();
    String[] uri = requestURI.split("/");
    String exchangeRateCodes = uri[uri.length - 1];

    if (exchangeRateCodes.length() != 6) {
      throw new WrongParamException("Каждый код валюты должен состоять из 3 символов");
    }

    String baseCurrencyCode = exchangeRateCodes.substring(0, 3);
    String targetCurrencyCode = exchangeRateCodes.substring(3);

    exchangeRateService.deleteExchangeRate(
            ExchangeRateDto.builder()
                    .baseCurrencyCode(baseCurrencyCode)
                    .targetCurrencyCode(targetCurrencyCode)
                    .build()
    );
  }
}
