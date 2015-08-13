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
        if (opts.isLogin) {
            if (opts.loginMember.following.list.indexOf(member.memberId) >= 0) {
                _this.isFollowing = true;
            }
            _this.isMe = opts.loginMember.memberId == member.memberId;
            _this.update();
        }
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

});
