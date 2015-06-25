package com.example.user.sg50app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {
    public static ArrayList<ParseObject> mRESULTS;
    View mTextEntryView;
    GridView mPhotoGridView;
    EditText searchText;
    private ProgressBar loading;
    ImageButton confirmButton;
    ImageButton backButton;
    String searchQuery;
    static String originator;



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
        mPhotoGridView = (GridView) view.findViewById(R.id.searchGridView);
        loading = (ProgressBar) view.findViewById(R.id.progressBar3);
        loading.setVisibility(View.INVISIBLE);
        searchText = (EditText)view.findViewById(R.id.searchText);
        confirmButton = (ImageButton)view.findViewById(R.id.confirm_search);
        backButton = (ImageButton)view.findViewById(R.id.backArrow);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRESULTS.clear();
                searchQuery = searchText.getText().toString().toLowerCase();
                searchText.setText("");
                switch (originator){
                    case "PF":
                    searchPhotos();
                        break;


                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, PhotosFragment.newInstance()).commit();
            }
        });




        return view;
    }

    public void searchPhotos(){
        loading.setVisibility(View.VISIBLE);
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("allPostings");
        query.addDescendingOrder("createdAt");
        query.whereContains("imgTitle", searchQuery);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    for (int j = 0; j < parseObjects.size(); j++) {
                        mRESULTS.add(parseObjects.get(j));
                    }
                    setContentList();
                }

            }
        });
    }
    public void setContentList() {
        loading.setVisibility(View.VISIBLE);
                PhotoListAdapter adapter = new PhotoListAdapter(getActivity(), R.layout.photos_list, mRESULTS);
                mPhotoGridView.setVisibility(View.VISIBLE);
                mPhotoGridView.setAdapter(adapter);
        loading.setVisibility(View.GONE);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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

            final ParseObject currentItem = mTopPics.get(position);
            ParseFile fileObject = currentItem.getParseFile("actualImage");
            ParseImageView currentImage = (ParseImageView) row.findViewById(R.id.gridImg);
            currentImage.setPlaceholder(getResources().getDrawable(R.drawable.image_placeholder));
            currentImage.setParseFile(fileObject);
            currentImage.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                }
            });
            currentImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    photoDialog(currentItem);
                }
            });
            return row;
        }
    }

    public void photoDialog(final ParseObject currentItem) {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        mTextEntryView = factory.inflate(R.layout.individual_photo, null);
        ParseImageView imageView = (ParseImageView)mTextEntryView.findViewById(R.id.indImgView);
        imageView.setPlaceholder(getResources().getDrawable(R.drawable.image_placeholder));
        imageView.setParseFile(currentItem.getParseFile("actualImage"));
        imageView.loadInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
            }
        });

        TextView titleText = (TextView)mTextEntryView.findViewById(R.id.imgTitleI);
        titleText.setText(currentItem.getString("imgTitle"));
        TextView subtitleText = (TextView)mTextEntryView.findViewById(R.id.photoByI);
        subtitleText.setText(currentItem.getString("createdBy"));
        TextView categoryText = (TextView)mTextEntryView.findViewById(R.id.imageCategoryI);
        categoryText.setText(currentItem.getString("category"));

        Button nbutton = (Button)mTextEntryView.findViewById(R.id.backButton2);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        final Dialog alert = new Dialog(getActivity());
        WindowManager.LayoutParams layoutParams = alert.getWindow().getAttributes();
        layoutParams.height=size.y;
        layoutParams.width=size.x;
        layoutParams.gravity= Gravity.TOP;
        alert.getWindow().setAttributes(layoutParams);
        alert.setContentView(mTextEntryView);

        final ImageView likeImageView = (ImageView) mTextEntryView.findViewById(R.id.likeImageViewI);
        final TextView likeNumberTextView = (TextView) mTextEntryView.findViewById(R.id.likeNumberI);
        likeNumberTextView.setText(String.valueOf(currentItem.getInt("likeNumber")) + getString(R.string.space) + getString(R.string.likes));
        final ParseUser mCurrentUser = ParseUser.getCurrentUser();
        ArrayList<ParseUser> mFirstWhoLikedList = (ArrayList) currentItem.get("likeImgPeople");
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
                ArrayList<ParseUser> mWhoLikedList = (ArrayList) currentItem.get("likeImgPeople");
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
                    currentItem.put("likeNumber", (currentItem.getInt("likeNumber") - 1));
                    mWhoLikedList.remove(ParseUser.getCurrentUser());
                    currentItem.put("likeImgPeople", mWhoLikedList);
                } else {
                    likeImageView.setImageDrawable(getResources().getDrawable(R.drawable.like_icon));
                    currentItem.put("likeNumber", (currentItem.getInt("likeNumber") + 1));
                    mWhoLikedList.add(ParseUser.getCurrentUser());
                    currentItem.put("likeImgPeople", mWhoLikedList);
                }
                currentItem.saveInBackground();
                likeNumberTextView.setText(String.valueOf(currentItem.getInt("likeNumber")) + getString(R.string.space) + getString(R.string.likes));
            }
        });

        nbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        alert.show();
    }

}
