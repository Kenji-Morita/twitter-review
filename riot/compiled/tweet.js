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
opts.observable.on("onLoadMember", function (member) {
    opts.member = member;
    _this.update();
});
// ===================================================================================
//                                                                               Logic
//                                                                               =====
request
    .get("/api/tweet/detail/" + opts.tweetId)
    .end(function (error, response) {
    if (response.ok) {
        var result = JSON.parse(response.text);
        opts.tweet = result.value;
        _this.isRetweet = opts.tweet.reTweet != null;
        _this.update();
        opts.findMemberDetail(opts.tweet.memberId);
    }
});

});
