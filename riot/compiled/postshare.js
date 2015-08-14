riot.tag('postshare', '<form onsubmit="{doPost}" class="{sg-header-post-inputting: isShowComment}"><input type="text" placeholder="share URL" oninput="{doInputURL}"><textarea placeholder="with comment (option)" oninput="{doInputComment}" class="{sg-header-post-inputting: isShowComment}"></textarea><div class="{sg-header-post-submit-inputting: isShowComment}"><p class="{sg-header-post-submit-over: commentLength > 140}">{commentLength}</p><button>Post</button></div></form>', function(opts) {// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
var _this = this;
this.commentLength = 0;
this.isShowComment = false;
// ===================================================================================
//                                                                               Event
//                                                                               =====
this.doInputURL = function (e) {
    var url = e.target.value;
    if (url.length > 0) {
        _this.isShowComment = true;
        _this.update();
    }
    else {
        _this.isInputWait = false;
        var form = e.srcElement.parentElement;
        var textarea = form.querySelector("textarea");
        var submitBox = form.querySelector("div");
        textarea.classList.add("sg-header-post-removing");
        submitBox.classList.add("sg-header-submit-removing");
        _this.update();
        setTimeout(function () {
            _this.isShowComment = false;
            textarea.classList.remove("sg-header-post-removing");
            submitBox.classList.remove("sg-header-submit-removing");
            _this.update();
        }, 330);
    }
};
this.doInputComment = function (e) {
    var textarea = e.target;
    _this.commentLength = textarea.value.length;
    _this.update();
};
this.doPost = function (e) {
    e.preventDefault();
};

});
