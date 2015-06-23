package com.example.user.sg50app;

import android.app.Application;

import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
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
