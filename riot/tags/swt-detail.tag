<swt-detail>
  <div class="sg-contents-detail">
    <section>
      <header>
        <h1><a href="{contents.shareContents.url}" target="_blank">{contents.shareContents.title}</a></h1>
        <p>{contents.shareContents.url}</p>
      </header>
      <swt-tweet-comment if={sawitter.isLogin} url={contents.shareContents.url}></swt-tweet-comment>
      <ul class="sg-contents-timeline-sort">
        <li>
          <button onclick={sortByNew}>新着順</button>
        </li>
        <li>
          <button onclick={sortByGood}>Good順</button>
        </li>
        <li>
          <button onclick={sortByBad}>Bad順</button>
        </li>
      </ul>
      <ul class="sg-contents-timeline">
        <li each={contents.tweets}>
          <section>
            <dl class="sg-contents-timeline-comment">
              <dt>
                <img alt="icon" src="data:image/png;base64,{generateIcon(identityHash)}">
              </dt>
              <dd>
                <p>{tweet.comment}</p>
                <time>{tweet.postedAt}</time>
              </dd>
            </dl>
            <swt-value-btns value={value} tweetid={tweet.tweetId}></swt-value-btns>
          </section>
        </li>
      </ul>
    </section>
  </div>

  <script>
    // ===================================================================================
    //                                                                             Declare
    //                                                                             =======

    declare var sawitter: any;
    declare var _: any;

    enum SortMode {
      new,
      good,
      bad
    }

    // ===================================================================================
    //                                                                          Attributes
    //                                                                          ==========

    this.contents = {};
    this.sortMode = SortMode.new;

    // ===================================================================================
    //                                                                               Event
    //                                                                               =====

    this.sortByNew = e => {
      e.preventDefault();
      this.sortMode = SortMode.new;
      this.sort();
    };

    this.sortByGood = e => {
      e.preventDefault();
      this.sortMode = SortMode.good;
      this.sort();
    };

    this.sortByBad = e => {
      e.preventDefault();
      this.sortMode = SortMode.bad;
      this.sort();
    };

    sawitter.obs.on("onContentsLoaded", contents => {
      this.contents = contents;
      this.sort();
      this.update();
    });

    sawitter.obs.on("onPosted", () => {
      if (this.contents.shareContents != undefined) {
        setTimeout(() => {
          sawitter.findContentsDetail(this.contents.shareContents.shareContentsId);
        }, 1000);
      }
    });

    this.generateIcon = hash => {
      var source = sawitter.generateIcon(hash);
      return source;
    }

    // ===================================================================================
    //                                                                               Logic
    //                                                                               =====

    this.sort = () => {
      this.contents.tweets = _
      .chain(this.contents.tweets)
      .sortBy(tweet => {
        switch (this.sortMode) {
          case SortMode.new:
            return tweet.tweet.timestamp;
          case SortMode.good:
            return tweet.value.good;
          case SortMode.bad:
            return tweet.value.bad;
        }
      })
      .reverse()
      .value();
      this.update();
    };

  </script>
</swt-detail>
