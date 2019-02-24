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
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author andrew
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountHandlerTest {

  @Mock
  private AccountRepository accountRepository;

  @InjectMocks
  private AccountHandler sut;

  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();

  private final AtomicLong counter = new AtomicLong(1L);

  @Test
  public void createAccount() {
    final CreateAccount newAccount = new CreateAccount("Andrew", BigDecimal.TEN);
    final Account expected = new Account(1L, "Andrew", BigDecimal.TEN);

    when(accountRepository.create(newAccount))
        .thenReturn(expected);

    assertEquals(expected, sut.create(newAccount));
    verify(accountRepository, times(1)).create(newAccount);
  }

  @Test
  public void createAccount_nullOpeningBalance() {
    final CreateAccount newAccount = new CreateAccount("Andrew", null);
    final Account expected = new Account(1L, "Andrew", BigDecimal.ONE);

    when(accountRepository.create(newAccount))
        .thenReturn(expected);

    assertEquals(expected, sut.create(newAccount));
    verify(accountRepository, times(1)).create(newAccount);
  }

  @Test
  public void createAccount_negativeOpeningBalance() {
    exceptionRule.expect(IllegalArgumentException.class);
    exceptionRule.expectMessage("Opening balance cannot be less than 0");

    sut.create(new CreateAccount("Andrew", BigDecimal.TEN.negate()));
    verify(accountRepository, times(0)).create(new CreateAccount());
  }

  @Test
  public void createAccount_nullName() {
    exceptionRule.expect(NullPointerException.class);
    exceptionRule.expectMessage("Account name cannot be null");

    sut.create(new CreateAccount(null, BigDecimal.TEN));
    verify(accountRepository, times(0)).create(new CreateAccount());
  }

  @Test
  public void findById() {
    final Account expected = new Account(1L, "Andrew", BigDecimal.TEN);

    when(accountRepository.findById(1L))
        .thenReturn(expected);

    assertEquals(expected, sut.findById(1L));
    verify(accountRepository, times(1)).findById(1L);
  }

  @Test
  public void findById_invalidId() {
    exceptionRule.expect(AccountNotFoundException.class);
    exceptionRule.expectMessage(String.format("Specified ID: %d not associated with any account", 20L));

    sut.findById(20L);
    verify(accountRepository, times(0)).findById(1L);
  }

  @Test
  public void findAll() {
    final Account accountOne = createAccount("Andrew", null);
    final Account accountTwo = createAccount("Ivan", BigDecimal.ONE);
    final Account[] expected = {accountOne, accountTwo};

    when(accountRepository.findAll())
        .thenReturn(Arrays.asList(expected));

    assertThat(sut.findAll(), containsInAnyOrder(expected));
    verify(accountRepository, times(1)).findAll();
  }

  @Test
  public void deposit() {
    final Account account = createAccount("Andrew", BigDecimal.ONE);
    final Deposit expected = new Deposit(account.getId(), BigDecimal.TEN);
    final Account updatedAccount = sut.deposit(expected);

    assertThat(BigDecimal.valueOf(11), Matchers.equalTo(updatedAccount.getBalance()));
    verify(accountRepository, times(1)).findById(1L);
  }

  @Test
  public void deposit_nullAmount() {
    exceptionRule.expect(NullPointerException.class);
    exceptionRule.expectMessage("Amount cannot be null");

    final Account account = createAccount("Andrew", null);
    final Deposit expected = new Deposit(account.getId(), null);
    sut.deposit(expected);
  }

  @Test
  public void deposit_negativeAmount() {
    exceptionRule.expect(IllegalArgumentException.class);
    exceptionRule.expectMessage("Amount must be greater than 0");

    final Account account = createAccount("Andrew", null);
    final Deposit expected = new Deposit(account.getId(), BigDecimal.ONE.negate());
    sut.deposit(expected);
  }

  @Test
  public void deposit_invalidId() {
    exceptionRule.expect(AccountNotFoundException.class);
    exceptionRule.expectMessage(String.format("Specified ID: %d not associated with any account", 99L));

    final Deposit expected = new Deposit(99L, BigDecimal.ONE);
    sut.deposit(expected);
  }

  @Test
  public void withdraw() {
    final Account account = createAccount("Andrew", BigDecimal.TEN);
    final Withdraw expected = new Withdraw(account.getId(), BigDecimal.ONE);
    final Account updatedAccount = sut.withdraw(expected);

    assertThat(BigDecimal.valueOf(9), Matchers.equalTo(updatedAccount.getBalance()));
    verify(accountRepository, times(1)).findById(1L);
  }

  @Test
  public void withdraw_nullAmount() {
    exceptionRule.expect(NullPointerException.class);
    exceptionRule.expectMessage("Amount cannot be null");

    final Account account = createAccount("Andrew", null);
    final Withdraw expected = new Withdraw(account.getId(), null);
    sut.withdraw(expected);
  }

  @Test
  public void withdraw_negativeAmount() {
    exceptionRule.expect(IllegalArgumentException.class);
    exceptionRule.expectMessage("Amount must be greater than 0");

    final Account account = createAccount("Andrew", null);
    final Withdraw expected = new Withdraw(account.getId(), BigDecimal.ONE.negate());
    sut.withdraw(expected);
  }

  @Test
  public void withdraw_invalidId() {
    exceptionRule.expect(AccountNotFoundException.class);
    exceptionRule.expectMessage(String.format("Specified ID: %d not associated with any account", 99L));

    final Withdraw expected = new Withdraw(99L, BigDecimal.ONE);
    sut.withdraw(expected);
  }

  @Test
  public void transfer() {
    final Account accountOne = createAccount("Andrew", BigDecimal.ONE);
    final Account accountTwo = createAccount("Ivan", BigDecimal.ONE);
    final Account updatedAccount = sut.transfer(new Transfer(accountOne.getId(), accountTwo.getId(), BigDecimal.ONE));

    assertThat(BigDecimal.valueOf(0), Matchers.equalTo(updatedAccount.getBalance()));
  }

  @Test
  public void transfer_sameBenefactorAndBeneficiaryId() {
    exceptionRule.expect(IllegalTransferException.class);
    exceptionRule.expectMessage("Cannot transfer money to your account");

    final Account accountOne = createAccount("Andrew", BigDecimal.ONE);
    sut.transfer(new Transfer(accountOne.getId(), accountOne.getId(), BigDecimal.ONE));
  }

  @Test
  public void transfer_nullAmount() {
    exceptionRule.expect(NullPointerException.class);
    exceptionRule.expectMessage("Amount cannot be null");

    final Account accountOne = createAccount("Andrew", BigDecimal.TEN);
    final Account accountTwo = createAccount("Ivan", null);
    sut.transfer(new Transfer(accountOne.getId(), accountTwo.getId(), null));
  }

  @Test
  public void transfer_negativeAmount() {
    exceptionRule.expect(IllegalArgumentException.class);
    exceptionRule.expectMessage("Amount must be greater than 0");

    final Account accountOne = createAccount("Andrew", BigDecimal.TEN);
    final Account accountTwo = createAccount("Ivan", null);
    sut.transfer(new Transfer(accountOne.getId(), accountTwo.getId(), BigDecimal.ONE.negate()));
  }

  @Test
  public void transfer_insufficientBalance() {
    exceptionRule.expect(InsufficientBalanceException.class);
    exceptionRule.expectMessage("Insufficient balance");

    final Account accountOne = createAccount("Andrew", BigDecimal.ONE);
    final Account accountTwo = createAccount("Ivan", null);
    sut.transfer(new Transfer(accountOne.getId(), accountTwo.getId(), BigDecimal.TEN));
  }

  @Test
  public void transfer_invalidBenefactorId() {
    exceptionRule.expect(AccountNotFoundException.class);
    exceptionRule.expectMessage(String.format("Specified ID: %d not associated with any account", 99L));

    final Account accountTwo = createAccount("Ivan", null);
    sut.transfer(new Transfer(99L, accountTwo.getId(), BigDecimal.ONE));
  }

  @Test
  public void transfer_invalidBeneficiaryId() {
    exceptionRule.expect(AccountNotFoundException.class);
    exceptionRule.expectMessage(String.format("Specified ID: %d not associated with any account", 99L));

    final Account accountOne = createAccount("Andrew", BigDecimal.TEN);
    sut.transfer(new Transfer(accountOne.getId(), 99L, BigDecimal.ONE));
  }

  private Account createAccount(String name, BigDecimal openingBalance) {
    final Long id = counter.getAndIncrement();
    final Account account = new Account(id, name, openingBalance);

    when(accountRepository.findById(id)).thenReturn(account);

    return account;
  }
}