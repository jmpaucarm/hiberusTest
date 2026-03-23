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
 * PaymentOrderStatusView
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-03-22T21:01:34.541313700-05:00[America/Guayaquil]", comments = "Generator version: 7.10.0")
public class PaymentOrderStatusView {

  private String paymentOrderId;

  private PaymentOrderStatus status;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.OffsetDateTime lastUpdated;

  public PaymentOrderStatusView() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public PaymentOrderStatusView(String paymentOrderId, PaymentOrderStatus status, java.time.OffsetDateTime lastUpdated) {
    this.paymentOrderId = paymentOrderId;
    this.status = status;
    this.lastUpdated = lastUpdated;
  }

  public PaymentOrderStatusView paymentOrderId(String paymentOrderId) {
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

  public PaymentOrderStatusView status(PaymentOrderStatus status) {
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

  public PaymentOrderStatusView lastUpdated(java.time.OffsetDateTime lastUpdated) {
    this.lastUpdated = lastUpdated;
    return this;
  }

  /**
   * Timestamp of the last status update (legacy `lastUpdate`).
   * @return lastUpdated
   */
  @NotNull @Valid 
  @JsonProperty("lastUpdated")
  public java.time.OffsetDateTime getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(java.time.OffsetDateTime lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentOrderStatusView paymentOrderStatusView = (PaymentOrderStatusView) o;
    return Objects.equals(this.paymentOrderId, paymentOrderStatusView.paymentOrderId) &&
        Objects.equals(this.status, paymentOrderStatusView.status) &&
        Objects.equals(this.lastUpdated, paymentOrderStatusView.lastUpdated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(paymentOrderId, status, lastUpdated);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentOrderStatusView {\n");
    sb.append("    paymentOrderId: ").append(toIndentedString(paymentOrderId)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    lastUpdated: ").append(toIndentedString(lastUpdated)).append("\n");
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

