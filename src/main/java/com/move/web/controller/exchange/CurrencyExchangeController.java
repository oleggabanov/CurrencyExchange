package com.move.web.controller.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.move.context.AppContext;
import com.move.dto.CurrencyExchangeDto;
import com.move.exception.WrongParamException;
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

  private final ObjectMapper objectMapper = AppContext.getInstance().getObjectMapper();
  private final CurrencyExchangeService currencyExchangeService = AppContext.getInstance().getCurrencyExchangeService();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String baseCurrencyCode = req.getParameter("from");
    String targetCurrencyCode = req.getParameter("to");
    BigDecimal amount = new BigDecimal(req.getParameter("amount"));
    if (baseCurrencyCode.length() != 3 || targetCurrencyCode.length() != 3 ||
            amount.compareTo(BigDecimal.ZERO) < 0 || baseCurrencyCode.equals(targetCurrencyCode)) {
      throw new WrongParamException("Каждый код валюты должен состоять из 3 символов и не повторять другой. Сумма должна быть положительным числом");
    }
    CurrencyExchangeDto currencyExchangeDto = CurrencyExchangeDto.builder()
            .baseCurrencyCode(baseCurrencyCode.toUpperCase())
            .targetCurrencyCode(targetCurrencyCode.toUpperCase())
            .amount(amount)
            .build();
    CurrencyExchange currencyExchange = currencyExchangeService.convertCurrency(currencyExchangeDto);

    objectMapper.writerWithDefaultPrettyPrinter()
            .writeValue(resp.getWriter(), currencyExchange);
  }

}
