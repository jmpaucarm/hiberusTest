package com.hiberius.paymentinitiation.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.hiberius.paymentinitiation.generated.model.PaymentOrderStatus;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PaymentOrderCreationResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-03-22T21:01:34.541313700-05:00[America/Guayaquil]", comments = "Generator version: 7.10.0")
public class PaymentOrderCreationResponse {

  private String paymentOrderId;

  private String externalReference;

  private PaymentOrderStatus status;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.OffsetDateTime createdAt;

  public PaymentOrderCreationResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public PaymentOrderCreationResponse(String paymentOrderId, PaymentOrderStatus status, java.time.OffsetDateTime createdAt) {
    this.paymentOrderId = paymentOrderId;
    this.status = status;
    this.createdAt = createdAt;
  }

  public PaymentOrderCreationResponse paymentOrderId(String paymentOrderId) {
    this.paymentOrderId = paymentOrderId;
    return this;
  }

  /**
   * Opaque internal identifier for the payment order (e.g. UUID, business key, or legacy key). No single format is mandated by this API. 
   * @return paymentOrderId
   */
  @NotNull @Size(min = 1, max = 128) 
  @JsonProperty("paymentOrderId")
  public String getPaymentOrderId() {
    return paymentOrderId;
  }

  public void setPaymentOrderId(String paymentOrderId) {
    this.paymentOrderId = paymentOrderId;
  }

  public PaymentOrderCreationResponse externalReference(String externalReference) {
    this.externalReference = externalReference;
    return this;
  }

  /**
   * Echo of the client reference for correlation.
   * @return externalReference
   */
  @Size(max = 64) 
  @JsonProperty("externalReference")
  public String getExternalReference() {
    return externalReference;
  }

  public void setExternalReference(String externalReference) {
    this.externalReference = externalReference;
  }

  public PaymentOrderCreationResponse status(PaymentOrderStatus status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  @NotNull @Valid 
  @JsonProperty("status")
  public PaymentOrderStatus getStatus() {
    return status;
  }

  public void setStatus(PaymentOrderStatus status) {
    this.status = status;
  }

  public PaymentOrderCreationResponse createdAt(java.time.OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Creation timestamp (UTC recommended).
   * @return createdAt
   */
  @NotNull @Valid 
  @JsonProperty("createdAt")
  public java.time.OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(java.time.OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentOrderCreationResponse paymentOrderCreationResponse = (PaymentOrderCreationResponse) o;
    return Objects.equals(this.paymentOrderId, paymentOrderCreationResponse.paymentOrderId) &&
        Objects.equals(this.externalReference, paymentOrderCreationResponse.externalReference) &&
        Objects.equals(this.status, paymentOrderCreationResponse.status) &&
        Objects.equals(this.createdAt, paymentOrderCreationResponse.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(paymentOrderId, externalReference, status, createdAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentOrderCreationResponse {\n");
    sb.append("    paymentOrderId: ").append(toIndentedString(paymentOrderId)).append("\n");
    sb.append("    externalReference: ").append(toIndentedString(externalReference)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
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

