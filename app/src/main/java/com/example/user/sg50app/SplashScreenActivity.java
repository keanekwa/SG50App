package com.example.user.sg50app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class SplashScreenActivity extends Activity {

    private Boolean hasTopLoaded = false;
    private Boolean hasPastLoaded = false;
    private Boolean hasPresentLoaded = false;
    private Boolean hasFutureLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (ParseUser.getCurrentUser()==null){
            Intent intent = new Intent (SplashScreenActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        else {
            //LOADING FOR PHOTOS FRAGMENT
            if (PhotosFragment.mTOP==null) PhotosFragment.mTOP = new ArrayList<>();
            else PhotosFragment.mTOP.clear();
            if (PhotosFragment.mPAST==null) PhotosFragment.mPAST = new ArrayList<>();
            else PhotosFragment.mPAST.clear();
            if (PhotosFragment.mPRESENT==null) PhotosFragment.mPRESENT = new ArrayList<>();
            else PhotosFragment.mPRESENT.clear();
            if (PhotosFragment.mFUTURE==null) PhotosFragment.mFUTURE = new ArrayList<>();
            else PhotosFragment.mFUTURE.clear();

            ParseQuery<ParseObject> query = ParseQuery.getQuery("allPostings");
            query.addDescendingOrder("likeNumber");
            query.setLimit(15);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (e == null) {
                        for (int j = 0; j < parseObjects.size(); j++) {
                            PhotosFragment.mTOP.add(parseObjects.get(j));
                            if (PhotosFragment.mTOP.size() == 6) {
                                hasTopLoaded = true;
                                ParseQuery<ParseObject> query2 = ParseQuery.getQuery("allPostings");
                                query2.addDescendingOrder("createdAt");
                                query2.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> list, ParseException e) {
                                        for (int j = 0; j < list.size(); j++) {
                                            String category = list.get(j).getString("category");
                                            switch (category) {
                                                case "BestOfPast":
                                                    if (PhotosFragment.mPAST.size() < 6) PhotosFragment.mPAST.add(list.get(j));
                                                    break;
                                                case "DayAsSGean":
                                                    if (PhotosFragment.mPRESENT.size() < 6) PhotosFragment.mPRESENT.add(list.get(j));
                                                    break;
                                                case "FutureHopes":
                                                    if (PhotosFragment.mFUTURE.size() < 6) PhotosFragment.mFUTURE.add(list.get(j));
                                                    break;
                                            }

                                        }
                                        loadUserContentContent();
                                    }
                                });
                                break;
                            }
                        }

                    }
                }
            });
        }
    }

    private void loadUserContentContent(){
        UserContentFragment.mPHOTOS = new ArrayList<>();
        UserContentFragment.mWISHES = new ArrayList<>();
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("allPostings");
        query.addDescendingOrder("createdAt");
        query.whereEqualTo("createdBy", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    for (int j = 0; j < parseObjects.size(); j++) {
                        UserContentFragment.mPHOTOS.add(parseObjects.get(j));
                    }
                    final ParseQuery<ParseObject> query2 = ParseQuery.getQuery("onNationalDay");
                    query2.addDescendingOrder("createdAt");
                    query2.whereEqualTo("createdBy", ParseUser.getCurrentUser().getUsername());
                    query2.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> parseObjects, ParseException e) {
                            if (e == null) {
                                for (int j = 0; j < parseObjects.size(); j++) {
                                    UserContentFragment.mWISHES.add(parseObjects.get(j));
                                }
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("onNationalDay");
                                query.addDescendingOrder("createdAt");
                                query.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> parseObjects, ParseException e) {
                                        if (e == null) {
                                            for (int j = 0; j < parseObjects.size(); j++) {
                                                OnNatDayFragment.mPosts.add(parseObjects.get(j));
                                            }
                                            Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                                            startActivity(i);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }
}
