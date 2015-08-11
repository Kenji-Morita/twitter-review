<signforms>
  <ul>
      <li if={!toggleState}>
          <a href="#" onclick={toggle}>Sign in</a>
      </li>
      <li if={toggleState}>
          <a href="#" onclick={toggle}>Sign up</a>
      </li>
  </ul>
  <form class="pg-sign-in" if={toggleState} onsubmit={doSignIn}>
      <label if={signIn.account.isEmpty}>Please input Mail Address or Name!</label>
      <input type="text" name="account" placeholder="Mail address or Name">
      <label if={signIn.password.isEmpty}>Please input Password!</label>
      <input type="password" name="signInPassword" placeholder="Password">
      <button>Sign in</button>
  </form>
  <form class="pg-sign-up" if={!toggleState} onsubmit={doSignUp}>
      <label if={signUp.screenName.isEmpty}>Please input Account Name!</label>
      <input type="text" name="screenName" placeholder="Account Name">
      <label if={signUp.displayName.isEmpty}>Please input Display Name!</label>
      <input type="text" name="displayName" placeholder="Display Name">
      <label if={signUp.mail.isEmpty}>Please input Mail Address!</label>
      <input type="mail" name="mail" placeholder="Mail address">
      <label if={signUp.password.isEmpty}>Please input Password!</label>
      <input type="password" name="signUpPassword" placeholder="Password">
      <label if={signUp.passwordConfirm.isEmpty}>Please input Password again!</label>
      <input type="password" name="signUpPasswordConfirm" placeholder="Password confirm">
      <button>Sign up</button>
  </form>

  <script>
    // ===================================================================================
    //                                                                             Declare
    //                                                                             =======
    interface Window {
      superagent: any;
    }

    // ===================================================================================
    //                                                                          Attributes
    //                                                                          ==========
    var request = window.superagent;

    this.toggleState = true;

    this.signIn = {
      account: {
        isEmpty: false
      },
      password: {
        isEmpty: false
      }
    };

    this.signUp = {
      screenName: {
        isEmpty: false
      },
      displayName: {
        isEmpty: false
      },
      mail: {
        isEmpty: false
      },
      password: {
        isEmpty: false
      },
      passwordConfirm: {
        isEmpty: false
      }
    }

    // ===================================================================================
    //                                                                               Event
    //                                                                               =====
    this.toggle = e => {
      e.preventDefault();
      this.toggleState = !this.toggleState;
    }

    this.doSignIn = e => {
      e.preventDefault();
      var account = this.account.value.trim();
      var password = this.signInPassword.value.trim();

      // empty validate
      this.signIn.account.isEmpty = account == "";
      this.signIn.password.isEmpty = password == "";
      if (this.signIn.account.isEmpty || this.signIn.password.isEmpty) {
        return;
      }

      // sign in
      request
        .post("api/auth/signin")
        .withCredentials()
        .send({screenName: account, mail: account, password: password})
        .set('Accept', 'application/json')
        .end((error, response) => {
          if (response.ok) {
            location.reload();
          } else {
            var result = JSON.parse(response.text);
            console.log(result.reason);
          }
        });
    }

    this.doSignUp = e => {
      e.preventDefault();
      var screenName = this.screenName.value.trim();
      var displayName = this.displayName.value.trim();
      var mail = this.mail.value.trim();
      var password = this.signUpPassword.value.trim();
      var passwordConfirm = this.signUpPasswordConfirm.value.trim();

      // empty validate
      this.signUp.screenName.isEmpty = screenName == "";
      this.signUp.displayName.isEmpty = displayName == "";
      this.signUp.mail.isEmpty = mail == "";
      this.signUp.password.isEmpty = password == "";
      this.signUp.passwordConfirm.isEmpty = passwordConfirm == "";

      if (this.signUp.screenName.isEmpty || this.signUp.displayName.isEmpty || this.signUp.mail.isEmpty || this.signUp.password.isEmpty || this.signUp.passwordConfirm.isEmpty) {
        return;
      }

      // sign up
      request
        .post("api/auth/signup")
        .send({screenName: screenName, displayName: displayName, mail: mail, password: password, passwordConfirm: passwordConfirm})
        .set('Accept', 'application/json')
        .end((error, response) => {
          if (response.ok) {
            location.reload();
          }
        })
    }
  </script>
</signforms>
