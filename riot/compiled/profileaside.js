riot.tag('profileaside', '<aside class="pg-profile"><img src="/assets/icon/1"><h2>{opts.member.displayName}</h2><p>{opts.member.biography}</p><dl><dt>Followings</dt><dd><a href="/following/{opts.member.memberId}">{opts.member.following.count}</a></dd><dt>Followers</dt><dd><a href="/followers/{opts.member.memberId}">{opts.member.followers.count}</a></dd></dl></aside>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
var request = window.superagent;
// ===================================================================================
//                                                                               Event
//                                                                               =====
if (opts.observable != undefined) {
    if (opts.profile.loginMember) {
        opts.loaded();
    }
    else {
        opts.findMemberDetail(opts.profile.memberId);
    }
    opts.observable.on("onLoadMember", function (member) {
        opts.member = member;
        _this.update();
    });
}
// ===================================================================================
//                                                                               Logic
//                                                                               =====
if (opts.profile.loginMember && opts.loginMember) {
    opts.member = opts.loginMember;
}

});
