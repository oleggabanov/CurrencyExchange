package com.move.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrencyResponse {

  private int id;
  private String code;
  private String fullName;
  private String sign;


}
