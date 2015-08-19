riot.tag('swt-tweet', '<div class="sg-contents-tweet"><form onsubmit="{onSubmit}"><input type="text" name="tweetUrl" oninput="{onInputUrl}" placeholder="気になったWEBページのアドレスを入力"><textarea name="tweetComment" if="{isStartDisplayComment}" oninput="{onInputComment}" class="{sg-contents-tweet-comment-show: isDisplayComment}" placeholder="コメントを入力"></textarea><div class="sg-contents-tweet-submit"><span class="{sg-contents-tweet-submit-invalid: commentLength > 140}">{commentLength}</span><button __disabled="{!isDisplayComment || commentLength <= 0 || commentLength > 140}">投稿</button></div></form></div>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
this.isStartDisplayComment = false;
this.isDisplayComment = false;
this.commentLength = 0;
// ===================================================================================
//                                                                               Event
//                                                                               =====
this.onInputUrl = function (e) {
    var url = e.target.value;
    if (url.length > 0) {
        _this.isStartDisplayComment = true;
        _this.update();
        setTimeout(function () {
            _this.isDisplayComment = true;
            _this.update();
        }, 1);
    }
    else {
        _this.isDisplayComment = false;
        _this.update();
        setTimeout(function () {
            _this.isStartDisplayComment = false;
            _this.update();
        }, 200);
    }
};
this.onInputComment = function (e) {
    _this.commentLength = e.target.value.length;
    _this.update();
};
this.onSubmit = function (e) {
    e.preventDefault();
    var url = _this.tweetUrl.value.trim();
    var comment = _this.tweetComment.value.trim();
    if (url == "") {
        alert("URLを入力してください");
        return;
    }
    if (comment == "") {
        alert("コメントを入力してください");
        return;
    }
    opts.obs.trigger("showModal", {
        title: "投稿確認",
        msg: comment,
        msgSub: "WEBページ(" + url + ")について、このコメントを投稿してもよろしいでしょうか？",
        okButtonMsg: "投稿",
        ngButtonMsg: "キャンセル",
        ok: function () {
            opts.doPost(url, comment);
        },
        ng: function () {
            opts.obs.trigger("hideModal");
        }
    });
};

});
