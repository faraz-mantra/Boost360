package com.nowfloats.Login;

public class LoginManager {

    private static LoginManager loginManager;
    private AuthLoginInterface listener;

    public static LoginManager getInstance() {
        if (loginManager == null) loginManager = new LoginManager();

        return loginManager;
    }

    public void setListener(AuthLoginInterface listene) {
        this.listener = listene;
    }

    public AuthLoginInterface getListener() {
        return listener;
    }

}
