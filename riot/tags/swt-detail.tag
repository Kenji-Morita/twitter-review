<swt-detail>
  <div class="sg-contents-detail">
    <section>
      <header>
        <h1><a href="{contents.shareContents.url}" target="_blank">{contents.shareContents.title}</a></h1>
        <p>{contents.shareContents.url}</p>
      </header>
      <swt-tweet-comment opts={opts} url={contents.shareContents.url}></swt-tweet-comment>
      <ul class="sg-contents-detail-timeline">
        <li each={contents.tweets}>
          <section>
            <dl class="sg-contents-timeline-comment">
              <dt>
                <img alt="icon" src="data:image/png;base64,{this.generateIcon(identityHash)}">
              </dt>
              <dd>
                <p>{tweet.comment}</p>
                <time>{tweet.postedAt}</time>
              </dd>
            </dl>
            <ul class="sg-contents-timeline-btn">
              <li>
                <a class="sg-contents-timeline-btn-good" onclick={onPutGood} disabled={!opts.isLogin} href="#">
                  <i class="fa fa-thumbs-up"></i> {value.good}
                  <span data-id={tweet.tweetId}>
                    <i class="fa fa-thumbs-up"></i> Good
                  </span>
                </a>
              </li>
              <li>
                <a class="sg-contents-timeline-btn-bad" onclick={onPutBad} disabled={!opts.isLogin} href="#">
                  <i class="fa fa-thumbs-down"></i> {value.bad}
                  <span data-id={tweet.tweetId}>
                    <i class="fa fa-thumbs-down"></i> Bad
                  </span>
                </a>
              </li>
            </ul>
          </section>
        </li>
      </ul>
    </section>
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

    this.contents = {};

    // ===================================================================================
    //                                                                               Event
    //                                                                               =====

    opts.obs.on("onContentsLoaded", contents => {
      this.contents = contents;
      this.update();
    });

    // ===================================================================================
    //                                                                               Logic
    //                                                                               =====

    this.generateIcon = hash => {
      var source = opts.generateIcon(hash);
      return source;
    }
  </script>
</swt-detail>
