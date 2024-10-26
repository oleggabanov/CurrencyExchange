package com.move.servlet;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.move.model.CurrencyResponse;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/currencies")
public class CurrenciesController extends HttpServlet {

  private ObjectMapper mapper = new ObjectMapper();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("application/json");
    CurrencyResponse currencyResponse = new CurrencyResponse(1, "RUB", "Russian Ruble", "₽");
    mapper.writeValue(resp.getOutputStream(), currencyResponse);

  }

}
