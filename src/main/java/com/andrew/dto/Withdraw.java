package com.andrew.dto;

import java.math.BigDecimal;

/**
 * @author andrew
 */
public class Withdraw {

  private Long accountId;
  private BigDecimal amount;

  //Required for JSON serialization
  public Withdraw() {
  }

  public Withdraw(Long accountId, BigDecimal amount) {
    this.accountId = accountId;
    this.amount = amount;
  }

  public Long getAccountId() {
    return accountId;
  }

  public void setAccountId(Long accountId) {
    this.accountId = accountId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  @Override
  public String toString() {
    return "Deposit{" +
        "accountId=" + accountId +
        ", amount=" + amount +
        '}';
  }
}
