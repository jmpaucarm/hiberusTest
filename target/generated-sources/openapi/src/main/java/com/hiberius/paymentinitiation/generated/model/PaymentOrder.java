package com.hiberius.paymentinitiation.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.hiberius.paymentinitiation.generated.model.AccountReference;
import com.hiberius.paymentinitiation.generated.model.InstructedAmount;
import com.hiberius.paymentinitiation.generated.model.PaymentOrderStatus;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PaymentOrder
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-03-22T21:01:34.541313700-05:00[America/Guayaquil]", comments = "Generator version: 7.10.0")
public class PaymentOrder {

  private String paymentOrderId;

  private String externalReference;

  private AccountReference debtorAccount;

  private AccountReference creditorAccount;

  private InstructedAmount instructedAmount;

  private String remittanceInformation;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private java.time.LocalDate requestedExecutionDate;

  private PaymentOrderStatus status;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.OffsetDateTime createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private java.time.OffsetDateTime statusChangedAt;

  public PaymentOrder() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public PaymentOrder(String paymentOrderId, String externalReference, AccountReference debtorAccount, AccountReference creditorAccount, InstructedAmount instructedAmount, java.time.LocalDate requestedExecutionDate, PaymentOrderStatus status, java.time.OffsetDateTime createdAt, java.time.OffsetDateTime statusChangedAt) {
    this.paymentOrderId = paymentOrderId;
    this.externalReference = externalReference;
    this.debtorAccount = debtorAccount;
    this.creditorAccount = creditorAccount;
    this.instructedAmount = instructedAmount;
    this.requestedExecutionDate = requestedExecutionDate;
    this.status = status;
    this.createdAt = createdAt;
    this.statusChangedAt = statusChangedAt;
  }

  public PaymentOrder paymentOrderId(String paymentOrderId) {
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

  public PaymentOrder externalReference(String externalReference) {
    this.externalReference = externalReference;
    return this;
  }

  /**
   * Get externalReference
   * @return externalReference
   */
  @NotNull @Size(max = 64) 
  @JsonProperty("externalReference")
  public String getExternalReference() {
    return externalReference;
  }

  public void setExternalReference(String externalReference) {
    this.externalReference = externalReference;
  }

  public PaymentOrder debtorAccount(AccountReference debtorAccount) {
    this.debtorAccount = debtorAccount;
    return this;
  }

  /**
   * Get debtorAccount
   * @return debtorAccount
   */
  @NotNull @Valid 
  @JsonProperty("debtorAccount")
  public AccountReference getDebtorAccount() {
    return debtorAccount;
  }

  public void setDebtorAccount(AccountReference debtorAccount) {
    this.debtorAccount = debtorAccount;
  }

  public PaymentOrder creditorAccount(AccountReference creditorAccount) {
    this.creditorAccount = creditorAccount;
    return this;
  }

  /**
   * Get creditorAccount
   * @return creditorAccount
   */
  @NotNull @Valid 
  @JsonProperty("creditorAccount")
  public AccountReference getCreditorAccount() {
    return creditorAccount;
  }

  public void setCreditorAccount(AccountReference creditorAccount) {
    this.creditorAccount = creditorAccount;
  }

  public PaymentOrder instructedAmount(InstructedAmount instructedAmount) {
    this.instructedAmount = instructedAmount;
    return this;
  }

  /**
   * Get instructedAmount
   * @return instructedAmount
   */
  @NotNull @Valid 
  @JsonProperty("instructedAmount")
  public InstructedAmount getInstructedAmount() {
    return instructedAmount;
  }

  public void setInstructedAmount(InstructedAmount instructedAmount) {
    this.instructedAmount = instructedAmount;
  }

  public PaymentOrder remittanceInformation(String remittanceInformation) {
    this.remittanceInformation = remittanceInformation;
    return this;
  }

  /**
   * Get remittanceInformation
   * @return remittanceInformation
   */
  @Size(max = 140) 
  @JsonProperty("remittanceInformation")
  public String getRemittanceInformation() {
    return remittanceInformation;
  }

  public void setRemittanceInformation(String remittanceInformation) {
    this.remittanceInformation = remittanceInformation;
  }

  public PaymentOrder requestedExecutionDate(java.time.LocalDate requestedExecutionDate) {
    this.requestedExecutionDate = requestedExecutionDate;
    return this;
  }

  /**
   * Get requestedExecutionDate
   * @return requestedExecutionDate
   */
  @NotNull @Valid 
  @JsonProperty("requestedExecutionDate")
  public java.time.LocalDate getRequestedExecutionDate() {
    return requestedExecutionDate;
  }

  public void setRequestedExecutionDate(java.time.LocalDate requestedExecutionDate) {
    this.requestedExecutionDate = requestedExecutionDate;
  }

  public PaymentOrder status(PaymentOrderStatus status) {
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

  public PaymentOrder createdAt(java.time.OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
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

  public PaymentOrder statusChangedAt(java.time.OffsetDateTime statusChangedAt) {
    this.statusChangedAt = statusChangedAt;
    return this;
  }

  /**
   * Last time the business status changed (maps conceptually to legacy `lastUpdate`).
   * @return statusChangedAt
   */
  @NotNull @Valid 
  @JsonProperty("statusChangedAt")
  public java.time.OffsetDateTime getStatusChangedAt() {
    return statusChangedAt;
  }

  public void setStatusChangedAt(java.time.OffsetDateTime statusChangedAt) {
    this.statusChangedAt = statusChangedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentOrder paymentOrder = (PaymentOrder) o;
    return Objects.equals(this.paymentOrderId, paymentOrder.paymentOrderId) &&
        Objects.equals(this.externalReference, paymentOrder.externalReference) &&
        Objects.equals(this.debtorAccount, paymentOrder.debtorAccount) &&
        Objects.equals(this.creditorAccount, paymentOrder.creditorAccount) &&
        Objects.equals(this.instructedAmount, paymentOrder.instructedAmount) &&
        Objects.equals(this.remittanceInformation, paymentOrder.remittanceInformation) &&
        Objects.equals(this.requestedExecutionDate, paymentOrder.requestedExecutionDate) &&
        Objects.equals(this.status, paymentOrder.status) &&
        Objects.equals(this.createdAt, paymentOrder.createdAt) &&
        Objects.equals(this.statusChangedAt, paymentOrder.statusChangedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(paymentOrderId, externalReference, debtorAccount, creditorAccount, instructedAmount, remittanceInformation, requestedExecutionDate, status, createdAt, statusChangedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentOrder {\n");
    sb.append("    paymentOrderId: ").append(toIndentedString(paymentOrderId)).append("\n");
    sb.append("    externalReference: ").append(toIndentedString(externalReference)).append("\n");
    sb.append("    debtorAccount: ").append(toIndentedString(debtorAccount)).append("\n");
    sb.append("    creditorAccount: ").append(toIndentedString(creditorAccount)).append("\n");
    sb.append("    instructedAmount: ").append(toIndentedString(instructedAmount)).append("\n");
    sb.append("    remittanceInformation: ").append(toIndentedString(remittanceInformation)).append("\n");
    sb.append("    requestedExecutionDate: ").append(toIndentedString(requestedExecutionDate)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    statusChangedAt: ").append(toIndentedString(statusChangedAt)).append("\n");
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

