<timeline>
  <ul class="pg-timeline">
      <li class="pg-timeline-tweet" each={tweets}>
          <img src="http://placehold.jp/64x64.png">
          <h3><a href="/member/{memberId}">screenName</a></h3>
          <p data-tweet-id={tweetId}>{text}</p>
          <time><a href="/tweet/{tweetId}">{postedAt}</a></time>
      </li>
  </ul>

  <script>
    // ===================================================================================
    //                                                                             Declare
    //                                                                             =======

    declare var opts: any;
    declare var _: any;
    interface Window {
      superagent: any;
    }

    // ===================================================================================
    //                                                                          Attributes
    //                                                                          ==========

    var request = window.superagent;
    this.tweets = [];

    // ===================================================================================
    //                                                                               Event
    //                                                                               =====

    opts.observable.on("onPost", () => {
      setTimeout(callFindTimeline, 1000);
    });

    opts.observable.on("onLoadTimeline", timeline => {
      var memberIds = [];
      this.tweets = _
        .chain(timeline)
        .map(json => {
          memberIds.push(json.memberId);
          return {
            memberId: json.memberId,
            tweetId: json.tweetId,
            text: json.text,
            postedAt: json.postedAt,
            timestamp: json.timestamp,
            reTweet: json.reTweet
          };
        })
        .concat(this.tweets)
        .value();
      this.update();
      opts.findMemberDetailList(memberIds);
    });

    // ===================================================================================
    //                                                                               Logic
    //                                                                               =====

    var callFindTimeline = () => {
      if (this.tweets.length > 0) {
        opts.findTimeline(opts.timeline.targetId, null, this.tweets[0].timestamp);
      } else {
        opts.findTimeline(opts.timeline.targetId);
      }
    };

    var looper = () => {
      callFindTimeline();
      setTimeout(looper, 10000);
    };

    looper();
  </script>
</timeline>
