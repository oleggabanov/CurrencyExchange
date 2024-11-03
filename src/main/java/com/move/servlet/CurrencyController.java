package com.move.servlet;

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


  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    String pathInfo = request.getRequestURI();
    String currencyCode = pathInfo.split("/")[2];
    CurrenciesService currenciesService = new CurrenciesService();
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    try {
      objectMapper.writerWithDefaultPrettyPrinter().writeValue(response.getWriter(), currenciesService.getCurrencyByCode(currencyCode));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }




}
