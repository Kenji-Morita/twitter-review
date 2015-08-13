<commonheader>

    <header class="sg-header">
        <div class="sg-container">
            <ul class="sg-header-contents">
                <li>
                    <h1><a href="/">SAW Twitter</a></h1>
                </li>
                <li if={opts.isLogin}>
                    <a href="#">Setting</a>
                    <ul>
                        <li><a href="#">Setting</a></li>
                        <li><a href="#" onclick={doSignOut}>Sign out</a></li>
                    </ul>
                </li>
            </ul>
        </div>
    </header>

    <script>
        // ===================================================================================
        //                                                                             Declare
        //                                                                             =======

        declare var riot: any;
        declare var opts: any;
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
        }

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

        this.loaded = () => {
          if (opts.isLogin) {
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
          }
        };

        // ===================================================================================
        //                                                                               Mixin
        //                                                                               =====

        this.mixin({
          profile: {
            loginMember: true,
            memberId: null
          },
          timeline: {
            target: "home"
          }
        });
    </script>

</commonheader>
