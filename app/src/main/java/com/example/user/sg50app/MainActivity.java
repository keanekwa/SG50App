package com.example.user.sg50app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ParseUser.getCurrentUser()==null){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Fragment newFragment = null;

        switch (position) {
            case 0:
                newFragment = new DashboardFragment();
                mTitle = getString(R.string.title_section1);
                break;
            case 1:
                newFragment = new DashboardFragment(); //TODO: PhotosFragment();
                mTitle = getString(R.string.title_section2);
                break;
            case 2:
                newFragment = new OnNatDayFragment();
                mTitle = getString(R.string.title_section3);
                break;
        }
        if (newFragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, newFragment).commit();
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.singapics_red)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            /* todo add settings activity Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            MainActivity.this.startActivity(intent);*/
        }

        if (id == R.id.action_logout) {
            final ProgressDialog mLogoutLoader = new ProgressDialog(MainActivity.this);
            mLogoutLoader.setMessage(getString(R.string.logout_dialog_message));
            mLogoutLoader.show();
            ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    mLogoutLoader.dismiss();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    MainActivity.this.startActivity(intent);
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    //Handles refreshing
    /*I'd rather put this in PhotosFragment, but the function of an interface HAS to be in an activity ._.
    public void timeToRefresh(){
        if(PhotosFragment.topPhotosFragment!=null) {
            PhotosFragment.mTop.clear();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("allPostings");
            query.addDescendingOrder("likeNumber");
            query.setLimit(15);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (e == null) {
                        for (int j = 0; j < parseObjects.size(); j++) {
                            PhotosFragment.mTop.add(parseObjects.get(j));
                            if (PhotosFragment.mTop.size() == 6) {
                                PhotosFragment.topPhotosFragment.refresh();
                                break;
                            }
                        }

                    }
                }
            });
        }

        if(PhotosFragment.dayAsSingaporeanFragment!=null && PhotosFragment.bestOfPastFragment!=null && PhotosFragment.futureHopesFragment!=null) {
            PhotosFragment.mFHF.clear();
            PhotosFragment.mBOP.clear();
            PhotosFragment.mDAS.clear();
            ParseQuery<ParseObject> query2 = ParseQuery.getQuery("allPostings");
            query2.addDescendingOrder("createdAt");
            query2.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    for (int j = 0; j < list.size(); j++) {
                        String category = list.get(j).getString("category");
                        switch (category) {
                            case "BestOfPast":
                                if (PhotosFragment.mBOP.size() != 6) PhotosFragment.mBOP.add(list.get(j));
                                else PhotosFragment.bestOfPastFragment.refresh();
                                break;
                            case "DayAsSGean":
                                if (PhotosFragment.mDAS.size() != 6) PhotosFragment.mDAS.add(list.get(j));
                                else PhotosFragment.dayAsSingaporeanFragment.refresh();
                                break;
                            case "FutureHopes":
                                if (PhotosFragment.mFHF.size() != 6) PhotosFragment.mFHF.add(list.get(j));
                                else PhotosFragment.futureHopesFragment.refresh();
                                break;
                        }

                    }
                }
            });
        }
    }*/
}
