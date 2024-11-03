package com.move.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.move.service.ExchangeRatesService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/exchangeRates")
public class ExchangeRatesController extends HttpServlet {

  private ObjectMapper objectMapper = new ObjectMapper();
  private ExchangeRatesService exchangeRatesService = new ExchangeRatesService();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");
    try {
      objectMapper.writerWithDefaultPrettyPrinter()
              .writeValue(resp.getWriter(), exchangeRatesService.getAllExchangeRates());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
