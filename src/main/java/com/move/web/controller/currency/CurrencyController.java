package com.move.web.controller.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.move.exception.ParamAbsenceException;
import com.move.service.currency.CurrenciesService;
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
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String[] pathInfo = request.getRequestURI().split("/");

    if (pathInfo.length != 3) {
      throw new ParamAbsenceException("Код валюты отсутствует в адресе");
    }

    response.setStatus(HttpServletResponse.SC_OK);
    objectMapper.writerWithDefaultPrettyPrinter()
            .writeValue(response.getWriter(), currenciesService.getCurrencyByCode(pathInfo[2]));
  }

}
