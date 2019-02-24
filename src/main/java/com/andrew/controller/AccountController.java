package com.andrew.controller;

import com.andrew.business.AccountHandler;
import com.andrew.dto.CreateAccount;
import com.andrew.dto.Deposit;
import com.andrew.dto.Transfer;
import com.andrew.dto.Withdraw;
import com.andrew.model.Account;
import org.jooby.mvc.Body;
import org.jooby.mvc.GET;
import org.jooby.mvc.POST;
import org.jooby.mvc.Path;

import javax.inject.Inject;
import java.util.Collection;

/**
 * @author andrew
 */
@Path("/account")
public class AccountController {

  @Inject
  private AccountHandler accountHandler;

  @POST
  public Account create(@Body CreateAccount createAccount) {
    return accountHandler.create(createAccount);
  }

  @GET
  @Path("/:id")
  public Account findById(Long id) {
    return accountHandler.findById(id);
  }

  @GET
  @Path("/")
  public Collection<Account> findAll() {
    return accountHandler.findAll();
  }

  @POST
  @Path("/deposit")
  public Account deposit(@Body Deposit deposit) {
    return accountHandler.deposit(deposit);
  }

  @POST
  @Path("/withdraw")
  public Account withdraw(@Body Withdraw withdraw) {
    return accountHandler.withdraw(withdraw);
  }

  @POST
  @Path("/transfer")
  public Account transfer(@Body Transfer transfer) {
    return accountHandler.transfer(transfer);
  }
}
