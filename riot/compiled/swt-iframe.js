riot.tag('swt-iframe', '<iframe class="sg-contents-iframe" name="contentsIframe"></iframe>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
var opts = opts.opts;
// ===================================================================================
//                                                                               Event
//                                                                               =====
opts.obs.on("onContentsLoaded", function (contents) {
    _this.contentsIframe.src = contents.shareContents.url;
});

});
