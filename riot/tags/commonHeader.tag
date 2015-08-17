<commonheader>

    <header class="sg-header">
      <ul class="sg-header-contents">
          <li>
              <h1><a href="/">SAW Twitter</a></h1>
          </li>
          <li class="sg-header-post" if={opts.isLogin}>
            <postshare></postshare>
          </li>
          <li class="sg-header-menu" if={opts.isLogin}>
              <a href="#" onclick={doSignOut}>Sign out</a>
          </li>
      </ul>
    </header>

    <script>
        // ===================================================================================
        //                                                                             Declare
        //                                                                             =======

        declare var riot: any;
        declare var opts: any;
        declare var _: any;
        interface Window {
          superagent: any;
        }

        // ===================================================================================
        //                                                                          Attributes
        //                                                                          ==========

        var request = window.superagent;
        this.member = null;
        this.isLogin = opts.isLogin;
        this.observable = riot.observable();

        // ===================================================================================
        //                                                                               Event
        //                                                                               =====

        request
          .get("http://google.com")
          .end((error, response) => {
            console.log(response.text);
          });

        this.doSignOut = e => {
          e.preventDefault();
          request
            .post("/api/auth/signout")
            .withCredentials()
            .end((error, response) => {
              if (response.ok) {
                location.href = "/";
              }
            });
        };

        // ===================================================================================
        //                                                                               Logic
        //                                                                               =====

        var memberKeyPrefix = "member-";

        this.findMemberDetail = (memberId) => {
          var existsData = localStorage.getItem(memberKeyPrefix + memberId);
          if (existsData) {
            this.member = JSON.parse(existsData);
            this.observable.trigger("onLoadMember", this.member);
          } else {
            request
              .get("/api/member/detail/" + memberId)
              .end((error, response) => {
                if (response.ok) {
                  this.member = JSON.parse(response.text).value;
                  this.observable.trigger("onLoadMember", this.member);
                  localStorage.setItem(memberKeyPrefix + memberId, JSON.stringify(this.member));
                }
            });
          }
        };

        this.findMemberDetailList = (memberIds) => {
          var unknownIds = _
            .chain(memberIds)
            .uniq()
            .filter((memberId) => {
              var existsData = localStorage.getItem(memberKeyPrefix + memberId);
              return !existsData;
            })
            .value();

          // request
          if (unknownIds.length > 0) {
            request
              .post("/api/member/details")
              .send({memberIdList: unknownIds})
              .end((error, response) => {
                if (response.ok) {
                  var memberJsons = JSON.parse(response.text).value;
                  memberJsons.forEach((json) => {
                    var jsonStr = JSON.stringify(json);
                    localStorage.setItem(memberKeyPrefix + json.memberId, jsonStr);
                  });
                }
              });
          }

          // event fire
          var memberDetailList = _
            .chain(memberIds)
            .uniq()
            .map((memberId) => {
              return localStorage.getItem(memberKeyPrefix + memberId);
            })
            .value();
          this.observable.trigger("onLoadMemberList", memberDetailList);
        }

        this.findLoginMemberDetail = () => {
          if (!opts.isLogin) {
            return;
          }

          var loginMemberKey = "loginMember";
          var existsData = localStorage.getItem(loginMemberKey);

          if (existsData) {
            this.loginMember = JSON.parse(existsData);
            this.observable.trigger("onLoadLoginMember", this.loginMember);
          } else {
            request
              .get("/api/auth/member/detail")
              .withCredentials()
              .end((error, response) => {
                if (response.ok) {
                  this.loginMember = JSON.parse(response.text).value;
                  this.observable.trigger("onLoadLoginMember", this.loginMember);
                  var loginMemberJsonStr = JSON.stringify(this.loginMember);
                  localStorage.setItem(loginMemberKey, loginMemberJsonStr);
                  localStorage.setItem(memberKeyPrefix + this.loginMember.memberId, loginMemberJsonStr);
                }
              });
          }
        };

        this.findTweetDetail = (tweetId) =>  {
          request
            .get("/api/tweet/detail/" + tweetId)
            .end((error, response) => {
              if(response.ok) {
                var result = JSON.parse(response.text);
                this.tweet = result.value;
                this.observable.trigger("onLoadTweet", this.tweet);
              }
            });
        };

        this.findTimeline = (memberId, before, after) => {
          // set target
          var url = "/api/timeline/";
          if (memberId == null) {
            url += "home";
          } else {
            url += "member/" + memberId;
          }

          // set url parameter
          var existsParameter = false;
          var addParameter = (key, value) => {
            if (!existsParameter) {
              url += "?";
              existsParameter = true;
            } else {
              url += "&";
            }
            url += key + "=" + value;
          }
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
                this.observable.trigger("onLoadTimeline", timeline);
              }
            });
        }

        // ===================================================================================
        //                                                                               Mixin
        //                                                                               =====

        this.mixin({
          timeline: {
            targetId: null
          }
        });
    </script>

</commonheader>
