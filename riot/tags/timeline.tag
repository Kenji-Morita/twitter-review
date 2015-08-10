<timeline>
  <ul class="pg-timeline">
      <li class="pg-timeline-tweet" each={tweets}>
          <img src="http://placehold.jp/64x64.png">
          <h3><a href="#">screenName</a></h3>
          <p data-tweet-id={tweetId}>{text}</p>
          <time><a href="#">{postedAt}</a></time>
      </li>
  </ul>

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
    this.tweets = [];

    // ===================================================================================
    //                                                                               Logic
    //                                                                               =====
    var loadTweets = () => {
      var url = "/api/timeline/" + opts.target;
      if (this.tweets.length > 0) {
        url += "?after=" + this.tweets[0].timestamp;
      }
      request
        .get(url)
        .withCredentials()
        .end((error, response) => {
          if (response.ok) {
            var result = JSON.parse(response.text);
            result.value.tweets.forEach(json => {
              this.tweets.push({tweetId: json.tweetId, text: json.text, postedAt: json.postedAt, timestamp: json.timestamp});
            });
            this.update();
          }
        });
      setTimeout(loadTweets, 10000);
    };
    loadTweets();
  </script>
</timeline>
