<profileaside>

  <aside class="pg-profile">
        <img src="/assets/icon/1">
        <h2>{opts.member.displayName}</h2>
        <p>{opts.member.profile.biography}</p>
        <dl>
            <dt>Followings</dt>
            <dd><a href="/following/{opts.member.memberId}">{opts.member.following.count}</a></dd>
            <dt>Followers</dt>
            <dd><a href="/followers/{opts.member.memberId}">{opts.member.followers.count}</a></dd>
        </dl>
    </aside>

  <script>
    // ===================================================================================
    //                                                                             Declare
    //                                                                             =======
    declare var opts: any;

    // ===================================================================================
    //                                                                             Declare
    //                                                                             =======
  </script>

</profileaside>
