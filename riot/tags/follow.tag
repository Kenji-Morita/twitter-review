<follow>
  <div if={opts.loginInfo.isLogin}>
    <button if={!isFollowing} onclick={doFollow}>Follow</button>
    <button if={isFollowing} onclick={doUnFollow}>Unfollow</button>
  </div>

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
    this.isFollowing = false;
    if (opts.loginInfo != undefined && opts.loginInfo.isLogin) {
      if (opts.loginInfo.member.following.list.indexOf(opts.memberId) >= 0) {
        this.isFollowing = true;
      }
    }

    // ===================================================================================
    //                                                                               Event
    //                                                                               =====
    this.doFollow = e => {
      e.preventDefault();
      request
        .post("/api/member/follow/" + opts.memberId)
        .withCredentials()
        .end((error, response) => {
          if(response.ok) {
            this.isFollowing = true;
            this.update();
          }
        });
    };

    this.doUnFollow = e => {
      e.preventDefault();
      request
        .del("/api/member/unfollow/" + opts.memberId)
        .withCredentials()
        .end((error, response) => {
          if(response.ok) {
            this.isFollowing = false;
            this.update();
          } else {
            console.log(response.text);
          }
        });
    };
  </script>
</follow>
