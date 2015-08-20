riot.tag('swt-timeline', '<ul class="sg-contents-timeline {sg-contents-timeline-detail: isDetail}"><li each="{tweets}"><section><dl class="sg-contents-timeline-share"><dt><a href="/content/{shareContents.shareContentsId}" onclick="{onClickDetail}"><img riot-src="{shareContents.thumbnailUrl}" alt="{shareContents.title}"></a></dt><dd><h1><a href="/content/{shareContents.shareContentsId}" onclick="{onClickDetail}"> {shareContents.title} </a></h1></dd></dl><dl class="sg-contents-timeline-comment"><dt><i class="fa fa-user fa-2x"></i></dt><dd><p>{tweet.comment}</p><time>{tweet.postedAt}</time></dd></dl><swt-value-btns value="{value}" tweetid="{tweet.tweetId}"></swt-value-btns></section></li></ul>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
this.isDetail = false;
this.tweets = [];
// ===================================================================================
//                                                                               Event
//                                                                               =====
this.onClickDetail = function (e) {
    e.preventDefault();
    var commandLeftIndex = sawitter.currentKeyCodes.indexOf(91);
    var commandRightIndex = sawitter.currentKeyCodes.indexOf(93);
    if (commandLeftIndex >= 0 || commandRightIndex >= 0) {
        window.open(e.item.shareContents.url);
    }
    var shareContentsId = e.item.shareContents.shareContentsId;
    sawitter.showDetail(shareContentsId);
};
sawitter.obs.on("onLoadTimeline", function (timeline) {
    _this.tweets = timeline;
    _this.update();
});
sawitter.obs.on("showDetail", function () {
    _this.isDetail = true;
    _this.update();
});
sawitter.obs.on("hideDetail", function () {
    _this.isDetail = false;
    _this.update();
});
sawitter.obs.on("onValueUpdated", function (valueInfo) {
    _this.update();
});
sawitter.obs.on("onPosted", function () {
    setTimeout(callFindTimeline, 100);
});
// ===================================================================================
//                                                                               Logic
//                                                                               =====
var callFindTimeline = function () {
    if (_this.tweets.length > 0) {
        sawitter.findTimeline(null, _this.tweets[0].timestamp);
    }
    else {
        sawitter.findTimeline();
    }
};
var looper = function () {
    callFindTimeline();
    setTimeout(looper, 10000);
};
looper();

});
