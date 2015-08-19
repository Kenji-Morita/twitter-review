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
