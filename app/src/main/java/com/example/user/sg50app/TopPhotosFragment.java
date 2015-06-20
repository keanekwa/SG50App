package com.example.user.sg50app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

public class TopPhotosFragment extends Fragment {

    private ListView lvToShow;
    private FutureHopesFragment.OnFragmentSelectedListener mCallback;
    private static ArrayList<ParseObject> mOldList = new ArrayList<>();

    public TopPhotosFragment() {
        // Required empty public constructor
    }

    public static TopPhotosFragment newInstance() {
        return new TopPhotosFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_photos, container, false);
        lvToShow =  (ListView)view.findViewById(R.id.imgListView);
        mCallback.timeToRefresh();
        /* 'see more' button
        View buttonView = inflater.inflate(R.layout.footer_view, container, false);
        lvToShow.addFooterView(buttonView);*/
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        mCallback.timeToRefresh();
    }

    public void refresh(){
        //only refreshes if there has been a change
        if (!mOldList.equals(PhotosFragment.mTop)) {
            mOldList = new ArrayList<>(PhotosFragment.mTop);
            ArrayAdapter<ParseObject> adapter;
            adapter = new TopImgAdapter(getActivity(), R.layout.photos_list, PhotosFragment.mTop);
            lvToShow.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            lvToShow.setAdapter(adapter);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (FutureHopesFragment.OnFragmentSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class TopImgAdapter extends ArrayAdapter<ParseObject> {
        //creating variables
        private int mResource;
        private ArrayList<ParseObject> mTopPics;

        public TopImgAdapter(Context context, int resource, ArrayList<ParseObject> topPics) {
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
}
