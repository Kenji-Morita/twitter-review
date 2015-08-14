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
