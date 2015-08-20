riot.tag('swt-value-btns', '<ul class="sg-contents-timeline-btn"><li if="{sawitter.isLogin && !opts.value.isValued}"><a class="sg-contents-timeline-btn-good" onclick="{onPutGood}" href="#"><i class="fa fa-thumbs-up"></i> {opts.value.good} <span><i class="fa fa-thumbs-up"></i> Good </span></a></li><li if="{sawitter.isLogin && !opts.value.isValued}"><a class="sg-contents-timeline-btn-bad" onclick="{onPutBad}" href="#"><i class="fa fa-thumbs-down"></i> {opts.value.bad} <span><i class="fa fa-thumbs-down"></i> Bad </span></a></li><li if="{sawitter.isLogin && opts.value.isValued}"><a class="sg-contents-timeline-btn-good sg-contents-timeline-btn-complete" onclick="{onCancel}" href="#"><i class="fa fa-thumbs-up"></i> {opts.value.good} <span>Cancel</span></a></li><li if="{sawitter.isLogin && opts.value.isValued}"><a class="sg-contents-timeline-btn-bad sg-contents-timeline-btn-complete" onclick="{onCancel}" href="#"><i class="fa fa-thumbs-down"></i> {opts.value.bad} <span>Cancel</span></a></li><li if="{!sawitter.isLogin}"><a class="sg-contents-timeline-btn-good"><i class="fa fa-thumbs-up"></i> {opts.value.good} </a></li><li if="{!sawitter.isLogin}"><a class="sg-contents-timeline-btn-bad"><i class="fa fa-thumbs-down"></i> {opts.value.bad} </a></li></ul>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
// ===================================================================================
//                                                                               Event
//                                                                               =====
this.onPutGood = function (e) {
    e.preventDefault();
    sawitter.putGood(opts.tweetid);
};
this.onPutBad = function (e) {
    e.preventDefault();
    sawitter.putBad(opts.tweetid);
};
this.onCancel = function (e) {
    e.preventDefault();
};

});
