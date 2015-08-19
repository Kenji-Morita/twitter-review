<swt-common>
    <script>
    // ===================================================================================
    //                                                                             Declare
    //                                                                             =======

    declare var riot: any;
    declare var opts: any;
    declare var _: any;
    declare var Identicon: any;
    declare var jsSHA: any;
    interface Window {
      superagent: any;
    }
    interface Event {
      originalEvent: any;
    }
    interface EventTarget {
      location: any;
    }

    // ===================================================================================
    //                                                                          Attributes
    //                                                                          ==========

    var request = window.superagent;
    this.isLogin = opts.isLogin;
    this.obs = riot.observable();
    this.currentKeyCodes = [];

    // ===================================================================================
    //                                                                               Logic
    //                                                                               =====

    this.doSignUp = (mail, password, passwordConfirm) => {
      // empty validate
      if (mail.isEmpty || password.isEmpty || passwordConfirm.isEmpty) {
        return;
      }

      // sign up
      request
        .post("api/auth/signup")
        .send({mail: mail, password: password, passwordConfirm: passwordConfirm})
        .set('Accept', 'application/json')
        .end((error, response) => {
          if (response.ok) {
            location.reload();
          }
        })
    };

    this.doSignIn = (mail, password) => {
      // empty validate
      if (mail.isEmpty || password.isEmpty) {
        return;
      }

      // sign in
      request
        .post("api/auth/signin")
        .withCredentials()
        .send({mail: mail, password: password})
        .set('Accept', 'application/json')
        .end((error, response) => {
          if (response.ok) {
            location.reload();
          }
        });
    };

    this.doSignOut = () => {
      request
        .post("/api/auth/signout")
        .withCredentials()
        .end((error, response) => {
          if (response.ok) {
            location.href = "/";
          }
        });
    };

    this.findTimeline = (before, after) => {

      // set url parameter
      var url = "/api/timeline/home";
      var existsParameter = false;
      var addParameter = (key, value) => {
          if (!existsParameter) {
              url += "?";
              existsParameter = true;
          }
          else {
              url += "&";
          }
          url += key + "=" + value;
      };
      if (before != null) {
          addParameter("before", before);
      }
      if (after != null) {
          addParameter("after", after);
      }

      // request timeline
      request
        .get(url)
        .withCredentials()
        .end((error, response) => {
          if (response.ok) {
            var timeline = JSON.parse(response.text).value;
            this.obs.trigger("onLoadTimeline", timeline);
          }
        });
    };

    this.doPost = (url, comment) => {
      request
        .post("/api/tweet/tweet")
        .send({url: url, comment: comment})
        .set('Accept', 'application/json')
        .end((error, response) => {
          if (response.ok) {
            this.obs.trigger("onPosted");
          }
        });
    };

    this.putGood = tweetId => {
      request
        .put("/api/value/good/" + tweetId)
        .end((error, response) => {
          if (response.ok) {
            var valueCount = JSON.parse(response.text).value;
            this.obs.trigger("onValueUpdated", {tweetId: tweetId, value: valueCount});
          }
        });
    };

    this.putBad = tweetId => {
      request
        .put("/api/value/bad/" + tweetId)
        .end((error, response) => {
          if (response.ok) {
            var valueCount = JSON.parse(response.text).value;
            this.obs.trigger("onValueUpdated", {tweetId: tweetId, value: valueCount});
          }
        });
    };

    this.findContentsDetail = shareContentsId => {
      request
        .get("/api/contents/" + shareContentsId)
        .end((error, response) => {
          if (response.ok) {
            var contents = JSON.parse(response.text).value;
            this.obs.trigger("onContentsLoaded", contents);
          }
        });
    };

    this.showDetail = shareContentsId => {
      this.obs.trigger("showDetail", shareContentsId);
      this.findContentsDetail(shareContentsId);
      history.pushState(null, null, '/contents/' + shareContentsId);
    };

    this.generateIcon = input => {
      var salt = 0;
      var rounds = 1;
      var size = 32;
      var outputType = "HEX";
      var hashType = "SHA-512";
      var shaObj = new jsSHA(input + salt, "TEXT");
      var hash = shaObj.getHash(hashType, outputType, rounds);
      return new Identicon(hash, 32).toString();
    };

    window.addEventListener("keydown", e => {
      var keyCode = e.keyCode;
      var index = this.currentKeyCodes.indexOf(keyCode);
      if (index < 0) {
        this.currentKeyCodes.push(keyCode);
      }
    });

    window.addEventListener("keyup", e => {
      var index = this.currentKeyCodes.indexOf(e.keyCode);
      if (index >= 0) {
        this.currentKeyCodes.splice(index, 1);
      }
    });

    window.addEventListener("popstate", e => {
      var path = e.target.location.pathname;
      if (path == "/") {
        this.obs.trigger("hideDetail");
      } else {
        var array = path.split("/");
        this.showDetail(array[array.length - 1]);
      }
    });

  </script>
</swt-common>
