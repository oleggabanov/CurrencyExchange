package com.move.servlet.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.move.service.CurrenciesService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyController extends HttpServlet {

  private ObjectMapper objectMapper = new ObjectMapper();
  private CurrenciesService currenciesService = new CurrenciesService();


  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    String pathInfo = request.getRequestURI();
    String currencyCode = pathInfo.split("/")[2];
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    try {
      objectMapper.writerWithDefaultPrettyPrinter().writeValue(response.getWriter(), currenciesService.getCurrencyByCode(currencyCode));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


}
