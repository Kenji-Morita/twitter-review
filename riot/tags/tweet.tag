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
    if (opts.observable != undefined) {
      opts.observable.on("onLoadTweet", tweet => {
        this.update();
        opts.findMemberDetail(opts.tweet.memberId);
      });

      opts.observable.on("onLoadMember", member => {
        opts.member = member;
        this.update();
      });
    }
  </script>
</tweet>
