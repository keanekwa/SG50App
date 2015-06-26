package com.example.user.sg50app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class IndividualPhotoFragment extends Fragment {

    public static String originator;
    public static ParseObject currentItem;

    private ParseImageView mImageView;
    private ProgressBar loading;

    private ImageButton backButton;

    public static IndividualPhotoFragment newInstance(ParseObject currentItems, String origin) {
        originator = origin;
        currentItem = currentItems;
        return new IndividualPhotoFragment();
    }

    public IndividualPhotoFragment() {
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
        View view = inflater.inflate(R.layout.fragment_individual_photo, container, false);
        mImageView = (ParseImageView) view.findViewById(R.id.individualPhoto);
        loading = (ProgressBar) view.findViewById(R.id.progressBar4);
        backButton = (ImageButton)view.findViewById(R.id.imageButton);
        loading.setVisibility(View.VISIBLE);
        mImageView.setParseFile(currentItem.getParseFile("actualImage"));
        mImageView.setPlaceholder(getResources().getDrawable(R.drawable.image_placeholder));
        mImageView.loadInBackground();
        loading.setVisibility(View.INVISIBLE);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (originator) {
                    case "PF":
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.container, PhotosFragment.newInstance()).commit();
                    case "UF":
                        FragmentManager fragmentManager2 = getActivity().getSupportFragmentManager();
                        fragmentManager2.beginTransaction().replace(R.id.container, UserContentFragment.newInstance()).commit();
                }
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


}