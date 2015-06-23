package com.example.user.sg50app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class VideosFragment extends YouTubePlayerSupportFragment implements YouTubePlayer.OnInitializedListener {

    public static final String API_KEY = "AIzaSyCzqHCXslgdrjc_iQsGBQ10-pMVVOKF8Ps";
    private YouTubePlayer youtubePlayer;
    public static String VIDEO_ID;
    private ArrayList<ParseObject> mVideos;

    private ProgressBar loading;

    public static VideosFragment newInstance() {
        return new VideosFragment();
    }

    public VideosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mVideos == null){
            mVideos = new ArrayList<>();
        }

        initialize(API_KEY, this);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
        Toast.makeText(getActivity(), "Failed to Initialize!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            player.cueVideo(VIDEO_ID);
        }
        /** add listeners to YouTubePlayer instance **/
        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);
        youtubePlayer = player;
        /** Start buffering **/
    }

    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {

        @Override
        public void onBuffering(boolean arg0) {
        }

        @Override
        public void onPaused() {
        }

        @Override
        public void onPlaying() {
        }

        @Override
        public void onSeekTo(int arg0) {
        }

        @Override
        public void onStopped() {
        }

    };

    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {

        @Override
        public void onAdStarted() {
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason arg0) {
        }

        @Override
        public void onLoaded(String arg0) {
        }

        @Override
        public void onLoading() {
        }

        @Override
        public void onVideoEnded() {
        }

        @Override
        public void onVideoStarted() {
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_videos, container, false);
        ListView mListView = (ListView) view.findViewById(R.id.vidListView);
        loading = (ProgressBar)view.findViewById(R.id.photosLoadingPb);
        ImageButton fabImageButton = (ImageButton) view.findViewById(R.id.imageButton3);



        final ParseQuery<ParseObject> query = ParseQuery.getQuery("videoList");
        query.addDescendingOrder("createdAt");
        query.setLimit(10);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> videosList, ParseException e) {
                if (e == null) {
                    for (int j = 0; j < videosList.size(); j++) {
                        mVideos.add(videosList.get(j));
                    }
                    query.cancel();
                }
            }
        });


        fabImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getActivity(), PostNewActivity.class);
                startActivity(intent);
            }
        });

        VideosAdapter adapter = new VideosAdapter(getActivity(), R.layout.videos_list, mVideos);

        mListView.setAdapter(adapter);

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

            final String vidURL = currentTopImage.getString("videoURL");
            final YouTubePlayerView youTubePlayerView = (YouTubePlayerView)row.findViewById(R.id.youtube_player);
            youTubePlayerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (youtubePlayer != null) {
                        VIDEO_ID = vidURL;
                        youtubePlayer.play();
                    }
                }
            });

            return row;
        }
    }
}
