package com.move.servlet;


import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/")
public class CurrenciesController extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    PrintWriter printWriter = resp.getWriter();
    printWriter.println("<html>");
    printWriter.println("<h1>Hello World!</h1>");
    printWriter.println("</html>");
    printWriter.close();
  }


}
