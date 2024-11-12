package com.move.controller.currency;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.move.dto.CurrencyDto;
import com.move.dto.ErrorResponse;
import com.move.exceptions.EntityAlreadyExistsException;
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
      response.setStatus(HttpServletResponse.SC_OK);
    } catch (Exception e) {
      ErrorResponse errorResponse = new ErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server Error", e.getMessage());
      objectMapper.writerWithDefaultPrettyPrinter()
              .writeValue(response.getWriter(), errorResponse);
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Map<String, String[]> requestParams = request.getParameterMap();
    ErrorResponse errorResponse;

    if (requestParams.size() != 3) {
      errorResponse = ErrorResponse.builder()
              .status(400)
              .message("Отсутствует нужное поле формы")
              .details("Проверьте корректность вводимых данных")
              .build();
      objectMapper.writerWithDefaultPrettyPrinter()
              .writeValue(response.getWriter(), errorResponse);
    }

    CurrencyDto currencyDto = CurrencyDto.builder()
            .code(requestParams.get("code")[0])
            .fullName(requestParams.get("full_name")[0])
            .sign(requestParams.get("sign")[0])
            .build();

    try {
      objectMapper.writerWithDefaultPrettyPrinter()
              .writeValue(response.getWriter(), currenciesService.addCurrency(currencyDto));
      response.setStatus(HttpServletResponse.SC_CREATED);
    } catch (EntityAlreadyExistsException e) {
      errorResponse = ErrorResponse.builder()
              .status(409)
              .message("Валюта с таким кодом уже существует")
              .details("Проверьте корректность вводимых данных и повторите попытку")
              .build();
      objectMapper.writerWithDefaultPrettyPrinter()
              .writeValue(response.getWriter(), errorResponse);
    } catch (IOException e) {
      errorResponse = ErrorResponse.builder()
              .status(500)
              .message("База данных недоступна")
              .details(e.getMessage())
              .build();
      objectMapper.writerWithDefaultPrettyPrinter()
              .writeValue(response.getWriter(), errorResponse);
    }
  }

}
