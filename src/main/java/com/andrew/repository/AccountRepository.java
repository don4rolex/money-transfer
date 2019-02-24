package com.andrew.repository;

import com.andrew.dto.CreateAccount;
import com.andrew.model.Account;
import com.google.common.annotations.VisibleForTesting;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author andrew
 */
@Singleton
public class AccountRepository {

  private final Map<Long, Account> accountMap = new ConcurrentHashMap<>();
  private final AtomicLong counter = new AtomicLong(1L);

  public Account create(CreateAccount createAccount) {
    final Long id = counter.getAndIncrement();
    final Account account = new Account(id, createAccount.getName(), createAccount.getOpeningBalance());
    accountMap.put(id, account);

    return account;
  }

  public Account findById(Long id) {
    return accountMap.get(id);
  }

  public Collection<Account> findAll() {
    return Collections.unmodifiableCollection(accountMap.values());
  }

  @VisibleForTesting
  public void clear(){
    accountMap.clear();
  }
}
