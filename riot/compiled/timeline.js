riot.tag('timeline', '<ul class="pg-timeline"><li class="pg-timeline-tweet" each="{tweets}"><img src="http://placehold.jp/64x64.png"><h3><a href="/member/{memberId}">screenName</a></h3><p data-tweet-id="{tweetId}">{text}</p><time><a href="/tweet/{tweetId}">{postedAt}</a></time></li></ul>', function(opts) {var _this = this;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
var request = window.superagent;
this.tweets = [];
// ===================================================================================
//                                                                               Logic
//                                                                               =====
var loadTweets = function () {
    var url = "/api/timeline/" + opts.target;
    if (_this.tweets.length > 0) {
        url += "?after=" + _this.tweets[0].timestamp;
    }
    request
        .get(url)
        .withCredentials()
        .end(function (error, response) {
        if (response.ok) {
            var result = JSON.parse(response.text);
            _this.tweets = _
                .chain(result.value.tweets)
                .map(function (json) {
                return {
                    memberId: json.memberId,
                    tweetId: json.tweetId,
                    text: json.text,
                    postedAt: json.postedAt,
                    timestamp: json.timestamp
                };
            })
                .concat(_this.tweets)
                .value();
            _this.update();
        }
    });
    setTimeout(loadTweets, 10000);
};
loadTweets();

});
