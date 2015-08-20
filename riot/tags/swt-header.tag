<swt-header>
  <header class="sg-header">
    <ul>
      <li class="sg-header-logo">
        <h1>
          <a href="/"><i class="fa fa-user-secret fa"></i> Sawitter</a>
        </h1>
      </li>
      <li if={sawitter.isLogin} class="sg-header-tweet">
        <a href="#" onclick={tweetNews}>
          <i class="fa fa-pencil-square-o"></i>
        </a>
      </li>
      <li class="sg-header-signs">
        <ul>
          <li if={!sawitter.isLogin} onclick={onSignin} class="sg-header-signin"><button>サインイン</button></li>
          <li if={!sawitter.isLogin} onclick={onSignup} class="sg-header-signup"><button>登録</button></li>
          <li if={sawitter.isLogin} onclick={onSignout} class="sg-header-signout"><button>サインアウト</button></li>
        </ul>
      </li>
    </ul>
  </header>

  <form name="signin" class="sg-header-signs-signin" if={false}>
    <label>メールアドレス</label>
    <input type="text" name="signinMail" placeholder="メールアドレス">
    <label>パスワード(6~32桁の英数小大文字)</label>
    <input type="password" name="signinPassword" placeholder="6~32桁の英数小大文字">
  </form>

  <form name="signup" class="sg-header-signs-signup" if={false}>
    <label>メールアドレス</label>
    <input type="text" name="signupMail" placeholder="メールアドレス">
    <label>パスワード</label>
    <input type="password" name="signupPassword" placeholder="6~32桁の英数小大文字">
    <label>パスワード(再確認)</label>
    <input type="password" name="signupPasswordConfirm" placeholder="6~32桁の英数小大文字">
  </form>

  <script>

    // ===================================================================================
    //                                                                             Declare
    //                                                                             =======

    declare var sawitter: any;
    declare var opts: any;

    // ===================================================================================
    //                                                                               Event
    //                                                                               =====

    this.tweetNews = e => {
      e.preventDefault();
      var runTime = 10;
      var fps = 60;
      var diffY = window.scrollY / fps;
      var scrollToTop = () => {
        var currentY = window.scrollY;
        var targetY = currentY - diffY;
        var targetY = targetY <= 0 ? 0 : targetY;
        if (currentY > 0) {
          window.scrollTo(0, targetY);
          if (targetY > 0) {
            setTimeout(scrollToTop, runTime / fps);
          }
        }
      };
      scrollToTop();
    };

    this.onSignin = e => {
      e.preventDefault();
      sawitter.obs.trigger("showModal", {
        title: "サインイン",
        raw: this.signin.innerHTML,
        okButtonMsg: "サインイン",
        ngButtonMsg: "キャンセル",
        ok: raw => {
          var mail = raw.querySelector('input[name="signinMail"]').value.trim();
          var password = raw.querySelector('input[name="signinPassword"]').value.trim();
          if (mail == "") {
            alert("メールアドレスが入力されていません");
            return;
          }
          if (password == "") {
            alert("パスワードが入力されていません");
            return;
          }
          sawitter.doSignIn(mail, password);
        },
        ng: raw => {
          sawitter.obs.trigger("hideModal");
        }
      });
    };

    this.onSignup = e => {
      e.preventDefault();
      sawitter.obs.trigger("showModal", {
        title: "登録",
        raw: this.signup.innerHTML,
        okButtonMsg: "登録",
        ngButtonMsg: "キャンセル",
        ok: raw => {
          var mail = raw.querySelector('input[name="signupMail"]').value.trim();
          var password = raw.querySelector('input[name="signupPassword"]').value.trim();
          var passwordConfirm = raw.querySelector('input[name="signupPasswordConfirm"]').value.trim();
          if (mail == "") {
            alert("メールアドレスが入力されていません");
            return;
          }
          if (password == "") {
            alert("パスワードが入力されていません");
            return;
          }
          if (passwordConfirm == "") {
            alert("パスワード(再確認)が入力されていません");
            return;
          }
          sawitter.doSignUp(mail, password, passwordConfirm);
        },
        ng: raw => {
          sawitter.obs.trigger("hideModal");
        }
      });
    };

    this.onSignout = e => {
      e.preventDefault();
      sawitter.doSignOut();
    };

  </script>
</swt-header>
