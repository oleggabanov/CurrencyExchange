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
import lombok.SneakyThrows;

import java.math.BigDecimal;
import java.util.Map;

@WebServlet("/exchangeRates")
public class ExchangeRatesController extends HttpServlet {

  private ObjectMapper objectMapper = AppContext.getInstance().getObjectMapper();
  private ExchangeRateService exchangeRateService = new ExchangeRateService();

  @Override
  @SneakyThrows
  protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    response.setStatus(HttpServletResponse.SC_OK);
    objectMapper.writerWithDefaultPrettyPrinter()
            .writeValue(response.getWriter(), exchangeRateService.getAllExchangeRates());
  }

  @Override
  @SneakyThrows
  protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    Map<String, String[]> requestParams = request.getParameterMap();

    if (requestParams.size() != 3) {
      throw new ParamAbsenceException("Отсутствует нужное поле формы");
    }

    String baseCurrencyCode = requestParams.get("baseCurrencyCode")[0];
    String targetCurrencyCode = requestParams.get("targetCurrencyCode")[0];
    BigDecimal rate = new BigDecimal(requestParams.get("rate")[0]);

    if (baseCurrencyCode.length() != 3 || targetCurrencyCode.length() != 3 || rate.compareTo(BigDecimal.ZERO) < 0 || baseCurrencyCode.equals(targetCurrencyCode)) {
      throw new WrongParamException("Каждый код валюты должен состоять из 3 символов и не повторять другой. Курс валютной пары должен быть положительным числом");
    }

    ExchangeRate exchangeRate = exchangeRateService.addExchangeRate(
            ExchangeRateDto.builder()
                    .baseCurrencyCode(baseCurrencyCode)
                    .targetCurrencyCode(targetCurrencyCode)
                    .rate(rate)
                    .build()
    );

    response.setStatus(HttpServletResponse.SC_CREATED);
    objectMapper.writerWithDefaultPrettyPrinter()
            .writeValue(response.getWriter(), exchangeRate);
  }

}
