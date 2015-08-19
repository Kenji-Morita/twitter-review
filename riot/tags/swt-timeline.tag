<swt-timeline>
  <ul class="sg-contents-timeline {sg-contents-timeline-detail: isDetail}">
    <li each={tweets}>
      <section>
        <dl class="sg-contents-timeline-share">
          <dt>
            <a href="/content/{shareContents.shareContentsId}" onclick={onClickDetail}>
              <img src={shareContents.thumbnailUrl} alt={shareContents.title}>
            </a>
          </dt>
          <dd>
            <h1>
              <a href="/content/{shareContents.shareContentsId}" onclick={onClickDetail}>
                {shareContents.title}
              </a>
            </h1>
          </dd>
        </dl>
        <dl class="sg-contents-timeline-comment">
          <dt>
            <i class="fa fa-user fa-2x"></i>
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

  <script>
    // ===================================================================================
    //                                                                             Declare
    //                                                                             =======

    declare var opts: any;
    var opts = opts.opts;

    // ===================================================================================
    //                                                                          Attributes
    //                                                                          ==========

    this.isDetail = false;
    this.tweets = [];

    // ===================================================================================
    //                                                                               Event
    //                                                                               =====

    this.onClickDetail = e => {
      e.preventDefault();
      var commandLeftIndex = opts.currentKeyCodes.indexOf(91);
      var commandRightIndex = opts.currentKeyCodes.indexOf(93);
      if (commandLeftIndex >= 0 || commandRightIndex >= 0) {
        window.open(e.item.shareContents.url);
      }
      var shareContentsId = e.item.shareContents.shareContentsId;
      opts.showDetail(shareContentsId);
    }

    this.onPutGood = e => {
      e.preventDefault();
      opts.putGood(e.target.getAttribute("data-id"));
    }

    this.onPutBad = e => {
      e.preventDefault();
      opts.putBad(e.target.getAttribute("data-id"));
    }

    opts.obs.on("onLoadTimeline", timeline => {
      this.tweets = timeline;
      this.update();
    });

    opts.obs.on("showDetail", () => {
      this.isDetail = true;
      this.update();
    });

    opts.obs.on("hideDetail", () => {
      this.isDetail = false;
      this.update();
    });

    opts.obs.on("onValueUpdated", valueInfo => {
      this.update();
    });

    opts.obs.on("onPosted", () => {
      setTimeout(callFindTimeline, 100);
    });

    // ===================================================================================
    //                                                                               Logic
    //                                                                               =====

    var callFindTimeline = () => {
      if (this.tweets.length > 0) {
        opts.findTimeline(null, this.tweets[0].timestamp);
      } else {
        opts.findTimeline();
      }
    };

    var looper = () => {
      callFindTimeline();
      setTimeout(looper, 10000);
    };

    looper();

  </script>
</swt-timeline>
