<follow>
  <div if={opts.isLogin && !isMe}>
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
    this.isMe = false;

    // ===================================================================================
    //                                                                               Event
    //                                                                               =====
    if (opts.observable != undefined) {
      opts.observable.on("onLoadMember", member => {
        opts.member = member;
        if (opts.isLogin) {
          if (opts.loginMember.following.list.indexOf(member.memberId) >= 0) {
            this.isFollowing = true;
          }
          this.isMe = opts.loginMember.memberId == member.memberId;
          this.update();
        }
      });
    }

    this.doFollow = e => {
      e.preventDefault();
      request
        .post("/api/member/follow/" + opts.member.memberId)
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
        .del("/api/member/unfollow/" + opts.member.memberId)
        .withCredentials()
        .end((error, response) => {
          if(response.ok) {
            this.isFollowing = false;
            this.update();
          }
        });
    };
  </script>
</follow>
