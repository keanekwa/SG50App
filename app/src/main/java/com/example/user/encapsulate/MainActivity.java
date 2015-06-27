package com.example.user.encapsulate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


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
    private static PhotosFragment mPhotosFragment;
    private static DashboardFragment mDashboardFragment;
    private static UserContentFragment mUserContentFragment;
    private static OnNatDayFragment mOnNatDayFragment;
    private static VideosFragment mVideosFragment;
    public static Boolean YOrN = false;
    public static String origin;
    private int mCurrentFrag;
    private static Menu mMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCurrentFrag = 0;

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

        Intent infoIntent = getIntent();
        if(infoIntent.getBooleanExtra("toRefreshPhotos", false)){
            if(mPhotosFragment!=null) mPhotosFragment.loadPhotos(false);
            if(mUserContentFragment!=null) mUserContentFragment.refresh(false);
        }
        if(infoIntent.getIntExtra("goto", 0)!=0){
            int position = infoIntent.getIntExtra("goto", 0) - 1;
            onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Fragment newFragment = null;
        mCurrentFrag = position;

        switch (position) {
            case 0:
                mDashboardFragment = new DashboardFragment();
                newFragment = mDashboardFragment;
                mTitle = getString(R.string.title_section1);
                break;
            case 1:
                mUserContentFragment = new UserContentFragment();
                newFragment = mUserContentFragment;
                mTitle = getString(R.string.title_section2);
                break;

            case 2:
                mPhotosFragment = new PhotosFragment();
                newFragment = mPhotosFragment;
                mTitle = getString(R.string.title_section3);
                break;
            case 3:
                mOnNatDayFragment = new OnNatDayFragment();
                newFragment = mOnNatDayFragment;
                mTitle = getString(R.string.title_section4);
                break;
            case 4:
                mVideosFragment = new VideosFragment();
                newFragment = mVideosFragment;
                mTitle = getString(R.string.title_section5);
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
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            mMenu = menu;
            MenuItem backIcon = menu.findItem(R.id.action_back);
            if (YOrN){
                backIcon.setVisible(true);
            }
            else{
                backIcon.setVisible(false);
            }
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

        if (id == R.id.action_logout) {
            final ProgressDialog mLogoutLoader = new ProgressDialog(MainActivity.this);
            mLogoutLoader.setMessage(getString(R.string.logout_dialog_message));
            mLogoutLoader.setIndeterminate(true);
            mLogoutLoader.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_bar_red));
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

        if (id == R.id.action_refresh){
            if(mDashboardFragment!=null) mDashboardFragment.recount();
            if(mUserContentFragment!=null) {
                if(mCurrentFrag==1) mUserContentFragment.refresh(true);
                else mUserContentFragment.refresh(false);
            }
            if(mPhotosFragment!=null) {
                if(mCurrentFrag==2) mPhotosFragment.loadPhotos(true);
                else mPhotosFragment.loadPhotos(false);
            }
            if(mOnNatDayFragment!=null) {
                if(mCurrentFrag==3) mOnNatDayFragment.refreshOnNatDay(true);
                else mOnNatDayFragment.refreshOnNatDay(false);
            }
            if(mVideosFragment!=null) {
                if(mCurrentFrag==4) mVideosFragment.refreshVideos(true);
                else mVideosFragment.refreshVideos(false);
            }
        }


        return super.onOptionsItemSelected(item);
    }
}
