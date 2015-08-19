<swt-modal>
  <div class="sg-contents-modal">
    <div class={sg-contents-modal-bg: isShowModal} onclick={closeModal}>
    </div>
    <div if={isShowModal} class="sg-contents-modal-contents">
      <section>
        <header class="sg-contents-modal-contents-header">
          <h1>{contents.title}</h1>
        </header>
        <div if={contents.raw != null} name="raw" class="sg-contents-modal-contents-raw"></div>
        <div if={contents.msg != null}>{contents.msg}</div>
        <div if={contents.msgSub != null} class="sg-contents-modal-contents-msg-sub">{contents.msgSub}</div>
        <footer class="sg-contents-modal-contents-footer">
          <ul>
            <li>
              <button onclick={onOk}>{contents.okButtonMsg}</button>
            </li>
            <li>
              <button onclick={onNg}>{contents.ngButtonMsg}</button>
            </li>
          </ul>
        </footer>
      </section>
    </div>
  </div>

  <script>
    // ===================================================================================
    //                                                                             Declare
    //                                                                             =======

    declare var opts: any;
    var opts = opts.opts;

    // ===================================================================================
    //                                                                          Attributes
    //                                                                          ==========

    this.isShowModal = false;
    this.contents = {};

    // ===================================================================================
    //                                                                               Event
    //                                                                               =====

    this.closeModal = e => {
      e.preventDefault();
      opts.obs.trigger("hideModal");
    };

    this.onOk = e => {
      e.preventDefault();
      this.contents.ok(this.raw);
    }

    this.onNg = e => {
      e.preventDefault();
      this.contents.ng(this.raw);
    }

    opts.obs.on("showModal", contents => {
      this.contents = contents;
      this.raw.innerHTML = contents.raw;
      setTimeout(() => {
        this.isShowModal = true;
        this.update();
      }, 1);
    });

    opts.obs.on("hideModal", () => {
      this.contents = {};
      this.raw.innerHTML = "";
      this.isShowModal = false;
      this.update();
    });

  </script>
</swt-modal>