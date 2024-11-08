package com.move.servlet.exchangeRate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.move.model.response.ExchangeRateResponse;
import com.move.service.ExchangeRatesService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchangeRate/*")
public class ExchangeRateController extends HttpServlet {

  private ObjectMapper objectMapper = new ObjectMapper();
  private ExchangeRatesService exchangeRatesService = new ExchangeRatesService();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    String requestURI = request.getRequestURI();
    String exchangeRateCodes = requestURI.split("/")[2];

    String firstCode = exchangeRateCodes.substring(0, 3);
    String secondCode = exchangeRateCodes.substring(3);

    ExchangeRateResponse exchangeRateResponse = exchangeRatesService.getExchangeRate(firstCode, secondCode);
    try {
      objectMapper.writerWithDefaultPrettyPrinter()
              .writeValue(response.getWriter(), exchangeRateResponse);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String requestURI = request.getRequestURI();
    String exchangeRateCodes = requestURI.split("/")[2];
    String firstCode = exchangeRateCodes.substring(0, 3);
    String secondCode = exchangeRateCodes.substring(3);
    BigDecimal rate = new BigDecimal(request.getParameter("rate"));

    ExchangeRateResponse exchangeRateResponse = exchangeRatesService.updateExchangeRate(firstCode,secondCode,rate);

    objectMapper.writerWithDefaultPrettyPrinter().writeValue(response.getWriter(),exchangeRateResponse);
  }

  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String requestURI = request.getRequestURI();
    String exchangeRateCodes = requestURI.split("/")[2];

    String firstCode = exchangeRateCodes.substring(0, 3);
    String secondCode = exchangeRateCodes.substring(3);

    objectMapper.writerWithDefaultPrettyPrinter()
            .writeValue(response.getWriter(), exchangeRatesService.deleteExchangeRate(firstCode,secondCode));
  }
}
