package com.move.web.controller.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
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

  private ObjectMapper objectMapper = new ObjectMapper();
  private ExchangeRatesService exchangeRatesService = new ExchangeRatesService();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String[] requestUri = request.getRequestURI().split("/");
    if (requestUri.length != 3 || requestUri[2].length() != 6) {
      throw new ParamAbsenceException("Коды валют пары отсутствуют в адресе");
    }
    String exchangeRateCodes = requestUri[2];
    String firstCode = exchangeRateCodes.substring(0, 3);
    String secondCode = exchangeRateCodes.substring(3);
    ExchangeRate exchangeRate = exchangeRatesService.getExchangeRateByCurrencyCodes(firstCode, secondCode);

    response.setStatus(HttpServletResponse.SC_OK);
    objectMapper.writerWithDefaultPrettyPrinter()
            .writeValue(response.getWriter(), exchangeRate);
  }

  @Override
  protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String requestURI = request.getRequestURI();
    String exchangeRateCodes = requestURI.split("/")[2];
    String firstCode = exchangeRateCodes.substring(0, 3);
    String secondCode = exchangeRateCodes.substring(3);
    Map<String, String[]> requestParams = request.getParameterMap();

    if (requestParams.size() != 1) {
      throw new ParamAbsenceException("Отсутствует нужное поле формы");
    }

    BigDecimal rate = new BigDecimal(requestParams.get("rate")[0]);
    ExchangeRate exchangeRate = exchangeRatesService.updateExchangeRate(firstCode, secondCode, rate);
    response.setStatus(HttpServletResponse.SC_OK);
    objectMapper.writerWithDefaultPrettyPrinter().writeValue(response.getWriter(), exchangeRate);
  }

  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String requestURI = request.getRequestURI();
    String exchangeRateCodes = requestURI.split("/")[2];
    String firstCode = exchangeRateCodes.substring(0, 3);
    String secondCode = exchangeRateCodes.substring(3);

    exchangeRatesService.deleteExchangeRate(firstCode, secondCode);
  }
}
