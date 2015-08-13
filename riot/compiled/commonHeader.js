riot.tag('commonheader', '<header class="sg-header"><div class="sg-container"><ul class="sg-header-contents"><li><h1><a href="/">SAW Twitter</a></h1></li><li if="{opts.isLogin}"><a href="#">Setting</a><ul><li><a href="#">Setting</a></li><li><a href="#" onclick="{doSignOut}">Sign out</a></li></ul></li></ul></div></header>', function(opts) {// ===================================================================================
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
this.loaded = function () {
    if (opts.isLogin) {
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
    }
};
// ===================================================================================
//                                                                               Mixin
//                                                                               =====
this.mixin({
    profile: {
        loginMember: true,
        memberId: null
    },
    timeline: {
        target: "home"
    }
});

});
