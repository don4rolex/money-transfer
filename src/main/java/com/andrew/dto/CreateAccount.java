package com.andrew.dto;

import java.math.BigDecimal;

/**
 * @author andrew
 */
public class CreateAccount {

  private String name;
  private BigDecimal openingBalance;

  //Required for JSON serialization
  public CreateAccount() {
  }

  public CreateAccount(String name, BigDecimal openingBalance) {
    this.name = name;
    this.openingBalance = openingBalance;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getOpeningBalance() {
    return openingBalance;
  }

  public void setOpeningBalance(BigDecimal openingBalance) {
    this.openingBalance = openingBalance;
  }

  @Override
  public String toString() {
    return "CreateAccount{" +
        "name='" + name + '\'' +
        ", openingBalance=" + openingBalance +
        '}';
  }
}
