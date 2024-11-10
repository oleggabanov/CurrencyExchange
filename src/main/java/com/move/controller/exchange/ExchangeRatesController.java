package com.move.controller.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.move.model.ExchangeRateResponse;
import com.move.service.ExchangeRatesService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet("/exchangeRates")
public class ExchangeRatesController extends HttpServlet {

  private ObjectMapper objectMapper = new ObjectMapper();
  private ExchangeRatesService exchangeRatesService = new ExchangeRatesService();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    try {
      objectMapper.writerWithDefaultPrettyPrinter()
              .writeValue(resp.getWriter(), exchangeRatesService.getAllExchangeRates());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String baseCurrencyCode = req.getParameter("baseCurrencyCode");
    String targetCurrencyCode = req.getParameter("targetCurrencyCode");
    BigDecimal rate = new BigDecimal(req.getParameter("rate"));
    ExchangeRateResponse exchangeRateResponse = exchangeRatesService.addExchangeRate(baseCurrencyCode, targetCurrencyCode, rate);
    objectMapper.writerWithDefaultPrettyPrinter()
            .writeValue(resp.getWriter(), exchangeRateResponse);
  }


}
