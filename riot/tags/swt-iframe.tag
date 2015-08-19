<swt-iframe>
  <iframe class="sg-contents-iframe" name="contentsIframe"></iframe>

  <script>
    // ===================================================================================
    //                                                                             Declare
    //                                                                             =======

    declare var opts: any;
    var opts = opts.opts;

    // ===================================================================================
    //                                                                               Event
    //                                                                               =====

    opts.obs.on("onContentsLoaded", contents => {
      this.contentsIframe.src = contents.shareContents.url;
    });
  </script>
</swt-iframe>
