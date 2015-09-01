package com.example.user.encapsulate;

import android.os.Bundle;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

public class VideoPlayerFragment extends YouTubePlayerSupportFragment {

    private String currentVideoID = "video_id";
    public static final String API_KEY = "AIzaSyCzqHCXslgdrjc_iQsGBQ10-pMVVOKF8Ps";
    private YouTubePlayer activePlayer;

    public static VideoPlayerFragment newInstance(String url) {
        VideoPlayerFragment videoPlayerFragment = new VideoPlayerFragment();

        Bundle bundle = new Bundle();
        bundle.putString("url", url);

        videoPlayerFragment.setArguments(bundle);
        videoPlayerFragment.init();
        return videoPlayerFragment;
    }

    private void init() {

        initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
            }

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                activePlayer = player;
                activePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                if (!wasRestored) {
                    activePlayer.cueVideo(getArguments().getString("url"), 0);
                }
            }


        });
    }

    @Override
    public void onPause(){
        super.onPause();
        if (activePlayer != null){
        activePlayer.pause();
        }
    }

    @Override
    public void onDestroy() {
        if (activePlayer != null) {
            activePlayer.release();
        }
        super.onDestroy();
    }
}