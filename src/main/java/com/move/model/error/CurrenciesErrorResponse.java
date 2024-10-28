package com.move.model.error;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrenciesErrorResponse {

  private int errorCode;
  private String errorMessage;

}
