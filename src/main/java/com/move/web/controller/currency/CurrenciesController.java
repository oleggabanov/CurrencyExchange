package com.move.web.controller.currency;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.move.context.AppContext;
import com.move.dto.CurrencyDto;
import com.move.exception.ParamAbsenceException;
import com.move.exception.WrongParamException;
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
    String code = requestParams.get("code")[0];
    String name = requestParams.get("name")[0];
    String sign = requestParams.get("sign")[0];

    if (requestParams.values().size() != 3) {
      throw new ParamAbsenceException("Отсутствует нужное поле формы");
    }

    if (code.length() != 3 || name.length() > 30 || sign.length() != 1){
      throw new WrongParamException("Код валюты должен состоять из 3 символов. Имя валюты содержит не больше 30 символов, а знак валюты не больше 1 символа ");
    }

    CurrencyDto currencyDto = CurrencyDto.builder()
            .code(code)
            .fullName(name)
            .sign(sign)
            .build();

    response.setStatus(HttpServletResponse.SC_CREATED);
    objectMapper.writerWithDefaultPrettyPrinter()
            .writeValue(response.getWriter(), currencyService.addCurrency(currencyDto));
  }

}
