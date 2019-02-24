package com.andrew.controller;

import com.andrew.App;
import com.andrew.dto.CreateAccount;
import com.andrew.dto.Deposit;
import com.andrew.dto.Transfer;
import com.andrew.dto.Withdraw;
import com.andrew.model.Account;
import com.andrew.repository.AccountRepository;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.http.ContentType;
import io.restassured.mapper.ObjectMapperType;
import org.jooby.Status;
import org.jooby.test.JoobyRule;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author andrew
 */
public class AccountControllerIT {

  private static final App app = new App();

  @ClassRule
  public static JoobyRule joobyRule = new JoobyRule(app);

  @BeforeClass
  public static void setUp() {
    RestAssured.basePath = "/account";

    RestAssured.requestSpecification = new RequestSpecBuilder()
        .setContentType(ContentType.JSON)
        .setAccept(ContentType.JSON)
        .build();

    RestAssured.config().objectMapperConfig(new ObjectMapperConfig(ObjectMapperType.JACKSON_2));
  }

  @After
  public void tearDown() {
    app.require(AccountRepository.class).clear();
  }

  @Test
  public void createAccount() {
    given()
        .body(new CreateAccount("Andrew", BigDecimal.TEN))
        .when()
        .post()
        .then()
        .statusCode(Status.OK.value())
        .body("balance", equalTo(10))
        .body("name", equalTo("Andrew"));
  }

  @Test
  public void createAccount_nullOpeningBalance() {
    given()
        .body(new CreateAccount("Andrew", null))
        .when()
        .post()
        .then()
        .statusCode(Status.OK.value())
        .body("balance", equalTo(0))
        .body("name", equalTo("Andrew"));
  }

  @Test
  public void createAccount_negativeOpeningBalance() {
    given()
        .body(new CreateAccount("Andrew", BigDecimal.TEN.negate()))
        .when()
        .post()
        .then()
        .statusCode(Status.BAD_REQUEST.value());
  }

  @Test
  public void createAccount_nullName() {
    given()
        .body(new CreateAccount(null, null))
        .when()
        .post()
        .then()
        .statusCode(Status.BAD_REQUEST.value());
  }

  @Test
  public void findById() {
    final Account accountOne = createAccount("Andrew", null);
    final Account accountTwo = createAccount("Ivan", BigDecimal.ONE);

    final Account expected = when()
        .get("/{id}", accountTwo.getId())
        .then()
        .statusCode(Status.OK.value())
        .extract()
        .body().as(Account.class);

    assertEquals(expected, accountTwo);
  }

  @Test
  public void findById_invalidId() {
    when()
        .get("/{id}", 10)
        .then()
        .statusCode(Status.BAD_REQUEST.value());
  }

  @Test
  public void findAll() {
    final Account accountOne = createAccount("Andrew", null);
    final Account accountTwo = createAccount("Ivan", BigDecimal.ONE);
    final List<Account> actual = Arrays.asList(accountOne, accountTwo);

    final Account[] expected = when()
        .get()
        .then()
        .statusCode(Status.OK.value())
        .extract()
        .body()
        .as(Account[].class);

    assertNotNull(expected);
    assertThat(actual, containsInAnyOrder(expected));
  }

  @Test
  public void deposit() {
    final Account account = createAccount("Andrew", null);

    given()
        .body(new Deposit(account.getId(), BigDecimal.TEN))
        .when()
        .post("/deposit")
        .then()
        .statusCode(Status.OK.value())
        .body("balance", equalTo(10))
        .body("name", equalTo("Andrew"));
  }

  @Test
  public void deposit_nullAmount() {
    final Account account = createAccount("Andrew", null);

    given()
        .body(new Deposit(account.getId(), null))
        .when()
        .post("/deposit")
        .then()
        .statusCode(Status.BAD_REQUEST.value());
  }

  @Test
  public void deposit_negativeAmount() {
    final Account account = createAccount("Andrew", null);

    given()
        .body(new Deposit(account.getId(), BigDecimal.TEN.negate()))
        .when()
        .post("/deposit")
        .then()
        .statusCode(Status.BAD_REQUEST.value());
  }

  @Test
  public void deposit_invalidId() {
    given()
        .body(new Deposit(99L, BigDecimal.TEN))
        .when()
        .post("/deposit")
        .then()
        .statusCode(Status.BAD_REQUEST.value());
  }

  @Test
  public void withdraw() {
    final Account account = createAccount("Andrew", BigDecimal.TEN);

    given()
        .body(new Withdraw(account.getId(), BigDecimal.ONE))
        .when()
        .post("/withdraw")
        .then()
        .statusCode(Status.OK.value())
        .body("balance", equalTo(9))
        .body("name", equalTo("Andrew"));

    final Account[] expected = when()
        .get()
        .then()
        .statusCode(Status.OK.value())
        .extract()
        .body()
        .as(Account[].class);

    for (Account acc : expected) {
      System.out.println(acc);
    }
  }

  @Test
  public void withdraw_nullAmount() {
    final Account account = createAccount("Andrew", null);

    given()
        .body(new Withdraw(account.getId(), null))
        .when()
        .post("/withdraw")
        .then()
        .statusCode(Status.BAD_REQUEST.value());
  }

  @Test
  public void withdraw_negativeAmount() {
    final Account account = createAccount("Andrew", null);

    given()
        .body(new Withdraw(account.getId(), BigDecimal.TEN.negate()))
        .when()
        .post("/withdraw")
        .then()
        .statusCode(Status.BAD_REQUEST.value());
  }

  @Test
  public void withdraw_invalidId() {
    given()
        .body(new Withdraw(99L, BigDecimal.ONE))
        .when()
        .post("/withdraw")
        .then()
        .statusCode(Status.BAD_REQUEST.value());
  }

  @Test
  public void transfer() {
    final Account accountOne = createAccount("Andrew", BigDecimal.TEN);
    final Account accountTwo = createAccount("Ivan", null);

    given()
        .body(new Transfer(accountOne.getId(), accountTwo.getId(), BigDecimal.ONE))
        .when()
        .post("/transfer")
        .then()
        .statusCode(Status.OK.value())
        .body("balance", equalTo(9))
        .body("name", equalTo("Andrew"));

    when()
        .get("/{id}", accountTwo.getId())
        .then()
        .statusCode(Status.OK.value())
        .body("balance", equalTo(1))
        .body("name", equalTo("Ivan"));
  }

  @Test
  public void transfer_sameBenefactorAndBeneficiaryId() {
    final Account accountOne = createAccount("Andrew", BigDecimal.TEN);

    given()
        .body(new Transfer(accountOne.getId(), accountOne.getId(), BigDecimal.ONE))
        .when()
        .post("/transfer")
        .then()
        .statusCode(Status.BAD_REQUEST.value());
  }

  @Test
  public void transfer_nullAmount() {
    final Account accountOne = createAccount("Andrew", BigDecimal.TEN);
    final Account accountTwo = createAccount("Ivan", null);

    given()
        .body(new Transfer(accountOne.getId(), accountTwo.getId(), null))
        .when()
        .post("/transfer")
        .then()
        .statusCode(Status.BAD_REQUEST.value());
  }

  @Test
  public void transfer_negativeAmount() {
    final Account accountOne = createAccount("Andrew", BigDecimal.TEN);
    final Account accountTwo = createAccount("Ivan", null);

    given()
        .body(new Transfer(accountOne.getId(), accountTwo.getId(), null))
        .when()
        .post("/transfer")
        .then()
        .statusCode(Status.BAD_REQUEST.value());
  }

  @Test
  public void transfer_invalidBenefactorId() {
    final Account accountTwo = createAccount("Ivan", null);

    given()
        .body(new Transfer(99L, accountTwo.getId(), BigDecimal.ONE))
        .when()
        .post("/transfer")
        .then()
        .statusCode(Status.BAD_REQUEST.value());
  }

  @Test
  public void transfer_invalidBeneficiaryId() {
    final Account accountOne = createAccount("Andrew", BigDecimal.TEN);

    given()
        .body(new Transfer(accountOne.getId(), 99L, BigDecimal.ONE))
        .when()
        .post("/transfer")
        .then()
        .statusCode(Status.BAD_REQUEST.value());
  }

  @Test
  public void transfer_insufficientBalance() {
    final Account accountOne = createAccount("Andrew", BigDecimal.ONE);
    final Account accountTwo = createAccount("Ivan", null);

    given()
        .body(new Transfer(accountOne.getId(), accountTwo.getId(), BigDecimal.TEN))
        .when()
        .post("/transfer")
        .then()
        .statusCode(Status.BAD_REQUEST.value());
  }

  private Account createAccount(String name, BigDecimal openingBalance) {

    return given()
        .body(new CreateAccount(name, openingBalance))
        .when()
        .post()
        .then()
        .statusCode(Status.OK.value())
        .extract().body().as(Account.class);
  }
}