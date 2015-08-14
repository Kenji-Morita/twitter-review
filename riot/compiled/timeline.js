riot.tag('timeline', '<ul class="pg-timeline"><li class="pg-timeline-tweet" each="{tweets}"><img src="http://placehold.jp/64x64.png"><h3><a href="/member/{memberId}">screenName</a></h3><p data-tweet-id="{tweetId}">{text}</p><time><a href="/tweet/{tweetId}">{postedAt}</a></time></li></ul>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
var request = window.superagent;
this.tweets = [];
// ===================================================================================
//                                                                               Event
//                                                                               =====
opts.observable.on("onPost", function () {
    setTimeout(callFindTimeline, 1000);
});
opts.observable.on("onLoadTimeline", function (timeline) {
    var memberIds = [];
    _this.tweets = _
        .chain(timeline)
        .map(function (json) {
        memberIds.push(json.memberId);
        return {
            memberId: json.memberId,
            tweetId: json.tweetId,
            text: json.text,
            postedAt: json.postedAt,
            timestamp: json.timestamp,
            reTweet: json.reTweet
        };
    })
        .concat(_this.tweets)
        .value();
    _this.update();
    opts.findMemberDetailList(memberIds);
});
// ===================================================================================
//                                                                               Logic
//                                                                               =====
var callFindTimeline = function () {
    if (_this.tweets.length > 0) {
        opts.findTimeline(opts.timeline.targetId, null, _this.tweets[0].timestamp);
    }
    else {
        opts.findTimeline(opts.timeline.targetId);
    }
};
var looper = function () {
    callFindTimeline();
    setTimeout(looper, 10000);
};
looper();

});
