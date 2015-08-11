riot.tag('follow', '<div if="{opts.loginInfo.isLogin}"><button if="{!isFollowing}" onclick="{doFollow}">Follow</button><button if="{isFollowing}" onclick="{doUnFollow}">Unfollow</button></div>', function(opts) {var _this = this;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
var request = window.superagent;
this.isFollowing = false;
if (opts.loginInfo != undefined && opts.loginInfo.isLogin) {
    if (opts.loginInfo.member.following.list.indexOf(opts.memberId) >= 0) {
        this.isFollowing = true;
    }
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
