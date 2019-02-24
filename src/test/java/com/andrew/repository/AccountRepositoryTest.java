package com.andrew.repository;

import com.andrew.dto.CreateAccount;
import com.andrew.model.Account;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author andrew
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountRepositoryTest {

  private static AccountRepository sut;

  @BeforeClass
  public static void setUp() {
    sut = new AccountRepository();
  }

  @After
  public void tearDown() {
    sut.clear();
  }

  @Test
  public void createAccount() {
    Account account = sut.create(new CreateAccount("Andrew", BigDecimal.TEN));
    assertNotNull(account);
    assertNotNull(account.getId());
    assertEquals(BigDecimal.TEN, account.getBalance());
    assertEquals("Andrew", account.getName());
  }

  @Test
  public void findById() {
    Account accountOne = sut.create(new CreateAccount("Andrew", BigDecimal.TEN));
    Account accountTwo = sut.create(new CreateAccount("Ivan", null));

    final Account account = sut.findById(accountTwo.getId());
    assertEquals(account, accountTwo);
  }

  @Test
  public void findAll() {
    Account accountOne = sut.create(new CreateAccount("Andrew", BigDecimal.TEN));
    Account accountTwo = sut.create(new CreateAccount("Ivan", null));
    final List<Account> actual = Arrays.asList(accountOne, accountTwo);

    final Collection<Account> expected = sut.findAll();
    assertThat(actual, containsInAnyOrder(expected.toArray()));
  }
}