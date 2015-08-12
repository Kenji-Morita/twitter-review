riot.tag('commonfooter', '<footer class="sg-footer"><div class="sg-container"><p>(c)2015 SAW</p></div></footer>', function(opts) {
});

riot.tag('commonheader', '<header class="sg-header"><div class="sg-container"><ul class="sg-header-contents"><li><h1>SAW Twitter</h1></li><li if="{opts.loginInfo.isLogin}"><a href="#">Setting</a><ul><li><a href="#">Setting</a></li><li><a href="#" onclick="{doSignOut}">Sign out</a></li></ul></li></ul></div></header>', function(opts) {// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
var request = window.superagent;
// ===================================================================================
//                                                                               Event
//                                                                               =====
this.doSignOut = function (e) {
    e.preventDefault();
    request
        .post("/api/auth/signout")
        .withCredentials()
        .end(function (error, response) {
        if (response.ok) {
            location.href = "/";
        }
    });
};

});

riot.tag('follow', '<div if="{opts.loginInfo.isLogin && !isMe}"><button if="{!isFollowing}" onclick="{doFollow}">Follow</button><button if="{isFollowing}" onclick="{doUnFollow}">Unfollow</button></div>', function(opts) {var _this = this;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
var request = window.superagent;
this.isFollowing = false;
this.isMe = true;
if (opts.loginInfo != undefined && opts.loginInfo.isLogin) {
    if (opts.loginInfo.member.following.list.indexOf(opts.memberId) >= 0) {
        this.isFollowing = true;
    }
    this.isMe = opts.loginInfo.member.memberId == opts.memberId;
}
// ===================================================================================
//                                                                               Event
//                                                                               =====
this.doFollow = function (e) {
    e.preventDefault();
    request
        .post("/api/member/follow/" + opts.memberId)
        .withCredentials()
        .end(function (error, response) {
        if (response.ok) {
            _this.isFollowing = true;
            _this.update();
        }
    });
};
this.doUnFollow = function (e) {
    e.preventDefault();
    request
        .del("/api/member/unfollow/" + opts.memberId)
        .withCredentials()
        .end(function (error, response) {
        if (response.ok) {
            _this.isFollowing = false;
            _this.update();
        }
        else {
            console.log(response.text);
        }
    });
};

});

riot.tag('post', '<form onsubmit="{doPostTweet}" class="sg-post-tweet"><p>tweet length: {tweetLength}</p><textarea oninput="{doInputTweet}"></textarea><button __disabled="{textLengthInvalid}">Tweet</button></form>', function(opts) {var _this = this;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
var request = window.superagent;
this.tweetLength = 0;
this.textLengthInvalid = true;
// ===================================================================================
//                                                                               Event
//                                                                               =====
this.doInputTweet = function (e) {
    var textarea = e.target;
    updateTextareaView(textarea.value);
    _this.update();
};
this.doPostTweet = function (e) {
    e.preventDefault();
    var textarea = e.target.querySelectorAll("textarea")[0];
    request
        .post("/api/tweet/tweet")
        .withCredentials()
        .send({ text: textarea.value })
        .set('Accept', 'application/json')
        .end(function (error, response) {
        if (response.ok) {
            textarea.value = "";
            updateTextareaView(textarea.value);
        }
    });
};
// ===================================================================================
//                                                                               Logic
//                                                                               =====
var updateTextareaView = function (text) {
    _this.tweetLength = text.length;
    if (_this.tweetLength > 140 || _this.tweetLength <= 0) {
        _this.textLengthInvalid = true;
    }
    else {
        _this.textLengthInvalid = false;
    }
    _this.update();
};

});

riot.tag('profileaside', '', function(opts) {
});

riot.tag('profileaside', '<aside class="pg-profile"><img src="/assets/icon/1"><h2>{opts.member.displayName}</h2><p>{opts.member.profile.biography}</p><dl><dt>Followings</dt><dd><a href="/following/{opts.member.memberId}">{opts.member.following.count}</a></dd><dt>Followers</dt><dd><a href="/followers/{opts.member.memberId}">{opts.member.followers.count}</a></dd></dl></aside>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======

});

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

riot.tag('timeline', '<ul class="pg-timeline"><li class="pg-timeline-tweet" each="{tweets}"><img src="http://placehold.jp/64x64.png"><h3><a href="/member/{memberId}">screenName</a></h3><p data-tweet-id="{tweetId}">{text}</p><time><a href="/tweet/{tweetId}">{postedAt}</a></time></li></ul>', function(opts) {var _this = this;
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
                    memberId: json.memberId,
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

riot.tag('tweet', '<section><header><img alt="memberIcon" src="/assets/icon/1"><dl><dt>{displayName}</dt><dd>@{screenName}</dd></dl><follow></follow></header><p if="{isRetweet}">{opts.tweet.reTweet.text}</p><p>{opts.tweet.text}</p><footer><time>{opts.tweet.postedAt}</time></footer></section>', function(opts) {// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
this.isRetweet = opts.tweet.reTweet != null;
// ===================================================================================
//                                                                               Event
//                                                                               =====

});
