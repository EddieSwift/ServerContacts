package com.eddie.servercontacts;

import android.app.Application;

public class App extends Application {

    public static final String MY_TAG = "MY_TAG";

    @Override
    public void onCreate() {
        super.onCreate();
        StoreProvider.getInstance().setContext(this);
    }
}
