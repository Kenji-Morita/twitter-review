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

});
