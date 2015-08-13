riot.tag('post', '<form onsubmit="{doPostTweet}" class="sg-post-tweet"><p>tweet length: {tweetLength}</p><textarea oninput="{doInputTweet}"></textarea><button __disabled="{textLengthInvalid}">Tweet</button></form>', function(opts) {var _this = this;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
var request = window.superagent;
this.tweetLength = 0;
this.textLengthInvalid = true;
// ===================================================================================
//                                                                               Event
//                                                                               =====
this.doInputTweet = function (e) {
    var textarea = e.target;
    updateTextareaView(textarea.value);
    _this.update();
};
this.doPostTweet = function (e) {
    e.preventDefault();
    var textarea = e.target.querySelectorAll("textarea")[0];
    request
        .post("/api/tweet/tweet")
        .withCredentials()
        .send({ text: textarea.value })
        .set('Accept', 'application/json')
        .end(function (error, response) {
        if (response.ok) {
            textarea.value = "";
            updateTextareaView(textarea.value);
            opts.observable.trigger("onPost");
        }
    });
};
// ===================================================================================
//                                                                               Logic
//                                                                               =====
var updateTextareaView = function (text) {
    _this.tweetLength = text.length;
    if (_this.tweetLength > 140 || _this.tweetLength <= 0) {
        _this.textLengthInvalid = true;
    }
    else {
        _this.textLengthInvalid = false;
    }
    _this.update();
};

});
