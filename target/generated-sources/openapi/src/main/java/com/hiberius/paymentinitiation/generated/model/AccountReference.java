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
 * AccountReference
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-03-22T21:01:34.541313700-05:00[America/Guayaquil]", comments = "Generator version: 7.10.0")
public class AccountReference {

  private String iban;

  public AccountReference() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public AccountReference(String iban) {
    this.iban = iban;
  }

  public AccountReference iban(String iban) {
    this.iban = iban;
    return this;
  }

  /**
   * Account identifier for the party. In production this is often a normalized IBAN; the API does not enforce IBAN checksum or structure so that tests, mocks, and sandboxes can use dummy values (e.g. short labels, non-IBAN strings). Stricter validation may be applied in the service layer when required. 
   * @return iban
   */
  @NotNull @Size(min = 1, max = 64) 
  @JsonProperty("iban")
  public String getIban() {
    return iban;
  }

  public void setIban(String iban) {
    this.iban = iban;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AccountReference accountReference = (AccountReference) o;
    return Objects.equals(this.iban, accountReference.iban);
  }

  @Override
  public int hashCode() {
    return Objects.hash(iban);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AccountReference {\n");
    sb.append("    iban: ").append(toIndentedString(iban)).append("\n");
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

