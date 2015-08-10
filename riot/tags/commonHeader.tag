<commonheader>

    <header class="sg-header">
        <div class="sg-container">
            <ul class="sg-header-contents">
                <li>
                    <h1>SAW Twitter</h1>
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
        interface Window {
          superagent: any;
        }

        // ===================================================================================
        //                                                                          Attributes
        //                                                                          ==========
        var request = window.superagent;

        // ===================================================================================
        //                                                                               Event
        //                                                                               =====
        this.doSignOut = e => {
          e.preventDefault();
          request
            .post("api/auth/signout")
            .withCredentials()
            .end((error, response) => {
              if (response.ok) {
                location.reload();
              }
            });
        }

        // ===================================================================================
        //                                                                               Mixin
        //                                                                               =====
        this.mixin({
          observable: riot.observable(),
        });
    </script>

</commonheader>
