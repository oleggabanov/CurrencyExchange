package com.move.controller.currency;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.move.dto.ErrorResponse;
import com.move.service.currency.CurrenciesService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/currencies")
public class CurrenciesController extends HttpServlet {

  private ObjectMapper objectMapper = new ObjectMapper();
  private CurrenciesService currenciesService = new CurrenciesService();


  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      objectMapper.writerWithDefaultPrettyPrinter()
              .writeValue(response.getWriter(), currenciesService.getCurrencies());
    } catch (Exception e) {
      ErrorResponse errorResponse = new ErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server Error");
      objectMapper.writerWithDefaultPrettyPrinter()
              .writeValue(response.getWriter(), errorResponse);
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    Map<String, String[]> requestParams = request.getParameterMap();
    try {
      objectMapper.writerWithDefaultPrettyPrinter()
              .writeValue(response.getWriter(), currenciesService.addCurrency(requestParams));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
