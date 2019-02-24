package com.andrew;

import com.andrew.controller.AccountController;
import org.jooby.Jooby;
import org.jooby.apitool.ApiTool;
import org.jooby.json.Jackson;

/**
 * @author andrew
 */
public class App extends Jooby {

  public App() {
    use(new Jackson());
    use(AccountController.class);
    use(new ApiTool()
        .swagger()
        .raml());
  }

  public static void main(final String[] args) {
    run(App::new, args);
  }

}
