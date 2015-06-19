package com.example.user.sg50app;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PhotosFragment extends Fragment implements ActionBar.TabListener {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    public static ArrayList<ParseObject> mFHF = new ArrayList<>();
    public static ArrayList<ParseObject> mDAS = new ArrayList<>();
    public static ArrayList<ParseObject> mBOP = new ArrayList<>();
    public static ArrayList<ParseObject> mTop = new ArrayList<>();
    public static  Boolean finishLoad = false;

    public PhotosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        final View view = inflater.inflate(R.layout.fragment_photos, container, false);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(PhotosFragment.this));
        }

        new UpdateTask().execute();

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    new TopPhotosFragment();
                case 1:
                    new BestOfPastFragment();
                case 2:
                    new DayAsSingaporeanFragment();
                case 3:
                    new FutureHopesFragment();
                default:
                    return new Fragment();
            }
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.photo_title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.photo_title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.photo_title_section3).toUpperCase(l);
                case 3:
                    return getString(R.string.photo_title_section4).toUpperCase(l);
            }
            return null;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.photos, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_postNew) {
            Intent intent = new Intent(getActivity(), PostNewActivity.class);
            PhotosFragment.this.startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private class UpdateTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute(){
            mTop.clear();
            mFHF.clear();
            mBOP.clear();
            mDAS.clear();
        }
        @Override
        protected Boolean doInBackground(Void... Params) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("allPostings");
            query.addDescendingOrder("likeNumber");
            query.setLimit(15);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (e == null) {
                        for (int j = 0; j < parseObjects.size(); j++) {
                            mTop.add(parseObjects.get(j));
                            if (mTop.size() == 6) {
                                break;
                            }
                        }

                    }
                }
            });
            ParseQuery<ParseObject> query2 = ParseQuery.getQuery("allPostings");
            query2.addDescendingOrder("createdAt");
            query2.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    for (int j = 0; j < list.size(); j++) {
                        String category = list.get(j).getString("category");
                        switch (category) {
                            case "BestOfPast":
                                if (mBOP.size() != 6) mBOP.add(list.get(j));
                                break;
                            case "DayAsSGean":
                                if (mDAS.size() != 6) mDAS.add(list.get(j));
                                break;
                            case "FutureHopes":
                                if (mFHF.size() != 6) mFHF.add(list.get(j));
                                break;
                        }

                    }
                }
            });

            return true;
        }
    }

    /*Handles refreshing //TODO:AD's working on this halfway
    public void timeToRefresh(){
        if(pastFrag!=null) pastFrag.refresh();
        if(presentFrag!=null) presentFrag.refresh();
        if(futureFrag!=null) futureFrag.refresh();
        if(topFrag!=null) topFrag.refresh();
    }*/
}
