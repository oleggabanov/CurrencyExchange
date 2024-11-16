package com.move.web.controller.currency;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.move.context.AppContext;
import com.move.dto.CurrencyDto;
import com.move.exception.ParamAbsenceException;
import com.move.service.currency.CurrencyService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/currencies")
public class CurrenciesController extends HttpServlet {

  private ObjectMapper objectMapper = AppContext.getInstance().getObjectMapper();
  private CurrencyService currencyService = new CurrencyService();


  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setStatus(HttpServletResponse.SC_OK);
    objectMapper.writerWithDefaultPrettyPrinter()
              .writeValue(response.getWriter(), currencyService.getCurrencies());
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Map<String, String[]> requestParams = request.getParameterMap();

    if (requestParams.values().size() != 3) {
      throw new ParamAbsenceException("Отсутствует нужное поле формы");
    }

    CurrencyDto currencyDto = CurrencyDto.builder()
            .code(requestParams.get("code")[0])
            .fullName(requestParams.get("name")[0])
            .sign(requestParams.get("sign")[0])
            .build();

    response.setStatus(HttpServletResponse.SC_CREATED);
    objectMapper.writerWithDefaultPrettyPrinter()
              .writeValue(response.getWriter(), currencyService.addCurrency(currencyDto));
  }

}
