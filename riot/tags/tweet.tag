<tweet>
  <section>
    <header>
      <img alt="memberIcon" src="/assets/icon/1">
      <dl>
        <dt>{displayName}</dt>
        <dd>@{screenName}</dd>
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

    // ===================================================================================
    //                                                                          Attributes
    //                                                                          ==========
    this.isRetweet = opts.tweet.reTweet != null;

    // ===================================================================================
    //                                                                               Event
    //                                                                               =====

  </script>
</tweet>
