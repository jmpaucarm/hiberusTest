package com.hiberius.paymentinitiation.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * InstructedAmount
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-03-22T21:01:34.541313700-05:00[America/Guayaquil]", comments = "Generator version: 7.10.0")
public class InstructedAmount {

  private Double amount;

  private String currency;

  public InstructedAmount() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public InstructedAmount(Double amount, String currency) {
    this.amount = amount;
    this.currency = currency;
  }

  public InstructedAmount amount(Double amount) {
    this.amount = amount;
    return this;
  }

  /**
   * Monetary amount (JSON number; avoid floating-point in domain implementation).
   * minimum: 0.01
   * @return amount
   */
  @NotNull @DecimalMin("0.01") 
  @JsonProperty("amount")
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public InstructedAmount currency(String currency) {
    this.currency = currency;
    return this;
  }

  /**
   * ISO 4217 alphabetic currency code.
   * @return currency
   */
  @NotNull @Pattern(regexp = "^[A-Z]{3}$") @Size(min = 3, max = 3) 
  @JsonProperty("currency")
  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InstructedAmount instructedAmount = (InstructedAmount) o;
    return Objects.equals(this.amount, instructedAmount.amount) &&
        Objects.equals(this.currency, instructedAmount.currency);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount, currency);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InstructedAmount {\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

