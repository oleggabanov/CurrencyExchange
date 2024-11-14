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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

@WebServlet("/exchangeRate/*")
public class ExchangeRateController extends HttpServlet {

  private ObjectMapper objectMapper = AppContext.getInstance().getObjectMapper();
  private ExchangeRatesService exchangeRatesService = new ExchangeRatesService();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String[] requestUri = request.getRequestURI().split("/");
    if (requestUri.length != 3 || requestUri[2].length() != 6) {
      throw new ParamAbsenceException("Коды валют пары отсутствуют в адресе");
    }
    String exchangeRateCodes = requestUri[2];
    String baseCurrencyCode = exchangeRateCodes.substring(0, 3);
    String targetCurrencyCode = exchangeRateCodes.substring(3);


    ExchangeRate exchangeRate = exchangeRatesService.getExchangeRateByCurrencyCodes(
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
    String requestURI = request.getRequestURI();
    String exchangeRateCodes = requestURI.split("/")[2];
    String baseCurrencyCode = exchangeRateCodes.substring(0, 3);
    String targetCurrencyCode = exchangeRateCodes.substring(3);
    Map<String, String[]> requestParams = request.getParameterMap();

    if (requestParams.size() != 1) {
      throw new ParamAbsenceException("Отсутствует нужное поле формы");
    }

    BigDecimal rate = new BigDecimal(requestParams.get("rate")[0]);
    ExchangeRate exchangeRate = exchangeRatesService.updateExchangeRate(
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
    String exchangeRateCodes = requestURI.split("/")[2];
    String baseCurrencyCode = exchangeRateCodes.substring(0, 3);
    String targetCurrencyCode = exchangeRateCodes.substring(3);

    exchangeRatesService.deleteExchangeRate(
            ExchangeRateDto.builder()
                    .baseCurrencyCode(baseCurrencyCode)
                    .targetCurrencyCode(targetCurrencyCode)
                    .build()
    );
  }
}
