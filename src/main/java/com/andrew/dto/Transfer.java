package com.andrew.dto;

import java.math.BigDecimal;

/**
 * @author andrew
 */
public class Transfer {

  private Long fromAccountId;
  private Long toAccountId;
  private BigDecimal amount;

  //Required for JSON serialization
  public Transfer() {
  }

  public Transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
    this.fromAccountId = fromAccountId;
    this.toAccountId = toAccountId;
    this.amount = amount;
  }

  public Long getFromAccountId() {
    return fromAccountId;
  }

  public Long getToAccountId() {
    return toAccountId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  @Override
  public String toString() {
    return "Transfer{" +
        "fromAccountId=" + fromAccountId +
        ", toAccountId=" + toAccountId +
        ", amount=" + amount +
        '}';
  }
}
