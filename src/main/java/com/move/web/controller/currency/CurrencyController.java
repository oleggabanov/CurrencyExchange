package com.move.web.controller.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.move.context.AppContext;
import com.move.exception.ParamAbsenceException;
import com.move.service.currency.CurrencyService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyController extends HttpServlet {

  private ObjectMapper objectMapper = AppContext.getInstance().getObjectMapper();
  private CurrencyService currencyService = new CurrencyService();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String[] pathInfo = request.getRequestURI().split("/");

    if (pathInfo.length != 3) {
      throw new ParamAbsenceException("Код валюты отсутствует в адресе");
    }

    response.setStatus(HttpServletResponse.SC_OK);
    objectMapper.writerWithDefaultPrettyPrinter()
            .writeValue(response.getWriter(), currencyService.getCurrencyByCode(pathInfo[pathInfo.length - 1]));
  }

}
