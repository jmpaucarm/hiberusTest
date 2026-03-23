package com.hiberius.paymentinitiation.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.hiberius.paymentinitiation.generated.model.AccountReference;
import com.hiberius.paymentinitiation.generated.model.InstructedAmount;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PaymentOrderInitiationRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-03-22T21:01:34.541313700-05:00[America/Guayaquil]", comments = "Generator version: 7.10.0")
public class PaymentOrderInitiationRequest {

  private String externalReference;

  private AccountReference debtorAccount;

  private AccountReference creditorAccount;

  private InstructedAmount instructedAmount;

  private String remittanceInformation;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private java.time.LocalDate requestedExecutionDate;

  public PaymentOrderInitiationRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public PaymentOrderInitiationRequest(String externalReference, AccountReference debtorAccount, AccountReference creditorAccount, InstructedAmount instructedAmount, java.time.LocalDate requestedExecutionDate) {
    this.externalReference = externalReference;
    this.debtorAccount = debtorAccount;
    this.creditorAccount = creditorAccount;
    this.instructedAmount = instructedAmount;
    this.requestedExecutionDate = requestedExecutionDate;
  }

  public PaymentOrderInitiationRequest externalReference(String externalReference) {
    this.externalReference = externalReference;
    return this;
  }

  /**
   * Client-supplied correlation reference (legacy `externalId`).
   * @return externalReference
   */
  @NotNull @Pattern(regexp = "^[\\x20-\\x7E]+$") @Size(min = 1, max = 64) 
  @JsonProperty("externalReference")
  public String getExternalReference() {
    return externalReference;
  }

  public void setExternalReference(String externalReference) {
    this.externalReference = externalReference;
  }

  public PaymentOrderInitiationRequest debtorAccount(AccountReference debtorAccount) {
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

  public PaymentOrderInitiationRequest creditorAccount(AccountReference creditorAccount) {
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

  public PaymentOrderInitiationRequest instructedAmount(InstructedAmount instructedAmount) {
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

  public PaymentOrderInitiationRequest remittanceInformation(String remittanceInformation) {
    this.remittanceInformation = remittanceInformation;
    return this;
  }

  /**
   * Unstructured remittance / narrative (optional).
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

  public PaymentOrderInitiationRequest requestedExecutionDate(java.time.LocalDate requestedExecutionDate) {
    this.requestedExecutionDate = requestedExecutionDate;
    return this;
  }

  /**
   * Requested execution date for the payment (ISO 8601 calendar date).
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentOrderInitiationRequest paymentOrderInitiationRequest = (PaymentOrderInitiationRequest) o;
    return Objects.equals(this.externalReference, paymentOrderInitiationRequest.externalReference) &&
        Objects.equals(this.debtorAccount, paymentOrderInitiationRequest.debtorAccount) &&
        Objects.equals(this.creditorAccount, paymentOrderInitiationRequest.creditorAccount) &&
        Objects.equals(this.instructedAmount, paymentOrderInitiationRequest.instructedAmount) &&
        Objects.equals(this.remittanceInformation, paymentOrderInitiationRequest.remittanceInformation) &&
        Objects.equals(this.requestedExecutionDate, paymentOrderInitiationRequest.requestedExecutionDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(externalReference, debtorAccount, creditorAccount, instructedAmount, remittanceInformation, requestedExecutionDate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentOrderInitiationRequest {\n");
    sb.append("    externalReference: ").append(toIndentedString(externalReference)).append("\n");
    sb.append("    debtorAccount: ").append(toIndentedString(debtorAccount)).append("\n");
    sb.append("    creditorAccount: ").append(toIndentedString(creditorAccount)).append("\n");
    sb.append("    instructedAmount: ").append(toIndentedString(instructedAmount)).append("\n");
    sb.append("    remittanceInformation: ").append(toIndentedString(remittanceInformation)).append("\n");
    sb.append("    requestedExecutionDate: ").append(toIndentedString(requestedExecutionDate)).append("\n");
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

