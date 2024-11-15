package com.move.mapper;

import com.move.dto.CurrencyDto;
import com.move.dto.ExchangeRateDto;
import com.move.model.Currency;
import com.move.model.ExchangeRate;

import java.lang.reflect.Field;

public class CustomMapper<T, R> {

  public T map(R base) {
    if (base == null) {
      throw new IllegalArgumentException();
    }
    T target = null;
    if (base instanceof CurrencyDto) {
      target = (T) new Currency();
    } else if (base instanceof ExchangeRateDto) {
      target = (T) new ExchangeRate();
    } else {
      throw new IllegalStateException("Unexpected value: " + base);
    }

    Class<?> baseClass = base.getClass();
    Class<?> targetClass = target.getClass();

    for (Field baseObjectField : baseClass.getDeclaredFields()) {
      baseObjectField.setAccessible(true);
      String fieldName = baseObjectField.getName();
      try {
        Field targetField = targetClass.getDeclaredField(fieldName);
        targetField.setAccessible(true);
        Object value = baseObjectField.get(base);

        targetField.set(target, value);
      } catch (Exception e) {
        throw new IllegalArgumentException();
      }
    }
    return target;
  }
}
