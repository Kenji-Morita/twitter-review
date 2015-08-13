<tweet>
  <section>
    <header>
      <img alt="memberIcon" src="/assets/icon/1">
      <dl>
        <dt>{opts.member.displayName}</dt>
        <dd>@{opts.member.screenName}</dd>
      </dl>
      <follow></follow>
    </header>
    <p if={isRetweet}>{opts.tweet.reTweet.text}</p>
    <p>{opts.tweet.text}</p>
    <footer>
      <time>{opts.tweet.postedAt}</time>
    </footer>
  </section>

  <script>
    // ===================================================================================
    //                                                                             Declare
    //                                                                             =======

    declare var opts: any;
    interface Window {
      superagent: any;
    }

    // ===================================================================================
    //                                                                          Attributes
    //                                                                          ==========

    var request = window.superagent;
    this.isRetweet = false;

    // ===================================================================================
    //                                                                               Event
    //                                                                               =====

    opts.observable.on("onLoadMember", member => {
      opts.member = member;
      this.update();
    });

    // ===================================================================================
    //                                                                               Logic
    //                                                                               =====

    request
      .get("/api/tweet/detail/" + opts.tweetId)
      .end((error, response) => {
        if(response.ok) {
          var result = JSON.parse(response.text);
          opts.tweet = result.value;
          this.isRetweet = opts.tweet.reTweet != null;
          this.update();
          opts.findMemberDetail(opts.tweet.memberId);
        }
      });

  </script>
</tweet>
