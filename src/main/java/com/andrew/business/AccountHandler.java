package com.andrew.business;

import com.andrew.dto.CreateAccount;
import com.andrew.dto.Deposit;
import com.andrew.dto.Transfer;
import com.andrew.dto.Withdraw;
import com.andrew.exception.AccountNotFoundException;
import com.andrew.exception.IllegalTransferException;
import com.andrew.exception.InsufficientBalanceException;
import com.andrew.model.Account;
import com.andrew.repository.AccountRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.Collection;

import static java.util.Objects.requireNonNull;

/**
 * @author andrew
 */
@Singleton
public class AccountHandler {

  @Inject
  private AccountRepository accountRepository;

  /**
   * Create a new account
   *
   * @param createAccount parameters to be used for creating new account
   * @return newly created account
   */
  public Account create(CreateAccount createAccount) {
    requireNonNull(createAccount.getName(), "Account name cannot be null");

    if (createAccount.getOpeningBalance() == null) {
      createAccount.setOpeningBalance(BigDecimal.ZERO);
    } else if (createAccount.getOpeningBalance().signum() < 0) {
      throw new IllegalArgumentException("Opening balance cannot be less than 0");
    }

    return accountRepository.create(createAccount);
  }

  /**
   * Return account based on specified ID.
   *
   * @param id account ID
   * @return account associated with the specified ID
   * @throws AccountNotFoundException when account is not found for specified ID
   */
  public Account findById(Long id) {
    requireNonNull(id, "Account ID cannot be null");

    final Account account = accountRepository.findById(id);
    if (account == null) {
      throw new AccountNotFoundException(String.format("Specified ID: %d not associated with any account", id));
    }

    return account;
  }

  /**
   * Return all registered accounts
   *
   * @return all registered accounts
   */
  public Collection<Account> findAll() {
    return accountRepository.findAll();
  }

  /**
   * Deposit money in account based on specified ID
   *
   * @param deposit parameters for depositing money
   * @return account with updated balance
   * @throws AccountNotFoundException when account is not found for specified ID
   * @throws IllegalArgumentException when amount is not greater than 0
   */
  public Account deposit(Deposit deposit) {
    final BigDecimal amount = deposit.getAmount();
    validateAmount(amount);

    final Account account = findById(deposit.getAccountId());

    synchronized (account) {
      account.setBalance(getUpdatedBalance(account, amount));
    }

    return account;
  }

  /**
   * Withdraw money from account
   *
   * @param withdraw parameters for withdrawing money
   * @return account with updated balance
   * @throws AccountNotFoundException     when account is not found for specified ID
   * @throws IllegalArgumentException     when amount is not greater than 0
   * @throws InsufficientBalanceException when the specified amount is greater than the account balance
   */
  public Account withdraw(Withdraw withdraw) {
    final BigDecimal amount = withdraw.getAmount();
    validateAmount(amount);

    final Account account = findById(withdraw.getAccountId());

    synchronized (account) {
      account.setBalance(getUpdatedBalance(account, amount.negate()));
    }

    return account;
  }

  /**
   * Transfer money between accounts
   *
   * @param transfer parameters for processing money transfer
   * @return benefactor account with updated balance
   * @throws AccountNotFoundException     when account is not found for either benefactor or beneficiary IDs
   * @throws IllegalArgumentException     when amount is not greater than 0
   * @throws IllegalTransferException     when fromAccountId and toAccountId are the same
   * @throws InsufficientBalanceException when the specified amount is greater than the account balance
   */
  public Account transfer(Transfer transfer) {
    final Long fromAccountId = transfer.getFromAccountId();
    final Long toAccountId = transfer.getToAccountId();

    if (fromAccountId.equals(toAccountId)) {
      throw new IllegalTransferException("Cannot transfer money to your account");
    }

    final BigDecimal amount = transfer.getAmount();
    validateAmount(amount);

    final Account fromAccount = findById(fromAccountId);
    final Account toAccount = findById(toAccountId);

    synchronized (fromAccount) {
      synchronized (toAccount) {
        fromAccount.setBalance(getUpdatedBalance(fromAccount, amount.negate()));
        toAccount.setBalance(getUpdatedBalance(toAccount, amount));
      }
    }

    return fromAccount;
  }

  private BigDecimal getUpdatedBalance(Account account, BigDecimal amount) {
    final BigDecimal newBalance = account.getBalance().add(amount);
    if (newBalance.signum() == -1) {
      throw new InsufficientBalanceException("Insufficient balance");
    }

    return newBalance;
  }

  private void validateAmount(BigDecimal amount) {
    requireNonNull(amount, "Amount cannot be null");
    if (amount.signum() < 1) {
      throw new IllegalArgumentException("Amount must be greater than 0");
    }
  }
}