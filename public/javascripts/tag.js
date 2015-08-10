riot.tag('commonfooter', '<footer class="sg-footer"><div class="sg-container"><p>(c)2015 SAW</p></div></footer>', function(opts) {
});

riot.tag('commonheader', '<header class="sg-header"><div class="sg-container"><ul class="sg-header-contents"><li><h1>SAW Twitter</h1></li><li if="{opts.isLogin}"><a href="#">Setting</a><ul><li><a href="#">Setting</a></li><li><a href="#" onclick="{doSignOut}">Sign out</a></li></ul></li></ul></div></header>', function(opts) {// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
var request = window.superagent;
// ===================================================================================
//                                                                               Event
//                                                                               =====
this.doSignOut = function (e) {
    e.preventDefault();
    request
        .post("api/auth/signout")
        .withCredentials()
        .end(function (error, response) {
        if (response.ok) {
            location.reload();
        }
    });
};
// ===================================================================================
//                                                                               Mixin
//                                                                               =====
this.mixin({
    observable: riot.observable(),
});

});

riot.tag('signforms', '<ul><li if="{!toggleState}"><a href="#" onclick="{toggle}">Sign in</a></li><li if="{toggleState}"><a href="#" onclick="{toggle}">Sign up</a></li></ul><form class="pg-sign-in" if="{toggleState}" onsubmit="{doSignIn}"><label if="{signIn.account.isEmpty}">Please input Mail Address or Name!</label><input type="text" name="account" placeholder="Mail address or Name"><label if="{signIn.password.isEmpty}">Please input Password!</label><input type="password" name="signInPassword" placeholder="Password"><button>Sign in</button></form><form class="pg-sign-up" if="{!toggleState}" onsubmit="{doSignUp}"><label if="{signUp.screenName.isEmpty}">Please input Account Name!</label><input type="text" name="screenName" placeholder="Account Name"><label if="{signUp.mail.isEmpty}">Please input Mail Address!</label><input type="mail" name="mail" placeholder="Mail address"><label if="{signUp.password.isEmpty}">Please input Password!</label><input type="password" name="signUpPassword" placeholder="Password"><label if="{signUp.passwordConfirm.isEmpty}">Please input Password again!</label><input type="password" name="signUpPasswordConfirm" placeholder="Password confirm"><button>Sign up</button></form>', function(opts) {var _this = this;
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
        .send({ screenName: account, mail: account, password: password })
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
    var mail = _this.mail.value.trim();
    var password = _this.signUpPassword.value.trim();
    var passwordConfirm = _this.signUpPasswordConfirm.value.trim();
    // empty validate
    _this.signUp.screenName.isEmpty = screenName == "";
    _this.signUp.mail.isEmpty = mail == "";
    _this.signUp.password.isEmpty = password == "";
    _this.signUp.passwordConfirm.isEmpty = passwordConfirm == "";
    if (_this.signUp.screenName.isEmpty || _this.signUp.mail.isEmpty || _this.signUp.password.isEmpty || _this.signUp.passwordConfirm.isEmpty) {
        return;
    }
    // TODO SAW sign up
};

});

riot.tag('timeline', '<ul class="pg-timeline"><li class="pg-timeline-tweet" each="{tweets}"><img src="http://placehold.jp/64x64.png"><h3><a href="#">screenName</a></h3><p data-tweet-id="{tweetId}">{text}</p><time><a href="#">{postedAt}</a></time></li></ul>', function(opts) {var _this = this;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
var request = window.superagent;
this.tweets = [];
// ===================================================================================
//                                                                               Logic
//                                                                               =====
var loadTweets = function () {
    var url = "/api/timeline/" + opts.target;
    if (_this.tweets.length > 0) {
        url += "?after=" + _this.tweets[0].timestamp;
    }
    request
        .get(url)
        .withCredentials()
        .end(function (error, response) {
        if (response.ok) {
            var result = JSON.parse(response.text);
            _this.tweets = _
                .chain(result.value.tweets)
                .map(function (json) {
                return {
                    tweetId: json.tweetId,
                    text: json.text,
                    postedAt: json.postedAt,
                    timestamp: json.timestamp
                };
            })
                .concat(_this.tweets)
                .value();
            _this.update();
        }
    });
    setTimeout(loadTweets, 10000);
};
loadTweets();

});
