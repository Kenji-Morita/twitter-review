<swt-tweet>
  <div class="sg-contents-tweet">
    <form onsubmit={onSubmit}>
      <input type="text" name="tweet-url" oninput={onInputUrl} placeholder="気になったWEBページのアドレスを入力">
      <textarea name="tweet-comment" if={isStartDisplayComment} oninput={onInputComment} class={sg-contents-tweet-comment-show: isDisplayComment} placeholder="コメントを入力"></textarea>
      <div class="sg-contents-tweet-submit">
        <span class={sg-contents-tweet-submit-invalid: commentLength > 140}>{commentLength}</span>
        <button>投稿</button>
      </div>
    </form>
  </div>

  <script>
    // ===================================================================================
    //                                                                          Attributes
    //                                                                          ==========

    this.isStartDisplayComment = false;
    this.isDisplayComment = false;
    this.commentLength = 0;

    // ===================================================================================
    //                                                                               Event
    //                                                                               =====

    this.onInputUrl = e => {
      var url = e.target.value;
      if (url.length > 0) {
        this.isStartDisplayComment = true;
        this.update();
        setTimeout(() => {
          this.isDisplayComment = true;
          this.update();
        }, 1);
      } else {
        this.isDisplayComment = false;
        this.update();
        setTimeout(() => {
          this.isStartDisplayComment = false;
          this.update();
        }, 200);
      }
    }

    this.onInputComment = e => {
      this.commentLength = e.target.value.length;
      this.update();
    }

    this.onSubmit = e => {
      e.preventDefault();
    }
  </script>
</swt-tweet>
