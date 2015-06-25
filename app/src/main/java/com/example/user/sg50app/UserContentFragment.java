package com.example.user.sg50app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class UserContentFragment extends Fragment {
    private static ArrayList<String> LIST_OF_PAGES;
    public static ArrayList<ParseObject> mPHOTOS;
    public static ArrayList<ParseObject> mWISHES;
    View mTextEntryView;

    private ListView mWishListView;
    private GridView mPhotoGridView;
    private Button mPhotoButton;
    private Button mWishButton;

    String YOUR_PHOTOS_STRING = "Your Photos";
    String YOUR_WISHES_STRING = "Your Wishes";
    private String currentPage;

    private ProgressBar loading;

    public static UserContentFragment newInstance() {
        return new UserContentFragment();
    }

    public UserContentFragment() {
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
        View view = inflater.inflate(R.layout.fragment_user_content, container, false);
        mWishListView = (ListView) view.findViewById(R.id.wishListView);
        mPhotoGridView = (GridView) view.findViewById(R.id.imgGridView);
        mPhotoButton = (Button) view.findViewById(R.id.userPhotoButton);
        mWishButton = (Button) view.findViewById(R.id.userWishButton);
        loading = (ProgressBar) view.findViewById(R.id.progressBar2);
        loading.setVisibility(View.VISIBLE);

        if (LIST_OF_PAGES == null) {
            LIST_OF_PAGES = new ArrayList<>();
            LIST_OF_PAGES.add(YOUR_PHOTOS_STRING);
            LIST_OF_PAGES.add(YOUR_WISHES_STRING);
        }
        if (currentPage == null) {
            currentPage = YOUR_PHOTOS_STRING;
            setTabAsSelected(mPhotoButton, true);
            setTabAsSelected(mWishButton, false);
        }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentPage(YOUR_PHOTOS_STRING);
                setTabAsSelected(mPhotoButton, true);
                setTabAsSelected(mWishButton, false);
            }
        });
        mWishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentPage(YOUR_WISHES_STRING);
                setTabAsSelected(mPhotoButton, false);
                setTabAsSelected(mWishButton, true);
            }
        });

        Boolean hasLoaded = true;
        if(mPHOTOS==null) {
            mPHOTOS = new ArrayList<>();
            hasLoaded = false;
        }
        if(mWISHES==null){
            mWISHES = new ArrayList<>();
            hasLoaded = false;
        }

        if(hasLoaded) setContentList();
        else refresh(true);
        loading.setVisibility(View.GONE);

        return view;
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

    public void refresh(final Boolean toSetList){
        loading.setVisibility(View.VISIBLE);
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("allPostings");
        query.addDescendingOrder("createdAt");
        query.whereEqualTo("createdBy", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    for (int j = 0; j < parseObjects.size(); j++) {
                        mPHOTOS.add(parseObjects.get(j));
                    }
                    final ParseQuery<ParseObject> query2 = ParseQuery.getQuery("onNationalDay");
                    query2.addDescendingOrder("createdAt");
                    query2.whereEqualTo("createdBy", ParseUser.getCurrentUser().getUsername());
                    query2.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> parseObjects, ParseException e) {
                            if (e == null) {
                                for (int j = 0; j < parseObjects.size(); j++) {
                                    mWISHES.add(parseObjects.get(j));
                                }
                                if(toSetList) setContentList();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setCurrentPage(String pageToSet) {
        if (!LIST_OF_PAGES.contains(pageToSet)) {
            currentPage = YOUR_PHOTOS_STRING;
            pageToSet = YOUR_PHOTOS_STRING;
        }

        if (!pageToSet.equals(currentPage)) {
            currentPage = pageToSet;
            setContentList();
        }
    }

    public void setContentList() {
        loading.setVisibility(View.VISIBLE);
        switch (currentPage) {
            case "Your Photos":
                PhotoListAdapter adapter = new PhotoListAdapter(getActivity(), R.layout.user_photos_list, mPHOTOS);
                mWishListView.setVisibility(View.INVISIBLE);
                mPhotoGridView.setVisibility(View.VISIBLE);
                mPhotoGridView.setAdapter(adapter);
                break;
            case "Your Wishes":
                wantAdapter adapter2 = new wantAdapter(getActivity(), R.layout.want_list, mWISHES);
                mPhotoGridView.setVisibility(View.INVISIBLE);
                mWishListView.setVisibility(View.VISIBLE);
                mWishListView.setAdapter(adapter2);
                break;
        }
        loading.setVisibility(View.GONE);
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
            ParseFile fileObject = currentTopImage.getParseFile("actualImage");
            ParseImageView currentImage = (ParseImageView) row.findViewById(R.id.gridImg);
            currentImage.setPlaceholder(getResources().getDrawable(R.drawable.image_placeholder));
            currentImage.setParseFile(fileObject);
            currentImage.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
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
            } else {
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

    public void Dialog() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        mTextEntryView = factory.inflate(R.layout.post_new_want, null);
        Button pbutton = (Button)mTextEntryView.findViewById(R.id.finalizeButton);
        Button nbutton = (Button)mTextEntryView.findViewById(R.id.backButton);

        final Dialog alert = new Dialog(getActivity());
        alert.setTitle("New Post");
        alert.setContentView(mTextEntryView);

        pbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                positiveButton();
                alert.dismiss();
            }
        });
        nbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        alert.show();
    }

    public void positiveButton() {
        EditText mPostField = (EditText) mTextEntryView.findViewById(R.id.captionEditText);
        String post = mPostField.getText().toString();
        ParseObject postObject = new ParseObject("onNationalDay");
        postObject.put("postTitle",post);
        postObject.put("likeNumber",0);
        postObject.put("createdBy", ParseUser.getCurrentUser().getUsername());
        postObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(getActivity(), "Posted!", Toast.LENGTH_LONG).show();

            }
        });
    }
}
