package com.move.currency.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrencyResponse {

  private int id;
  private String code;
  private String fullName;
  private String sign;

}
