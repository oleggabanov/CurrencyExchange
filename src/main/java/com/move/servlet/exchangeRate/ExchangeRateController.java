package com.move.servlet.exchangeRate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.move.service.ExchangeRatesService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/exchangeRate/*")
public class ExchangeRateController extends HttpServlet {

  private ObjectMapper objectMapper = new ObjectMapper();
  private ExchangeRatesService exchangeRatesService = new ExchangeRatesService();
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    String requestURI = request.getRequestURI();
    String exchangeRateCodes = requestURI.split("/")[2];

    String firstCode = exchangeRateCodes.substring(0,3);
    String secondCode = exchangeRateCodes.substring(3);

    exchangeRatesService.getExchangeRate(firstCode,secondCode);
    try {
      objectMapper.writeValue(response.getWriter(),firstCode + " : " + secondCode);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }


}
