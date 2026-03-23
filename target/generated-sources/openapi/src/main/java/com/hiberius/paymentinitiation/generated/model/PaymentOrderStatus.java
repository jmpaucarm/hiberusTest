package com.hiberius.paymentinitiation.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Business lifecycle state of the payment order.
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-03-22T21:01:34.541313700-05:00[America/Guayaquil]", comments = "Generator version: 7.10.0")
public enum PaymentOrderStatus {
  
  RECEIVED("RECEIVED"),
  
  PENDING("PENDING"),
  
  ACCEPTED("ACCEPTED"),
  
  REJECTED("REJECTED"),
  
  SETTLED("SETTLED"),
  
  FAILED("FAILED");

  private String value;

  PaymentOrderStatus(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static PaymentOrderStatus fromValue(String value) {
    for (PaymentOrderStatus b : PaymentOrderStatus.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

