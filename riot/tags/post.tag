<post>

  <form onsubmit={doPostTweet} class="sg-post-tweet">
    <p>tweet length: {tweetLength}</p>
    <textarea oninput={doInputTweet}></textarea>
    <button disabled={textLengthInvalid}>Tweet</button>
  </form>

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
    this.tweetLength = 0;
    this.textLengthInvalid = true;

    // ===================================================================================
    //                                                                               Event
    //                                                                               =====
    this.doInputTweet = e => {
      var textarea = e.target;
      updateTextareaView(textarea.value);
      this.update();
    };

    this.doPostTweet = e => {
      e.preventDefault();
      var textarea = e.target.querySelectorAll("textarea")[0];
      request
        .post("/api/tweet/tweet")
        .withCredentials()
        .send({text: textarea.value})
        .set('Accept', 'application/json')
        .end((error, response) => {
          if (response.ok) {
            textarea.value = "";
            updateTextareaView(textarea.value);
            opts.observable.trigger("onPost");
          }
        });
    };

    // ===================================================================================
    //                                                                               Logic
    //                                                                               =====
    var updateTextareaView = (text: string) => {
      this.tweetLength = text.length;
      if (this.tweetLength > 140 || this.tweetLength <= 0) {
        this.textLengthInvalid = true;
      } else {
        this.textLengthInvalid = false;
      }
      this.update();
    }

  </script>

</post>
