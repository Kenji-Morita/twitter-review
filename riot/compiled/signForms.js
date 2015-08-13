riot.tag('signforms', '<ul><li if="{!toggleState}"><a href="#" onclick="{toggle}">Sign in</a></li><li if="{toggleState}"><a href="#" onclick="{toggle}">Sign up</a></li></ul><form class="pg-sign-in" if="{toggleState}" onsubmit="{doSignIn}"><label if="{signIn.account.isEmpty}">Please input Mail Address or Name!</label><input type="text" name="account" placeholder="Mail address or Name"><label if="{signIn.password.isEmpty}">Please input Password!</label><input type="password" name="signInPassword" placeholder="Password"><button>Sign in</button></form><form class="pg-sign-up" if="{!toggleState}" onsubmit="{doSignUp}"><label if="{signUp.screenName.isEmpty}">Please input Account Name!</label><input type="text" name="screenName" placeholder="Account Name"><label if="{signUp.displayName.isEmpty}">Please input Display Name!</label><input type="text" name="displayName" placeholder="Display Name"><label if="{signUp.mail.isEmpty}">Please input Mail Address!</label><input type="mail" name="mail" placeholder="Mail address"><label if="{signUp.password.isEmpty}">Please input Password!</label><input type="password" name="signUpPassword" placeholder="Password"><label if="{signUp.passwordConfirm.isEmpty}">Please input Password again!</label><input type="password" name="signUpPasswordConfirm" placeholder="Password confirm"><button>Sign up</button></form>', function(opts) {var _this = this;
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
};
// ===================================================================================
//                                                                               Event
//                                                                               =====
this.toggle = function (e) {
    e.preventDefault();
    // TODO demo
    var sign = document.querySelector(".pg-sign");
    sign.classList.toggle("hoge");
    _this.toggleState = !_this.toggleState;
};
this.doSignIn = function (e) {
    e.preventDefault();
    var account = _this.account.value.trim();
    var password = _this.signInPassword.value.trim();
    // empty validate
    _this.signIn.account.isEmpty = account == "";
    _this.signIn.password.isEmpty = password == "";
    if (_this.signIn.account.isEmpty || _this.signIn.password.isEmpty) {
        return;
    }
    // sign in
    request
        .post("api/auth/signin")
        .withCredentials()
        .send({ account: account, password: password })
        .set('Accept', 'application/json')
        .end(function (error, response) {
        if (response.ok) {
            location.reload();
        }
        else {
            var result = JSON.parse(response.text);
            console.log(result.reason);
        }
    });
};
this.doSignUp = function (e) {
    e.preventDefault();
    var screenName = _this.screenName.value.trim();
    var displayName = _this.displayName.value.trim();
    var mail = _this.mail.value.trim();
    var password = _this.signUpPassword.value.trim();
    var passwordConfirm = _this.signUpPasswordConfirm.value.trim();
    // empty validate
    _this.signUp.screenName.isEmpty = screenName == "";
    _this.signUp.displayName.isEmpty = displayName == "";
    _this.signUp.mail.isEmpty = mail == "";
    _this.signUp.password.isEmpty = password == "";
    _this.signUp.passwordConfirm.isEmpty = passwordConfirm == "";
    if (_this.signUp.screenName.isEmpty || _this.signUp.displayName.isEmpty || _this.signUp.mail.isEmpty || _this.signUp.password.isEmpty || _this.signUp.passwordConfirm.isEmpty) {
        return;
    }
    // sign up
    request
        .post("api/auth/signup")
        .send({ screenName: screenName, displayName: displayName, mail: mail, password: password, passwordConfirm: passwordConfirm })
        .set('Accept', 'application/json')
        .end(function (error, response) {
        if (response.ok) {
            location.reload();
        }
    });
};

});
