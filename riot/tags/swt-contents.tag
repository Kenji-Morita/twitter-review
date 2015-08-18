<swt-contents>
  <div class="sg-contents {sg-contents-separate: isDetail}">
    <swt-tweet if={!isDetail && opts.isLogin}></swt-tweet>
    <swt-timeline if={!isDetail} opts={opts}></swt-timeline>
    <swt-detail if={isDetail} opts={opts}></swt-detail>
    <swt-iframe if={isDetail}></swt-iframe>
    <swt-modal if={isShowModal} opts={opts}></swt-modal>
  </div>

  <script>
    // ===================================================================================
    //                                                                             Declare
    //                                                                             =======

    declare var riot: any;
    declare var opts: any;

    // ===================================================================================
    //                                                                          Attributes
    //                                                                          ==========

    this.isDetail = false;
    this.isShowModal = false;

    // ===================================================================================
    //                                                                               Event
    //                                                                               =====

    opts.obs.on("showDetail", () => {
      this.isDetail = true;
      this.update();
    });

    opts.obs.on("hideDetail", () => {
      this.isDetail = false;
      this.update();
    });

    opts.obs.on("showModal", () => {
      this.isShowModal = true;
      this.update();
    });

    opts.obs.on("hideModal", () => {
      setTimeout(() => {
        this.isShowModal = false;
        this.update();
      }, 300);
    });

  </script>
</swt-contents>
