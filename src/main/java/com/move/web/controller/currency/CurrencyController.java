package com.move.web.controller.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.move.context.AppContext;
import com.move.exception.ParamAbsenceException;
import com.move.exception.WrongParamException;
import com.move.service.currency.CurrencyService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyController extends HttpServlet {

  private final ObjectMapper objectMapper = AppContext.getInstance().getObjectMapper();
  private final CurrencyService currencyService = AppContext.getInstance().getCurrencyService();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String[] requestURI = request.getRequestURI().split("/");
    String code = requestURI[requestURI.length - 1];
    if (requestURI.length != 3) {
      throw new ParamAbsenceException("Код валюты отсутствует в адресе");
    }

    if (code.length() != 3){
      throw new WrongParamException("Код валюты должен состоять из трех символов");
    }

    response.setStatus(HttpServletResponse.SC_OK);
    objectMapper.writerWithDefaultPrettyPrinter()
            .writeValue(response.getWriter(), currencyService.getCurrencyByCode(code.toUpperCase()));
  }

}
