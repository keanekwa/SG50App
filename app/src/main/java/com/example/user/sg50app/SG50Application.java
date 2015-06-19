package com.example.user.sg50app;

import android.app.Application;
import com.parse.Parse;

public class SG50Application extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "hfWJdDBsiUCPMxZJFxsnFTrrJmCWaQpySq0J7e8j", "wKqjsnCWrNf5jFk0mXLb9nI9JpsLjSDuyTpdLsP9");
    }
}
