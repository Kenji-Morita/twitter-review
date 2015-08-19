riot.tag('swt-common', '', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
var request = window.superagent;
this.isLogin = opts.isLogin;
this.obs = riot.observable();
this.currentKeyCodes = [];
// ===================================================================================
//                                                                               Logic
//                                                                               =====
this.doSignUp = function (mail, password, passwordConfirm) {
    // empty validate
    if (mail.isEmpty || password.isEmpty || passwordConfirm.isEmpty) {
        return;
    }
    // sign up
    request
        .post("api/auth/signup")
        .send({ mail: mail, password: password, passwordConfirm: passwordConfirm })
        .set('Accept', 'application/json')
        .end(function (error, response) {
        if (response.ok) {
            location.reload();
        }
    });
};
this.doSignIn = function (mail, password) {
    // empty validate
    if (mail.isEmpty || password.isEmpty) {
        return;
    }
    // sign in
    request
        .post("api/auth/signin")
        .withCredentials()
        .send({ mail: mail, password: password })
        .set('Accept', 'application/json')
        .end(function (error, response) {
        if (response.ok) {
            location.reload();
        }
    });
};
this.doSignOut = function () {
    request
        .post("/api/auth/signout")
        .withCredentials()
        .end(function (error, response) {
        if (response.ok) {
            location.href = "/";
        }
    });
};
this.findTimeline = function (before, after) {
    // set url parameter
    var url = "/api/timeline/home";
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
            _this.obs.trigger("onLoadTimeline", timeline);
        }
    });
};
this.doPost = function (url, comment) {
    request
        .post("/api/tweet/tweet")
        .send({ url: url, comment: comment })
        .set('Accept', 'application/json')
        .end(function (error, response) {
        if (response.ok) {
            _this.obs.trigger("onPosted");
        }
    });
};
this.putGood = function (tweetId) {
    request
        .put("/api/value/good/" + tweetId)
        .end(function (error, response) {
        if (response.ok) {
            var valueCount = JSON.parse(response.text).value;
            _this.obs.trigger("onValueUpdated", { tweetId: tweetId, value: valueCount });
        }
    });
};
this.putBad = function (tweetId) {
    request
        .put("/api/value/bad/" + tweetId)
        .end(function (error, response) {
        if (response.ok) {
            var valueCount = JSON.parse(response.text).value;
            _this.obs.trigger("onValueUpdated", { tweetId: tweetId, value: valueCount });
        }
    });
};
this.findContentsDetail = function (shareContentsId) {
    request
        .get("/api/contents/" + shareContentsId)
        .end(function (error, response) {
        if (response.ok) {
            var contents = JSON.parse(response.text).value;
            _this.obs.trigger("onContentsLoaded", contents);
        }
    });
};
this.showDetail = function (shareContentsId) {
    _this.obs.trigger("showDetail", shareContentsId);
    _this.findContentsDetail(shareContentsId);
    history.pushState(null, null, '/contents/' + shareContentsId);
};
this.generateIcon = function (input) {
    var salt = 0;
    var rounds = 1;
    var size = 32;
    var outputType = "HEX";
    var hashType = "SHA-512";
    var shaObj = new jsSHA(input + salt, "TEXT");
    var hash = shaObj.getHash(hashType, outputType, rounds);
    return new Identicon(hash, 32).toString();
};
window.addEventListener("keydown", function (e) {
    var keyCode = e.keyCode;
    var index = _this.currentKeyCodes.indexOf(keyCode);
    if (index < 0) {
        _this.currentKeyCodes.push(keyCode);
    }
});
window.addEventListener("keyup", function (e) {
    var index = _this.currentKeyCodes.indexOf(e.keyCode);
    if (index >= 0) {
        _this.currentKeyCodes.splice(index, 1);
    }
});
window.addEventListener("popstate", function (e) {
    var path = e.target.location.pathname;
    if (path == "/") {
        _this.obs.trigger("hideDetail");
    }
    else {
        var array = path.split("/");
        _this.showDetail(array[array.length - 1]);
    }
});

});

riot.tag('swt-contents', '<div class="sg-contents {sg-contents-separate: isDetail}"><swt-cover if="{!isDetail && !opts.isLogin}"></swt-cover><swt-tweet if="{!isDetail && opts.isLogin}"></swt-tweet><swt-timeline if="{!isDetail}" opts="{opts}"></swt-timeline><swt-detail if="{isDetail}" opts="{opts}"></swt-detail><swt-iframe if="{isDetail}" opts="{opts}"></swt-iframe><swt-modal if="{isShowModal}" opts="{opts}"></swt-modal></div>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
this.isDetail = false;
this.isShowModal = false;
// ===================================================================================
//                                                                               Event
//                                                                               =====
opts.obs.on("showDetail", function () {
    _this.isDetail = true;
    _this.update();
});
opts.obs.on("hideDetail", function () {
    _this.isDetail = false;
    _this.update();
});
opts.obs.on("showModal", function () {
    _this.isShowModal = true;
    _this.update();
});
opts.obs.on("hideModal", function () {
    setTimeout(function () {
        _this.isShowModal = false;
        _this.update();
    }, 300);
});

});

riot.tag('swt-cover', '<div class="sg-contents-cover"><ul></ul></div>', function(opts) {
});

riot.tag('swt-detail', '<div class="sg-contents-detail"><section><header><h1><a href="{contents.shareContents.url}" target="_blank">{contents.shareContents.title}</a></h1><p>{contents.shareContents.url}</p></header><swt-tweet-comment opts="{opts}" url="{contents.shareContents.url}"></swt-tweet-comment><ul class="sg-contents-detail-timeline"><li each="{contents.tweets}"><section><dl class="sg-contents-timeline-comment"><dt><img alt="icon" riot-src="data:image/png;base64,{this.generateIcon(identityHash)}"></dt><dd><p>{tweet.comment}</p><time>{tweet.postedAt}</time></dd></dl><ul class="sg-contents-timeline-btn"><li><a class="sg-contents-timeline-btn-good" onclick="{onPutGood}" __disabled="{!opts.isLogin}" href="#"><i class="fa fa-thumbs-up"></i> {value.good} <span data-id="{tweet.tweetId}"><i class="fa fa-thumbs-up"></i> Good </span></a></li><li><a class="sg-contents-timeline-btn-bad" onclick="{onPutBad}" __disabled="{!opts.isLogin}" href="#"><i class="fa fa-thumbs-down"></i> {value.bad} <span data-id="{tweet.tweetId}"><i class="fa fa-thumbs-down"></i> Bad </span></a></li></ul></section></li></ul></section></div>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
var opts = opts.opts;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
this.contents = {};
// ===================================================================================
//                                                                               Event
//                                                                               =====
opts.obs.on("onContentsLoaded", function (contents) {
    _this.contents = contents;
    _this.update();
});
// ===================================================================================
//                                                                               Logic
//                                                                               =====
this.generateIcon = function (hash) {
    var source = opts.generateIcon(hash);
    return source;
};

});

riot.tag('swt-footer', '<footer class="sg-footer"><div class="sg-container"><p>(c)2015 SAW</p></div></footer>', function(opts) {
});

riot.tag('swt-header', '<header class="sg-header"><ul><li class="sg-header-logo"><h1><a href="/"><i class="fa fa-user-secret fa"></i> Sawitter</a></h1></li><li if="{isLogin}" class="sg-header-tweet"><a href="#" onclick="{tweetNews}"><i class="fa fa-pencil-square-o"></i></a></li><li class="sg-header-signs"><ul><li if="{!isLogin}" onclick="{onSignin}" class="sg-header-signin"><button>サインイン</button></li><li if="{!isLogin}" onclick="{onSignup}" class="sg-header-signup"><button>登録</button></li><li if="{isLogin}" onclick="{onSignout}" class="sg-header-signout"><button>サインアウト</button></li></ul></li></ul></header><form name="signin" class="sg-header-signs-signin" if="{false}"><label>メールアドレス</label><input type="text" name="signinMail" placeholder="メールアドレスを入力してください"><label>パスワード</label><input type="password" name="signinPassword" placeholder="パスワードを入力してください"></form><form name="signup" class="sg-header-signs-signup" if="{false}"><label>メールアドレス</label><input type="text" name="signupMail" placeholder="メールアドレスを入力してください"><label>パスワード</label><input type="password" name="signupPassword" placeholder="パスワードを入力してください"><label>パスワード(再確認)</label><input type="password" name="signupPasswordConfirm" placeholder="パスワードを再度入力してください"></form>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
this.isLogin = opts.isLogin;
// ===================================================================================
//                                                                               Event
//                                                                               =====
this.tweetNews = function (e) {
    e.preventDefault();
    var runTime = 10;
    var fps = 60;
    var diffY = window.scrollY / fps;
    var scrollToTop = function () {
        var currentY = window.scrollY;
        var targetY = currentY - diffY;
        var targetY = targetY <= 0 ? 0 : targetY;
        if (currentY > 0) {
            window.scrollTo(0, targetY);
            if (targetY > 0) {
                setTimeout(scrollToTop, runTime / fps);
            }
        }
    };
    scrollToTop();
};
this.onSignin = function (e) {
    e.preventDefault();
    opts.obs.trigger("showModal", {
        title: "サインイン",
        raw: _this.signin.innerHTML,
        okButtonMsg: "サインイン",
        ngButtonMsg: "キャンセル",
        ok: function (raw) {
            var mail = raw.querySelector('input[name="signinMail"]').value.trim();
            var password = raw.querySelector('input[name="signinPassword"]').value.trim();
            if (mail == "") {
                alert("メールアドレスが入力されていません");
                return;
            }
            if (password == "") {
                alert("パスワードが入力されていません");
                return;
            }
            opts.doSignIn(mail, password);
        },
        ng: function (raw) {
            opts.obs.trigger("hideModal");
        }
    });
};
this.onSignup = function (e) {
    e.preventDefault();
    opts.obs.trigger("showModal", {
        title: "登録",
        raw: _this.signup.innerHTML,
        okButtonMsg: "登録",
        ngButtonMsg: "キャンセル",
        ok: function (raw) {
            var mail = raw.querySelector('input[name="signupMail"]').value.trim();
            var password = raw.querySelector('input[name="signupPassword"]').value.trim();
            var passwordConfirm = raw.querySelector('input[name="signupPasswordConfirm"]').value.trim();
            if (mail == "") {
                alert("メールアドレスが入力されていません");
                return;
            }
            if (password == "") {
                alert("パスワードが入力されていません");
                return;
            }
            if (passwordConfirm == "") {
                alert("パスワード(再確認)が入力されていません");
                return;
            }
            opts.doSignUp(mail, password, passwordConfirm);
        },
        ng: function (raw) {
            opts.obs.trigger("hideModal");
        }
    });
};
this.onSignout = function (e) {
    e.preventDefault();
    opts.doSignOut();
};

});

riot.tag('swt-iframe', '<iframe class="sg-contents-iframe" name="contentsIframe"></iframe>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
var opts = opts.opts;
// ===================================================================================
//                                                                               Event
//                                                                               =====
opts.obs.on("onContentsLoaded", function (contents) {
    _this.contentsIframe.src = contents.shareContents.url;
});

});

riot.tag('swt-modal', '<div class="sg-contents-modal"><div class="{sg-contents-modal-bg: isShowModal}" onclick="{closeModal}"></div><div if="{isShowModal}" class="sg-contents-modal-contents"><section><header class="sg-contents-modal-contents-header"><h1>{contents.title}</h1></header><div if="{contents.raw != null}" name="raw" class="sg-contents-modal-contents-raw"></div><div if="{contents.msg != null}">{contents.msg}</div><div if="{contents.msgSub != null}" class="sg-contents-modal-contents-msg-sub">{contents.msgSub}</div><footer class="sg-contents-modal-contents-footer"><ul><li><button onclick="{onOk}">{contents.okButtonMsg}</button></li><li><button onclick="{onNg}">{contents.ngButtonMsg}</button></li></ul></footer></section></div></div>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
var opts = opts.opts;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
this.isShowModal = false;
this.contents = {};
// ===================================================================================
//                                                                               Event
//                                                                               =====
this.closeModal = function (e) {
    e.preventDefault();
    opts.obs.trigger("hideModal");
};
this.onOk = function (e) {
    e.preventDefault();
    _this.contents.ok(_this.raw);
};
this.onNg = function (e) {
    e.preventDefault();
    _this.contents.ng(_this.raw);
};
opts.obs.on("showModal", function (contents) {
    _this.contents = contents;
    _this.raw.innerHTML = contents.raw;
    setTimeout(function () {
        _this.isShowModal = true;
        _this.update();
    }, 1);
});
opts.obs.on("hideModal", function () {
    _this.contents = {};
    _this.raw.innerHTML = "";
    _this.isShowModal = false;
    _this.update();
});

});

riot.tag('swt-timeline', '<ul class="sg-contents-timeline {sg-contents-timeline-detail: isDetail}"><li each="{tweets}"><section><dl class="sg-contents-timeline-share"><dt><a href="/content/{shareContents.shareContentsId}" onclick="{onClickDetail}"><img riot-src="{shareContents.thumbnailUrl}" alt="{shareContents.title}"></a></dt><dd><h1><a href="/content/{shareContents.shareContentsId}" onclick="{onClickDetail}"> {shareContents.title} </a></h1></dd></dl><dl class="sg-contents-timeline-comment"><dt><i class="fa fa-user fa-2x"></i></dt><dd><p>{tweet.comment}</p><time>{tweet.postedAt}</time></dd></dl><ul class="sg-contents-timeline-btn"><li><a class="sg-contents-timeline-btn-good" onclick="{onPutGood}" __disabled="{!opts.isLogin}" href="#"><i class="fa fa-thumbs-up"></i> {value.good} <span data-id="{tweet.tweetId}"><i class="fa fa-thumbs-up"></i> Good </span></a></li><li><a class="sg-contents-timeline-btn-bad" onclick="{onPutBad}" __disabled="{!opts.isLogin}" href="#"><i class="fa fa-thumbs-down"></i> {value.bad} <span data-id="{tweet.tweetId}"><i class="fa fa-thumbs-down"></i> Bad </span></a></li></ul></section></li></ul>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
var opts = opts.opts;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
this.isDetail = false;
this.tweets = [];
// ===================================================================================
//                                                                               Event
//                                                                               =====
this.onClickDetail = function (e) {
    e.preventDefault();
    var commandLeftIndex = opts.currentKeyCodes.indexOf(91);
    var commandRightIndex = opts.currentKeyCodes.indexOf(93);
    if (commandLeftIndex >= 0 || commandRightIndex >= 0) {
        window.open(e.item.shareContents.url);
    }
    var shareContentsId = e.item.shareContents.shareContentsId;
    opts.showDetail(shareContentsId);
};
this.onPutGood = function (e) {
    e.preventDefault();
    opts.putGood(e.target.getAttribute("data-id"));
};
this.onPutBad = function (e) {
    e.preventDefault();
    opts.putBad(e.target.getAttribute("data-id"));
};
opts.obs.on("onLoadTimeline", function (timeline) {
    _this.tweets = timeline;
    _this.update();
});
opts.obs.on("showDetail", function () {
    _this.isDetail = true;
    _this.update();
});
opts.obs.on("hideDetail", function () {
    _this.isDetail = false;
    _this.update();
});
opts.obs.on("onValueUpdated", function (valueInfo) {
    _this.update();
});
opts.obs.on("onPosted", function () {
    setTimeout(callFindTimeline, 100);
});
// ===================================================================================
//                                                                               Logic
//                                                                               =====
var callFindTimeline = function () {
    if (_this.tweets.length > 0) {
        opts.findTimeline(null, _this.tweets[0].timestamp);
    }
    else {
        opts.findTimeline();
    }
};
var looper = function () {
    callFindTimeline();
    setTimeout(looper, 10000);
};
looper();

});

riot.tag('swt-tweet-comment', '<div class="sg-contents-tweet"><form onsubmit="{onSubmit}"><input type="hidden" value="{opts.url}" name="tweetUrl" ><textarea name="tweetComment" oninput="{onInputComment}" class="sg-contents-tweet-comment-show" placeholder="コメントを入力"></textarea><div class="sg-contents-tweet-submit"><span class="{sg-contents-tweet-submit-invalid: commentLength > 140}">{commentLength}</span><button __disabled="{commentLength <= 0 || commentLength > 140}">投稿</button></div></form></div>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
var opts = opts.opts.opts;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
this.commentLength = 0;
// ===================================================================================
//                                                                               Event
//                                                                               =====
this.onInputComment = function (e) {
    _this.commentLength = e.target.value.length;
    _this.update();
};
this.onSubmit = function (e) {
    e.preventDefault();
    var urlObj = _this.tweetUrl;
    var commentObj = _this.tweetComment;
    var url = urlObj.value.trim();
    var comment = commentObj.value.trim();
    if (url == "") {
        alert("URLを入力してください");
        return;
    }
    if (comment == "") {
        alert("コメントを入力してください");
        return;
    }
    opts.obs.trigger("showModal", {
        title: "投稿確認",
        msg: comment,
        msgSub: "WEBページ(" + url + ")について、このコメントを投稿してもよろしいでしょうか？",
        okButtonMsg: "投稿",
        ngButtonMsg: "キャンセル",
        ok: function () {
            opts.doPost(url, comment);
            urlObj.value = "";
            commentObj.value = "";
            _this.commentLength = 0;
            opts.obs.trigger("hideModal");
        },
        ng: function () {
            opts.obs.trigger("hideModal");
        }
    });
};

});

riot.tag('swt-tweet', '<div class="sg-contents-tweet"><form onsubmit="{onSubmit}"><input type="text" name="tweetUrl" oninput="{onInputUrl}" placeholder="気になったWEBページのアドレスを入力"><textarea name="tweetComment" if="{isStartDisplayComment}" oninput="{onInputComment}" class="{sg-contents-tweet-comment-show: isDisplayComment}" placeholder="コメントを入力"></textarea><div class="sg-contents-tweet-submit"><span class="{sg-contents-tweet-submit-invalid: commentLength > 140}">{commentLength}</span><button __disabled="{!isDisplayComment || commentLength <= 0 || commentLength > 140}">投稿</button></div></form></div>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
this.isStartDisplayComment = false;
this.isDisplayComment = false;
this.commentLength = 0;
// ===================================================================================
//                                                                               Event
//                                                                               =====
this.onInputUrl = function (e) {
    var url = e.target.value;
    if (url.length > 0) {
        _this.isStartDisplayComment = true;
        _this.update();
        setTimeout(function () {
            _this.isDisplayComment = true;
            _this.update();
        }, 1);
    }
    else {
        _this.isDisplayComment = false;
        _this.update();
        setTimeout(function () {
            _this.isStartDisplayComment = false;
            _this.update();
        }, 200);
    }
};
this.onInputComment = function (e) {
    _this.commentLength = e.target.value.length;
    _this.update();
};
this.onSubmit = function (e) {
    e.preventDefault();
    var urlObj = _this.tweetUrl;
    var commentObj = _this.tweetComment;
    var url = urlObj.value.trim();
    var comment = commentObj.value.trim();
    if (url == "") {
        alert("URLを入力してください");
        return;
    }
    if (comment == "") {
        alert("コメントを入力してください");
        return;
    }
    opts.obs.trigger("showModal", {
        title: "投稿確認",
        msg: comment,
        msgSub: "WEBページ(" + url + ")について、このコメントを投稿してもよろしいでしょうか？",
        okButtonMsg: "投稿",
        ngButtonMsg: "キャンセル",
        ok: function () {
            opts.doPost(url, comment);
            urlObj.value = "";
            commentObj.value = "";
            _this.commentLength = 0;
            opts.obs.trigger("hideModal");
        },
        ng: function () {
            opts.obs.trigger("hideModal");
        }
    });
};

});
