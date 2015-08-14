<profileaside>

  <aside class="pg-profile">
        <img src="/assets/icon/1">
        <h2>{member.displayName}</h2>
        <p>{member.biography}</p>
        <dl>
            <dt>Followings</dt>
            <dd><a href="/following/{member.memberId}">{member.following.count}</a></dd>
            <dt>Followers</dt>
            <dd><a href="/followers/{member.memberId}">{member.followers.count}</a></dd>
        </dl>
    </aside>

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
    this.member = (opts.timeline.targetId == null) ? opts.loginMember : opts.member;

    // ===================================================================================
    //                                                                               Event
    //                                                                               =====

    if (opts.observable != undefined) {
      opts.observable.on("onLoadLoginMember", loginMember => {
        this.member = loginMember;
        this.update();
      });

      opts.observable.on("onLoadMember", member => {
        this.member = member;
        this.update();
      });
    }
  </script>

</profileaside>
