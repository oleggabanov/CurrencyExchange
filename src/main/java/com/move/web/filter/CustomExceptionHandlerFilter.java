package com.move.web.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.move.dto.ErrorResponse;
import com.move.exception.EntityAlreadyExistsException;
import com.move.exception.EntityNotFoundException;
import com.move.exception.ParamAbsenceException;
import com.move.exception.WrongParamException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(filterName = "ExceptionHandler", value = "/*")
public class CustomExceptionHandlerFilter implements Filter {

  private ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void doFilter(ServletRequest servletRequest,
                       ServletResponse servletResponse,
                       FilterChain filterChain) throws IOException, ServletException {
    HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
    httpResponse.setContentType("application/json; charset=utf-8");
    ErrorResponse errorResponse;
    try {
      filterChain.doFilter(servletRequest, servletResponse);
    } catch (Exception e) {

      if (e instanceof EntityNotFoundException) {
        httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
        errorResponse = getErrorResponse(404, "Сущность не найдена", e.getMessage());
        writeErrorResponse(httpResponse, errorResponse);
      } else if (e instanceof WrongParamException) {
        httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        errorResponse = getErrorResponse(400, "Проверьте поля формы на корректность", e.getMessage());
        writeErrorResponse(httpResponse, errorResponse);
      } else if (e instanceof EntityAlreadyExistsException) {
        httpResponse.setStatus(HttpServletResponse.SC_CONFLICT);
        errorResponse = getErrorResponse(409, "Валюта с таким кодом уже существует", e.getMessage());
        writeErrorResponse(httpResponse, errorResponse);
      } else if (e instanceof ParamAbsenceException) {
        httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        errorResponse = getErrorResponse(400, "Отсутствует нужное поле формы", e.getMessage());
        writeErrorResponse(httpResponse, errorResponse);
      } else {
        httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        errorResponse = getErrorResponse(500, "База данных недоступна", e.getMessage());
        writeErrorResponse(httpResponse, errorResponse);
      }
    }

  }

  private void writeErrorResponse(HttpServletResponse httpResponse, ErrorResponse errorResponse) throws IOException {
    objectMapper.writerWithDefaultPrettyPrinter()
            .writeValue(httpResponse.getWriter(), errorResponse);
  }

  private static ErrorResponse getErrorResponse(int status, String message, String details) {
    ErrorResponse errorResponse = ErrorResponse.builder()
            .status(status)
            .message(message)
            .details(details)
            .build();
    return errorResponse;
  }
}
