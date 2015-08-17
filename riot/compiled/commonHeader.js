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
request
    .get("http://google.com")
    .end(function (error, response) {
    console.log(response.text);
});
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
