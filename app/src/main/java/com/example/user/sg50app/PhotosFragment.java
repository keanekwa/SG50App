package com.example.user.sg50app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PhotosFragment extends Fragment implements ActionBar.TabListener {

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
        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        final ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#e74c3c")));
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#e74c3c")));
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);



        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mViewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager()));

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


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
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
