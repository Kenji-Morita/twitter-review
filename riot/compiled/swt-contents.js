riot.tag('swt-contents', '<div class="sg-contents {sg-contents-separate: isDetail}"><swt-cover if="{!isDetail && !opts.isLogin}"></swt-cover><swt-tweet if="{!isDetail && opts.isLogin}"></swt-tweet><swt-timeline if="{!isDetail}" opts="{opts}"></swt-timeline><swt-detail if="{isDetail}" opts="{opts}"></swt-detail><swt-iframe if="{isDetail}"></swt-iframe><swt-modal if="{isShowModal}" opts="{opts}"></swt-modal></div>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
this.isDetail = false;
this.isShowModal = false;
// ===================================================================================
//                                                                               Event
//                                                                               =====
opts.obs.on("showDetail", function () {
    _this.isDetail = true;
    _this.update();
});
opts.obs.on("hideDetail", function () {
    _this.isDetail = false;
    _this.update();
});
opts.obs.on("showModal", function () {
    _this.isShowModal = true;
    _this.update();
});
opts.obs.on("hideModal", function () {
    setTimeout(function () {
        _this.isShowModal = false;
        _this.update();
    }, 300);
});

});
