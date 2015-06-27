package com.example.user.encapsulate;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {
    public static ArrayList<ParseObject> mRESULTS;
    ListView mListView;
    EditText searchText;
    private ProgressBar loading;
    ImageButton confirmButton;
    ImageButton backButton;
    String searchQuery;
    static String originator;
    RelativeLayout noResults;

    private OnSearchBackListener mListener;

    public static SearchFragment newInstance(String origin) {
        originator = origin;
        return new SearchFragment();
    }

    public SearchFragment() {
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        if(mRESULTS ==null) {
            mRESULTS = new ArrayList<>();
        }
        mListView = (ListView) view.findViewById(R.id.searchListView);
        loading = (ProgressBar) view.findViewById(R.id.progressBar3);
        loading.setVisibility(View.GONE);
        searchText = (EditText)view.findViewById(R.id.searchText);
        confirmButton = (ImageButton)view.findViewById(R.id.confirm_search);
        backButton = (ImageButton)view.findViewById(R.id.backArrow);
        noResults = (RelativeLayout)view.findViewById(R.id.noResults);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRESULTS.clear();
                searchQuery = searchText.getText().toString().toLowerCase();
                if(searchQuery.equals("")){
                    Toast.makeText(getActivity(),"Please enter text.",Toast.LENGTH_LONG).show();
                }
                else{
                searchText.setText("");
                    mRESULTS.clear();
                switch (originator) {
                    case "PF":
                        searchPhotos();
                        break;
                    case "VF":
                        searchVideos();
                        break;
                    case "WF":
                        searchWants();
                        break;
                }
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (originator) {
                    case "PF":
                        mListener.backTo(2);
                        break;
                    case "VF":
                        mListener.backTo(4);
                        break;
                    case "WF":
                        mListener.backTo(3);
                        break;
                }
            }
        });

        return view;
    }

    public void searchPhotos(){
        loading.setVisibility(View.VISIBLE);
        noResults.setVisibility(View.INVISIBLE);
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("allPostings");
        query.addDescendingOrder("createdAt");
        query.whereContains("imgTitle", searchQuery);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null && parseObjects.size()>0) {
                    for (int j = 0; j < parseObjects.size(); j++) {
                        mRESULTS.add(parseObjects.get(j));
                    }
                    setContentList();
                }
                else{
                    noResults.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.INVISIBLE);
                    query.cancel();
                }

            }
        });
    }

    public void searchVideos(){
        loading.setVisibility(View.VISIBLE);
        noResults.setVisibility(View.INVISIBLE);
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("videoList");
        query.addDescendingOrder("createdAt");
        query.whereContains("vidTitle", searchQuery);
        query.setLimit(10);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> videosList, ParseException e) {
                if (e == null && videosList.size()>0) {
                    for (int j = 0; j < videosList.size(); j++) {
                        mRESULTS.add(videosList.get(j));
                    }
                    setContentList();
                }
                else{
                    noResults.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.INVISIBLE);
                    query.cancel();
                }
            }
        });
    }

    public void searchWants(){
        loading.setVisibility(View.VISIBLE);
        noResults.setVisibility(View.INVISIBLE);
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("onNationalDay");
        query.addDescendingOrder("createdAt");
        query.whereContains("postTitle",searchQuery);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null && parseObjects.size()>0) {
                    for (int j = 0; j < parseObjects.size(); j++) {
                        mRESULTS.add(parseObjects.get(j));
                    }
                    setContentList();
                }
                else{
                    noResults.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.INVISIBLE);
                    query.cancel();
                }
            }
        });
    }

    public void setContentList() {
        loading.setVisibility(View.VISIBLE);
        switch (originator){
            case "PF":
                PhotoListAdapter adapter = new PhotoListAdapter(getActivity(), R.layout.photos_list, mRESULTS);
                mListView.setVisibility(View.VISIBLE);
                mListView.setAdapter(adapter);
                break;
            case "VF":
                VideoListAdapter adapter2 = new VideoListAdapter(getActivity(),R.layout.videos_list, mRESULTS);
                mListView.setVisibility(View.VISIBLE);
                mListView.setAdapter(adapter2);
                break;
            case "WF":
                wantAdapter adapter3 = new wantAdapter(getActivity(),R.layout.want_list, mRESULTS);
                mListView.setVisibility(View.VISIBLE);
                mListView.setAdapter(adapter3);
                break;
        }
        loading.setVisibility(View.GONE);
    }

    public interface OnSearchBackListener{
        public void backTo(int position);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSearchBackListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSearchBackListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private class PhotoListAdapter extends ArrayAdapter<ParseObject> {
        //creating variables
        private int mResource;
        private ArrayList<ParseObject> mTopPics;

        public PhotoListAdapter(Context context, int resource, ArrayList<ParseObject> topPics) {
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
            return row;
        }
    }

    private class VideoListAdapter extends ArrayAdapter<ParseObject> {
        private int mResource;
        private ArrayList<ParseObject> mTopPics;

        public VideoListAdapter(Context context, int resource, ArrayList<ParseObject> topPics) {
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
            TextView titleTextView = (TextView) row.findViewById(R.id.vidTitle);
            titleTextView.setText(currentTopImage.getString("vidTitle"));
            TextView categoryTextView = (TextView) row.findViewById(R.id.vidCategory);
            categoryTextView.setText(currentTopImage.getString("vidCategory"));
            //set like button status on create
            final ImageView likeImageView = (ImageView) row.findViewById(R.id.vidLikeImageView);
            final TextView likeNumberTextView = (TextView) row.findViewById(R.id.vidLikeNumber);
            likeNumberTextView.setText(String.valueOf(currentTopImage.getInt("likeNumber")) + getString(R.string.space) + getString(R.string.likes));
            final ParseUser mCurrentUser = ParseUser.getCurrentUser();
            ArrayList<ParseUser> mFirstWhoLikedList = (ArrayList) currentTopImage.get("likeVidPeople");
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
                    ArrayList<ParseUser> mWhoLikedList = (ArrayList) currentTopImage.get("likeVidPeople");
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
                        currentTopImage.put("likeVidPeople", mWhoLikedList);
                    } else {
                        likeImageView.setImageDrawable(getResources().getDrawable(R.drawable.like_icon));
                        currentTopImage.put("likeNumber", (currentTopImage.getInt("likeNumber") + 1));
                        mWhoLikedList.add(ParseUser.getCurrentUser());
                        currentTopImage.put("likeVidPeople", mWhoLikedList);
                    }
                    currentTopImage.saveInBackground();
                    likeNumberTextView.setText(String.valueOf(currentTopImage.getInt("likeNumber")) + getString(R.string.space) + getString(R.string.likes));
                }
            });

            final String vidURL = "http://img.youtube.com/vi/"+currentTopImage.getString("videoURL")+"/sddefault.jpg";
            final ImageView thumbnail = (ImageView)row.findViewById(R.id.videoThumbnail);
            final Bitmap[] bmp = new Bitmap[1];

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        InputStream in = new URL(vidURL).openStream();
                        bmp[0] = BitmapFactory.decodeStream(in);
                    } catch (Exception e) {
                        // log error
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    if (bmp[0] != null)
                        thumbnail.setImageBitmap(bmp[0]);
                }

            }.execute();

            thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, VideoPlayerFragment.newInstance(currentTopImage.getString("videoURL"))).commit();
                }
            });
            return row;
        }
    }

    private class wantAdapter extends ArrayAdapter<ParseObject> {
        //creating variables
        private int mResource;
        private ArrayList<ParseObject> mPosts;

        public wantAdapter(Context context, int resource, ArrayList<ParseObject> posts) {
            super(context, resource, posts);
            mResource = resource;
            mPosts = posts;
        }

        //display subject data in every row of listView
        @Override
        public View getView(final int position, View row, ViewGroup parent) {
            if (row == null) {
                row = LayoutInflater.from(getContext()).inflate(mResource, parent, false);
            }

            final ParseObject currentTopImage = mPosts.get(position);
            TextView titleTextView = (TextView) row.findViewById(R.id.userWant);
            titleTextView.setText(currentTopImage.getString("postTitle"));
            TextView subtitleTextView = (TextView) row.findViewById(R.id.postedBy2);
            subtitleTextView.setText(currentTopImage.getString("createdBy"));

            //set like button status on create
            final ImageView likeImageView = (ImageView) row.findViewById(R.id.likeImageView2);
            final TextView likeNumberTextView = (TextView) row.findViewById(R.id.likeNumber2);
            likeNumberTextView.setText(String.valueOf(currentTopImage.getInt("likeNumber")) + getString(R.string.space) + getString(R.string.likes));
            final ParseUser mCurrentUser = ParseUser.getCurrentUser();
            ArrayList<ParseUser> mFirstWhoLikedList = (ArrayList) currentTopImage.get("likePeopleArray");
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
                    ArrayList<ParseUser> mWhoLikedList = (ArrayList) currentTopImage.get("likePeopleArray");
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
                        currentTopImage.put("likePeopleArray", mWhoLikedList);
                    } else {
                        likeImageView.setImageDrawable(getResources().getDrawable(R.drawable.like_icon));
                        currentTopImage.put("likeNumber", (currentTopImage.getInt("likeNumber") + 1));
                        mWhoLikedList.add(ParseUser.getCurrentUser());
                        currentTopImage.put("likePeopleArray", mWhoLikedList);
                    }
                    currentTopImage.saveInBackground();
                    likeNumberTextView.setText(String.valueOf(currentTopImage.getInt("likeNumber")) + getString(R.string.space) + getString(R.string.likes));
                }
            });

            return row;
        }
    }

}
