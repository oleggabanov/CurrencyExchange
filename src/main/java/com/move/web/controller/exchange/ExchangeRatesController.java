package com.move.web.controller.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.move.context.AppContext;
import com.move.dto.ExchangeRateDto;
import com.move.exception.ParamAbsenceException;
import com.move.model.ExchangeRate;
import com.move.service.exchange.ExchangeRatesService;
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
  private ExchangeRatesService exchangeRatesService = new ExchangeRatesService();

  @Override
  @SneakyThrows
  protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    response.setStatus(HttpServletResponse.SC_OK);
    objectMapper.writerWithDefaultPrettyPrinter()
            .writeValue(response.getWriter(), exchangeRatesService.getAllExchangeRates());
  }

  @Override
  @SneakyThrows
  protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    Map<String, String[]> requestParams = request.getParameterMap();

    if (requestParams.size() != 3) {
      throw new ParamAbsenceException("Отсутствует нужное поле формы");
    }

    String baseCurrencyCode = request.getParameter("baseCurrencyCode");
    String targetCurrencyCode = request.getParameter("targetCurrencyCode");
    BigDecimal rate = new BigDecimal(request.getParameter("rate"));


    ExchangeRate exchangeRate = exchangeRatesService.addExchangeRate(
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
