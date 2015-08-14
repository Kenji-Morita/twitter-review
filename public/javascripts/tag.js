riot.tag('commonfooter', '<footer class="sg-footer"><div class="sg-container"><p>(c)2015 SAW</p></div></footer>', function(opts) {
});

riot.tag('commonheader', '<header class="sg-header"><ul class="sg-header-contents"><li><h1><a href="/">SAW Twitter</a></h1></li><li class="sg-header-post" if="{opts.isLogin}"><postshare></postshare></li><li class="sg-header-menu" if="{opts.isLogin}"><a href="#" onclick="{doSignOut}">Sign out</a></li></ul></header>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
var request = window.superagent;
this.member = null;
this.isLogin = opts.isLogin;
this.observable = riot.observable();
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
// ===================================================================================
//                                                                               Logic
//                                                                               =====
var memberKeyPrefix = "member-";
this.findMemberDetail = function (memberId) {
    var existsData = localStorage.getItem(memberKeyPrefix + memberId);
    if (existsData) {
        _this.member = JSON.parse(existsData);
        _this.observable.trigger("onLoadMember", _this.member);
    }
    else {
        request
            .get("/api/member/detail/" + memberId)
            .end(function (error, response) {
            if (response.ok) {
                _this.member = JSON.parse(response.text).value;
                _this.observable.trigger("onLoadMember", _this.member);
                localStorage.setItem(memberKeyPrefix + memberId, JSON.stringify(_this.member));
            }
        });
    }
};
this.findMemberDetailList = function (memberIds) {
    var unknownIds = _
        .chain(memberIds)
        .uniq()
        .filter(function (memberId) {
        var existsData = localStorage.getItem(memberKeyPrefix + memberId);
        return !existsData;
    })
        .value();
    // request
    if (unknownIds.length > 0) {
        request
            .post("/api/member/details")
            .send({ memberIdList: unknownIds })
            .end(function (error, response) {
            if (response.ok) {
                var memberJsons = JSON.parse(response.text).value;
                memberJsons.forEach(function (json) {
                    var jsonStr = JSON.stringify(json);
                    localStorage.setItem(memberKeyPrefix + json.memberId, jsonStr);
                });
            }
        });
    }
    // event fire
    var memberDetailList = _
        .chain(memberIds)
        .uniq()
        .map(function (memberId) {
        return localStorage.getItem(memberKeyPrefix + memberId);
    })
        .value();
    _this.observable.trigger("onLoadMemberList", memberDetailList);
};
this.findLoginMemberDetail = function () {
    if (!opts.isLogin) {
        return;
    }
    var loginMemberKey = "loginMember";
    var existsData = localStorage.getItem(loginMemberKey);
    if (existsData) {
        _this.loginMember = JSON.parse(existsData);
        _this.observable.trigger("onLoadLoginMember", _this.loginMember);
    }
    else {
        request
            .get("/api/auth/member/detail")
            .withCredentials()
            .end(function (error, response) {
            if (response.ok) {
                _this.loginMember = JSON.parse(response.text).value;
                _this.observable.trigger("onLoadLoginMember", _this.loginMember);
                var loginMemberJsonStr = JSON.stringify(_this.loginMember);
                localStorage.setItem(loginMemberKey, loginMemberJsonStr);
                localStorage.setItem(memberKeyPrefix + _this.loginMember.memberId, loginMemberJsonStr);
            }
        });
    }
};
this.findTweetDetail = function (tweetId) {
    request
        .get("/api/tweet/detail/" + tweetId)
        .end(function (error, response) {
        if (response.ok) {
            var result = JSON.parse(response.text);
            _this.tweet = result.value;
            _this.observable.trigger("onLoadTweet", _this.tweet);
        }
    });
};
this.findTimeline = function (memberId, before, after) {
    // set target
    var url = "/api/timeline/";
    if (memberId == null) {
        url += "home";
    }
    else {
        url += "member/" + memberId;
    }
    // set url parameter
    var existsParameter = false;
    var addParameter = function (key, value) {
        if (!existsParameter) {
            url += "?";
            existsParameter = true;
        }
        else {
            url += "&";
        }
        url += key + "=" + value;
    };
    if (before != null) {
        addParameter("before", before);
    }
    if (after != null) {
        addParameter("after", after);
    }
    // request timeline
    request
        .get(url)
        .withCredentials()
        .end(function (error, response) {
        if (response.ok) {
            var timeline = JSON.parse(response.text).value;
            _this.observable.trigger("onLoadTimeline", timeline);
        }
    });
};
// ===================================================================================
//                                                                               Mixin
//                                                                               =====
this.mixin({
    timeline: {
        targetId: null
    }
});

});

riot.tag('follow', '<div if="{opts.isLogin && !isMe}"><button if="{!isFollowing}" onclick="{doFollow}">Follow</button><button if="{isFollowing}" onclick="{doUnFollow}">Unfollow</button></div>', function(opts) {var _this = this;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
var request = window.superagent;
this.isFollowing = false;
this.isMe = false;
// ===================================================================================
//                                                                               Event
//                                                                               =====
if (opts.observable != undefined) {
    opts.observable.on("onLoadMember", function (member) {
        opts.member = member;
        setupFollowStatus();
    });
    opts.observable.on("onLoadLoginMember", function (loginMember) {
        opts.loginMember = loginMember;
        setupFollowStatus();
    });
}
this.doFollow = function (e) {
    e.preventDefault();
    request
        .post("/api/member/follow/" + opts.member.memberId)
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
        .del("/api/member/unfollow/" + opts.member.memberId)
        .withCredentials()
        .end(function (error, response) {
        if (response.ok) {
            _this.isFollowing = false;
            _this.update();
        }
    });
};
// ===================================================================================
//                                                                               Logic
//                                                                               =====
var setupFollowStatus = function () {
    if (opts.isLogin && opts.loginMember != undefined && opts.member != undefined) {
        if (opts.loginMember.following.list.indexOf(opts.member.memberId) >= 0) {
            _this.isFollowing = true;
        }
        _this.isMe = opts.loginMember.memberId == opts.member.memberId;
        _this.update();
    }
};

});

riot.tag('post-share', '', function(opts) {
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
            opts.observable.trigger("onPost");
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

riot.tag('postshare', '<form onsubmit="{doPost}" class="{sg-header-post-inputting: isShowComment}"><input type="text" placeholder="share URL" oninput="{doInputURL}"><textarea placeholder="with comment (option)" oninput="{doInputComment}" class="{sg-header-post-inputting: isShowComment}"></textarea><div class="{sg-header-post-submit-inputting: isShowComment}"><p class="{sg-header-post-submit-over: commentLength > 140}">{commentLength}</p><button>Post</button></div></form>', function(opts) {// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
var _this = this;
this.commentLength = 0;
this.isShowComment = false;
// ===================================================================================
//                                                                               Event
//                                                                               =====
this.doInputURL = function (e) {
    var url = e.target.value;
    if (url.length > 0) {
        _this.isShowComment = true;
        _this.update();
    }
    else {
        _this.isInputWait = false;
        var form = e.srcElement.parentElement;
        var textarea = form.querySelector("textarea");
        var submitBox = form.querySelector("div");
        textarea.classList.add("sg-header-post-removing");
        submitBox.classList.add("sg-header-submit-removing");
        _this.update();
        setTimeout(function () {
            _this.isShowComment = false;
            textarea.classList.remove("sg-header-post-removing");
            submitBox.classList.remove("sg-header-submit-removing");
            _this.update();
        }, 330);
    }
};
this.doInputComment = function (e) {
    var textarea = e.target;
    _this.commentLength = textarea.value.length;
    _this.update();
};
this.doPost = function (e) {
    e.preventDefault();
};

});

riot.tag('profileaside', '', function(opts) {
});

riot.tag('profileaside', '<aside class="pg-profile"><img src="/assets/icon/1"><h2>{member.displayName}</h2><p>{member.biography}</p><dl><dt>Followings</dt><dd><a href="/following/{member.memberId}">{member.following.count}</a></dd><dt>Followers</dt><dd><a href="/followers/{member.memberId}">{member.followers.count}</a></dd></dl></aside>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
var request = window.superagent;
this.member = (opts.timeline.targetId == null) ? opts.loginMember : opts.member;
// ===================================================================================
//                                                                               Event
//                                                                               =====
if (opts.observable != undefined) {
    opts.observable.on("onLoadLoginMember", function (loginMember) {
        _this.member = loginMember;
        _this.update();
    });
    opts.observable.on("onLoadMember", function (member) {
        _this.member = member;
        _this.update();
    });
}

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

riot.tag('timeline', '<ul class="pg-timeline"><li class="pg-timeline-tweet" each="{tweets}"><img src="http://placehold.jp/64x64.png"><h3><a href="/member/{memberId}">screenName</a></h3><p data-tweet-id="{tweetId}">{text}</p><time><a href="/tweet/{tweetId}">{postedAt}</a></time></li></ul>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
var request = window.superagent;
this.tweets = [];
// ===================================================================================
//                                                                               Event
//                                                                               =====
opts.observable.on("onPost", function () {
    setTimeout(callFindTimeline, 1000);
});
opts.observable.on("onLoadTimeline", function (timeline) {
    var memberIds = [];
    _this.tweets = _
        .chain(timeline)
        .map(function (json) {
        memberIds.push(json.memberId);
        return {
            memberId: json.memberId,
            tweetId: json.tweetId,
            text: json.text,
            postedAt: json.postedAt,
            timestamp: json.timestamp,
            reTweet: json.reTweet
        };
    })
        .concat(_this.tweets)
        .value();
    _this.update();
    opts.findMemberDetailList(memberIds);
});
// ===================================================================================
//                                                                               Logic
//                                                                               =====
var callFindTimeline = function () {
    if (_this.tweets.length > 0) {
        opts.findTimeline(opts.timeline.targetId, null, _this.tweets[0].timestamp);
    }
    else {
        opts.findTimeline(opts.timeline.targetId);
    }
};
var looper = function () {
    callFindTimeline();
    setTimeout(looper, 10000);
};
looper();

});

riot.tag('tweet', '<section><header><img alt="memberIcon" src="/assets/icon/1"><dl><dt>{opts.member.displayName}</dt><dd>@{opts.member.screenName}</dd></dl><follow></follow></header><p if="{isRetweet}">{opts.tweet.reTweet.text}</p><p>{opts.tweet.text}</p><footer><time>{opts.tweet.postedAt}</time></footer></section>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
var request = window.superagent;
this.isRetweet = false;
// ===================================================================================
//                                                                               Event
//                                                                               =====
if (opts.observable != undefined) {
    opts.observable.on("onLoadTweet", function (tweet) {
        _this.update();
        opts.findMemberDetail(opts.tweet.memberId);
    });
    opts.observable.on("onLoadMember", function (member) {
        opts.member = member;
        _this.update();
    });
}

});
