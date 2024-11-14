package com.move.web.controller.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.move.context.AppContext;
import com.move.model.CurrencyExchange;
import com.move.service.exchange.CurrencyExchangeService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchange")
public class CurrencyExchangeController extends HttpServlet {

  private ObjectMapper objectMapper = AppContext.getInstance().getObjectMapper();
  private CurrencyExchangeService currencyExchangeService = new CurrencyExchangeService();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String baseCurrencyCode = req.getParameter("from");
    String targetCurrencyCode = req.getParameter("to");
    BigDecimal amount = new BigDecimal(req.getParameter("amount"));

    CurrencyExchange currencyExchange = currencyExchangeService.convertCurrency(baseCurrencyCode, targetCurrencyCode, amount);

    objectMapper.writerWithDefaultPrettyPrinter()
            .writeValue(resp.getWriter(), currencyExchange);
  }

}
