package com.example.user.sg50app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PhotosFragment extends Fragment {

    public static String TOP_PHOTOS_STRING = "Top Photos";
    public static String BEST_OF_PAST_STRING = "Best Of The Past";
    public static String DAY_AS_A_SINGAPOREAN_STRING = "A Day As A Singaporean";
    public static String FUTURE_HOPES_STRING = "Future Hopes For Singapore";
    public static ArrayList<String> LIST_OF_PAGES;

    public static ArrayList<ParseObject> mTOP;
    public static ArrayList<ParseObject> mPAST;
    public static ArrayList<ParseObject> mPRESENT;
    public static ArrayList<ParseObject> mFUTURE;

    private ListView mListView;
    private String currentPage;
    private ProgressBar loading;

    private Button topButton;
    private Button pastButton;
    private Button presentButton;
    private Button futureButton;

    private String toSortTopBy;

    public static PhotosFragment newInstance() {
        return new PhotosFragment();
    }

    public PhotosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        mListView = (ListView) view.findViewById(R.id.imgListView);
        topButton = (Button)view.findViewById(R.id.topPhotosButton);
        pastButton = (Button)view.findViewById(R.id.pastButton);
        presentButton = (Button)view.findViewById(R.id.presentButton);
        futureButton = (Button)view.findViewById(R.id.futureButton);
        loading = (ProgressBar)view.findViewById(R.id.photosLoadingPb);
        FloatingActionButton fabImageButton = (FloatingActionButton) view.findViewById(R.id.action_a);
        FloatingActionButton searchButton = (FloatingActionButton)view.findViewById(R.id.action_b);
        FloatingActionButton sortImageButton = (FloatingActionButton)view.findViewById(R.id.action_c);
        MainActivity.origin = "PF";
        toSortTopBy = "likeNumber";

        topButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentPage(TOP_PHOTOS_STRING);
            }
        });
        pastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentPage(BEST_OF_PAST_STRING);
            }
        });
        presentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentPage(DAY_AS_A_SINGAPOREAN_STRING);
            }
        });
        futureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentPage(FUTURE_HOPES_STRING);
            }
        });

        if (LIST_OF_PAGES==null){
            LIST_OF_PAGES = new ArrayList<>();
            LIST_OF_PAGES.add(TOP_PHOTOS_STRING);
            LIST_OF_PAGES.add(BEST_OF_PAST_STRING);
            LIST_OF_PAGES.add(DAY_AS_A_SINGAPOREAN_STRING);
            LIST_OF_PAGES.add(FUTURE_HOPES_STRING);
        }
        if (currentPage == null) {
            currentPage = TOP_PHOTOS_STRING;
            setTabAsSelected(topButton, true);
        }

        Boolean fromSplashScreen = true;
        if (mTOP == null) {
            mTOP = new ArrayList<>();
            fromSplashScreen = false;
        }
        if (mPAST == null) {
            mPAST = new ArrayList<>();
            fromSplashScreen = false;
        }
        if (mPRESENT == null) {
            mPRESENT = new ArrayList<>();
            fromSplashScreen = false;
        }
        if (mFUTURE == null) {
            mFUTURE = new ArrayList<>();
            fromSplashScreen = false;
        }
        if (fromSplashScreen){
            setPhotosList();
        }
        else loadPhotos(true);

        fabImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getActivity(),PostNewActivity.class);
                startActivity(intent);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, SearchFragment.newInstance("PF")).commit();
            }
        });

        sortImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListSelectorDialog dlg = new ListSelectorDialog(getActivity(), "Sort By");
                String[] list;
                String[] list2;
                if(currentPage.equals(TOP_PHOTOS_STRING)) {
                    list = new String[]{"Recent", "Popularity"};
                    list2 = new String[]{"recent", "likes"};
                }
                else{
                    list = new String[]{"Alphabetical", "Recent", "Popularity"};
                    list2 = new String[]{"title", "recent", "likes"};
                }
                dlg.show(list, list2, new ListSelectorDialog.listSelectorInterface() {
                    public void selectorCanceled() {
                        //Bloop
                    }
                    public void selectedItem(String key, String item) {
                        sortList(key);
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }



    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void sortList(String key){
        loading.setVisibility(View.VISIBLE);
        switch (currentPage){
            case "Top Photos":
                switch (key){
                    case "recent":
                        toSortTopBy = "createdAt";
                        topButton.setText("Recent Photos");
                        break;
                    case "likes":
                        toSortTopBy = "likeNumber";
                        topButton.setText("Top Photos");
                        break;
                }
                loadPhotos(true);
                break;
            case "Best Of The Past":
                switch (key){
                    case "title":
                        Collections.sort(mPAST, new TitleComparator());
                        break;
                    case "recent":
                        Collections.sort(mPAST, new RecentComparator());
                        break;
                    case "likes":
                        Collections.sort(mPAST, new LikesComparator());
                        break;
                }
                setPhotosList();
                break;
            case "A Day As A Singaporean":
                switch (key){
                    case "title":
                        Collections.sort(mPRESENT, new TitleComparator());
                        break;
                    case "recent":
                        Collections.sort(mPRESENT, new RecentComparator());
                        break;
                    case "likes":
                        Collections.sort(mPRESENT, new LikesComparator());
                        break;
                }
                setPhotosList();
                break;
            case "Future Hopes For Singapore":
                switch (key){
                    case "title":
                        Collections.sort(mFUTURE, new TitleComparator());
                        break;
                    case "recent":
                        Collections.sort(mFUTURE, new RecentComparator());
                        break;
                    case "likes":
                        Collections.sort(mFUTURE, new LikesComparator());
                        break;
                }
                setPhotosList();
                break;
        }
        loading.setVisibility(View.GONE);
    }



    public void setCurrentPage(String pageToSet){
        if (!LIST_OF_PAGES.contains(pageToSet)) {
            currentPage = TOP_PHOTOS_STRING;
            pageToSet = TOP_PHOTOS_STRING;
        }

        if (!pageToSet.equals(currentPage)){
            currentPage = pageToSet;
            setPhotosList();
        }

        switch (pageToSet){
            case "Top Photos":
                setTabAsSelected(topButton, true);
                setTabAsSelected(pastButton, false);
                setTabAsSelected(presentButton, false);
                setTabAsSelected(futureButton, false);
                break;
            case "Best Of The Past":
                setTabAsSelected(topButton, false);
                setTabAsSelected(pastButton, true);
                setTabAsSelected(presentButton, false);
                setTabAsSelected(futureButton, false);
                break;
            case "A Day As A Singaporean":
                setTabAsSelected(topButton, false);
                setTabAsSelected(pastButton, false);
                setTabAsSelected(presentButton, true);
                setTabAsSelected(futureButton, false);
                break;
            case "Future Hopes For Singapore":
                setTabAsSelected(topButton, false);
                setTabAsSelected(pastButton, false);
                setTabAsSelected(presentButton, false);
                setTabAsSelected(futureButton, true);
                break;
        }
    }

    public void setTabAsSelected (Button button, boolean isSelected) {
        if (isSelected) {
            button.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_bottom_border));
            button.setTextColor(getResources().getColor(R.color.white));
        }
        else {
            button.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
            button.setTextColor(getResources().getColor(R.color.translucent_white));
        }
    }

    public void setPhotosList() {
        if(getActivity()==null) return;
        loading.setVisibility(View.VISIBLE);
        PhotosAdapter adapter;
        switch(currentPage) {
            case "Top Photos":
                adapter = new PhotosAdapter(getActivity(), R.layout.photos_list, mTOP);
                mListView.setAdapter(adapter);
                break;
            case "Best Of The Past":
                adapter = new PhotosAdapter(getActivity(), R.layout.photos_list, mPAST);
                mListView.setAdapter(adapter);
                break;
            case "A Day As A Singaporean":
                adapter = new PhotosAdapter(getActivity(), R.layout.photos_list, mPRESENT);
                mListView.setAdapter(adapter);
                break;
            case "Future Hopes For Singapore":
                adapter = new PhotosAdapter(getActivity(), R.layout.photos_list, mFUTURE);
                mListView.setAdapter(adapter);
                break;
        }
        loading.setVisibility(View.GONE);
        //lvToShow.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
    }

    public void loadPhotos(final Boolean toSetList) {
        if(toSortTopBy==null) toSortTopBy = "likeNumber";
        loading.setVisibility(View.VISIBLE);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("allPostings");
        query.addDescendingOrder(toSortTopBy);
        query.setLimit(15);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    mTOP.clear();
                    for (int j = 0; j < parseObjects.size(); j++) {
                        mTOP.add(parseObjects.get(j));
                        if (mTOP.size() == 15) {
                            if (currentPage.equals(TOP_PHOTOS_STRING) && toSetList) setPhotosList();
                            ParseQuery<ParseObject> query2 = ParseQuery.getQuery("allPostings");
                            query2.addDescendingOrder("createdAt");
                            query2.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> list, ParseException e) {
                                    mPAST.clear();
                                    mPRESENT.clear();
                                    mFUTURE.clear();
                                    for (int j = 0; j < list.size(); j++) {
                                        String category = list.get(j).getString("category");
                                        switch (category) {
                                            case "BestOfPast":
                                                mPAST.add(list.get(j));
                                                break;
                                            case "DayAsSGean":
                                                mPRESENT.add(list.get(j));
                                                break;
                                            case "FutureHopes":
                                                mFUTURE.add(list.get(j));
                                                break;
                                        }
                                    }
                                    if (toSetList) setPhotosList();
                                }
                            });
                            break;
                        }
                    }

                }
            }
        });
    }

    private class PhotosAdapter extends ArrayAdapter<ParseObject> {
        //creating variables
        private int mResource;
        private ArrayList<ParseObject> mTopPics;

        public PhotosAdapter(Context context, int resource, ArrayList<ParseObject> topPics) {
            super(context, resource, topPics);
            mResource = resource;
            mTopPics = topPics;
        }

        //display subject data in every row of listView
        @Override
        public View getView(final int position, View row, ViewGroup parent) {
            if (row == null) {
                row = LayoutInflater.from(getContext()).inflate(mResource, parent, false);
            }

            final ParseObject currentTopImage = mTopPics.get(position);
            TextView titleTextView = (TextView) row.findViewById(R.id.imgTitle);
            titleTextView.setText(currentTopImage.getString("imgTitle"));
            TextView subtitleTextView = (TextView) row.findViewById(R.id.photoBy);
            subtitleTextView.setText(getString(R.string.photo_by) + getString(R.string.space) + currentTopImage.getString("createdBy"));
            TextView categoryTextView = (TextView) row.findViewById(R.id.imageCategory);
            String categoryString = getString(R.string.nothing);
            if (currentTopImage.getString("category").matches(getString(R.string.best_of_past_category))) {
                categoryString = getString(R.string.photo_title_section2);
            }
            else if (currentTopImage.getString("category").matches(getString(R.string.day_as_sgean_category))) {
                categoryString = getString(R.string.photo_title_section3);
            }
            else if (currentTopImage.getString("category").matches(getString(R.string.future_hopes_category))) {
                categoryString = getString(R.string.photo_title_section4);
            }
            categoryTextView.setText("Category:" + getString(R.string.space) + categoryString);

            //set like button status on create
            final ImageView likeImageView = (ImageView) row.findViewById(R.id.likeImageView);
            final TextView likeNumberTextView = (TextView) row.findViewById(R.id.likeNumber);
            likeNumberTextView.setText(String.valueOf(currentTopImage.getInt("likeNumber")) + getString(R.string.space) + getString(R.string.likes));
            final ParseUser mCurrentUser = ParseUser.getCurrentUser();
            ArrayList<ParseUser> mFirstWhoLikedList = (ArrayList) currentTopImage.get("likeImgPeople");
            if (mFirstWhoLikedList == null) {
                mFirstWhoLikedList = new ArrayList<>();
            }
            boolean hasLiked = false;
            for (int i = 0; i < mFirstWhoLikedList.size(); i++) {
                if (mFirstWhoLikedList.get(i) == mCurrentUser) {
                    hasLiked = true;
                    break;
                }
            }
            if (hasLiked) {
                likeImageView.setImageDrawable(getResources().getDrawable(R.drawable.like_icon));
            }
            else {
                likeImageView.setImageDrawable(getResources().getDrawable(R.drawable.like_outline));
            }

            //when like button is clicked
            likeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<ParseUser> mWhoLikedList = (ArrayList) currentTopImage.get("likeImgPeople");
                    if (mWhoLikedList == null) {
                        mWhoLikedList = new ArrayList<>();
                    }
                    boolean hasLiked = false;
                    for (int i = 0; i < mWhoLikedList.size(); i++) {
                        if (mWhoLikedList.get(i) == mCurrentUser) {
                            hasLiked = true;
                            break;
                        }
                    }
                    if (hasLiked) {
                        likeImageView.setImageDrawable(getResources().getDrawable(R.drawable.like_outline));
                        currentTopImage.put("likeNumber", (currentTopImage.getInt("likeNumber") - 1));
                        mWhoLikedList.remove(ParseUser.getCurrentUser());
                        currentTopImage.put("likeImgPeople", mWhoLikedList);
                    } else {
                        likeImageView.setImageDrawable(getResources().getDrawable(R.drawable.like_icon));
                        currentTopImage.put("likeNumber", (currentTopImage.getInt("likeNumber") + 1));
                        mWhoLikedList.add(ParseUser.getCurrentUser());
                        currentTopImage.put("likeImgPeople", mWhoLikedList);
                    }
                    currentTopImage.saveInBackground();
                    likeNumberTextView.setText(String.valueOf(currentTopImage.getInt("likeNumber")) + getString(R.string.space) + getString(R.string.likes));
                }
            });

            ParseFile fileObject = currentTopImage.getParseFile("actualImage");
            final ImageView actualImage = (ImageView) row.findViewById(R.id.topImgView);
            fileObject.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        Bitmap bmp = BitmapFactory
                                .decodeByteArray(
                                        data, 0,
                                        data.length);
                        actualImage.setImageBitmap(bmp);
                    }
                }
            });

            actualImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, IndividualPhotoFragment.newInstance(currentTopImage,"PF")).commit();
                }
            });

            return row;
        }
    }

    public class RecentComparator implements Comparator<ParseObject> {
        @Override
        public int compare(ParseObject o1, ParseObject o2) {
            return o2.getCreatedAt().compareTo(o1.getCreatedAt());
        }
    }

    public class LikesComparator implements Comparator<ParseObject> {
        @Override
        public int compare(ParseObject o1, ParseObject o2) {
            return o2.getInt("likeNumber") - o1.getInt("likeNumber");
        }
    }

    public class TitleComparator implements Comparator<ParseObject> {
        @Override
        public int compare(ParseObject o1, ParseObject o2) {
            return o1.getString("imgTitle").compareTo(o2.getString("imgTitle"));
        }
    }
}
