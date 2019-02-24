package com.andrew.model;

import java.math.BigDecimal;

/**
 * @author andrew
 */
public class Account {

  private Long id;
  private String name;
  private BigDecimal balance;

  public Account() {
  }

  public Account(Long id, String name, BigDecimal balance) {
    this.id = id;
    this.name = name;
    this.balance = balance;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Account account = (Account) o;

    return id.equals(account.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    return "Account{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", balance=" + balance +
        '}';
  }
}
