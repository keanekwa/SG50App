package com.example.user.sg50app;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VideosFragment extends Fragment {

    public static ArrayList<ParseObject> mVideos;
    private ProgressBar loading;
    private ListView mListView;
    private String toSortBy;

    public static VideosFragment newInstance() {
        return new VideosFragment();
    }

    public VideosFragment() {
        // Required empty public constructor
    }

    View mTextEntryView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mVideos == null){
            mVideos = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_videos, container, false);
        mListView = (ListView) view.findViewById(R.id.vidListView);
        loading = (ProgressBar)view.findViewById(R.id.vidsLoadingPb);
        ImageButton fabImageButton = (ImageButton) view.findViewById(R.id.imageButton3);
        ImageButton sortButton = (ImageButton) view.findViewById(R.id.sortVideosImageButton);

        refreshVideos();

        fabImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog();
            }
        });

        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListSelectorDialog dlg = new ListSelectorDialog(getActivity(), "Sort By");
                String[] list = new String[]{"Alphabetical", "Recent", "Popularity"};
                String[] list2 = new String[]{"title", "recent", "likes"};
                dlg.show(list, list2, new ListSelectorDialog.listSelectorInterface() {
                    public void selectorCanceled() {
                        //Bloop
                    }
                    public void selectedItem(String key, String item) {
                        switch (key){
                            case "title":
                                toSortBy = "vidTitle";
                                break;
                            case "recent":
                                toSortBy = "createdAt";
                                break;
                            case "likes":
                                toSortBy = "likeNumber";
                                break;
                        }
                        refreshVideos();
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

    public void refreshVideos(){
        loading.setVisibility(View.VISIBLE);
        if(toSortBy==null) toSortBy="createdAt";
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("videoList");
        query.addDescendingOrder(toSortBy);
        query.setLimit(10);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> videosList, ParseException e) {
                if (e == null) {
                    mVideos.clear();
                    for (int j = 0; j < videosList.size(); j++) {
                        mVideos.add(videosList.get(j));
                    }
                    setVideosList();
                }
            }
        });
    }

    public void setVideosList(){
        ArrayAdapter<ParseObject> adapter;
        adapter = new VideosAdapter(getActivity(), R.layout.videos_list, mVideos);
        mListView.setAdapter(adapter);
        loading.setVisibility(View.GONE);
    }

    private class VideosAdapter extends ArrayAdapter<ParseObject> {
        //creating variables
        private int mResource;
        private ArrayList<ParseObject> mTopPics;

        public VideosAdapter(Context context, int resource, ArrayList<ParseObject> topPics) {
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

    public void Dialog() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        mTextEntryView = factory.inflate(R.layout.suggest_new_videos, null);
        Button pbutton = (Button)mTextEntryView.findViewById(R.id.suggestButton);
        Button nbutton = (Button)mTextEntryView.findViewById(R.id.backButton2);

        final Dialog alert = new Dialog(getActivity());
        alert.setTitle("New Suggestion");
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
        EditText mPostField = (EditText) mTextEntryView.findViewById(R.id.suggestEditText);
        String post = mPostField.getText().toString();
        ParseObject postObject = new ParseObject("suggestionsFromUsers");
        postObject.put("suggestion", post);
        postObject.put("submittedBy", ParseUser.getCurrentUser().getUsername());
        postObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(getActivity(), "Suggested!", Toast.LENGTH_LONG).show();

            }
        });
    }
}
