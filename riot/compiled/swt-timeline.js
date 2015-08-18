riot.tag('swt-timeline', '<ul class="sg-contents-timeline {sg-contents-timeline-detail: isDetail}"><li each="{tweets}"><section><dl class="sg-contents-timeline-share"><dt><a href="/content/{shareContents.shareContentsId}" data-id="{shareContents.shareContentsId}" onclick="{onClickDetail}"><img riot-src="{shareContents.thumbnailUrl}" alt="{shareContents.title}"></a></dt><dd><h1><a href="/content/{shareContents.shareContentsId}" data-id="{shareContents.shareContentsId}" onclick="{onClickDetail}"> {shareContents.title} </a></h1></dd></dl><dl class="sg-contents-timeline-comment"><dt><i class="fa fa-user fa-2x"></i></dt><dd><p>{tweet.comment}</p><time>{tweet.postedAt}</time></dd></dl><ul class="sg-contents-timeline-btn"><li><a class="sg-contents-timeline-btn-good" onclick="{onPutGood}" __disabled="{!opts.isLogin}" href="#"><i class="fa fa-thumbs-up"></i> {value.good} <span data-id="{tweet.tweetId}"><i class="fa fa-thumbs-up"></i> Good </span></a></li><li><a class="sg-contents-timeline-btn-bad" onclick="{onPutBad}" __disabled="{!opts.isLogin}" href="#"><i class="fa fa-thumbs-down"></i> {value.bad} <span data-id="{tweet.tweetId}"><i class="fa fa-thumbs-down"></i> Bad </span></a></li></ul></section></li></ul>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
var opts = opts.opts;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
this.isDetail = false;
this.tweets = [];
var request = window.superagent;
// ===================================================================================
//                                                                               Event
//                                                                               =====
this.onClickDetail = function (e) {
    e.preventDefault();
};
this.onPutGood = function (e) {
    e.preventDefault();
    opts.putGood(e.target.getAttribute("data-id"));
};
this.onPutBad = function (e) {
    e.preventDefault();
    opts.putBad(e.target.getAttribute("data-id"));
};
opts.obs.on("onLoadTimeline", function (timeline) {
    _this.tweets = timeline;
    _this.update();
});
opts.obs.on("showDetail", function () {
    _this.isDetail = true;
    _this.update();
});
opts.obs.on("hideDetail", function () {
    _this.isDetail = false;
    _this.update();
});
opts.obs.on("onValueUpdated", function (valueInfo) {
    _this.update();
});
// ===================================================================================
//                                                                               Logic
//                                                                               =====
var callFindTimeline = function () {
    if (_this.tweets.length > 0) {
        opts.findTimeline(null, _this.tweets[0].timestamp);
    }
    else {
        opts.findTimeline();
    }
};
var looper = function () {
    callFindTimeline();
    setTimeout(looper, 10000);
};
looper();

});
