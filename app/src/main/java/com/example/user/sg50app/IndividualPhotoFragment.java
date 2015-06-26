package com.example.user.sg50app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
    public static Integer currentPos;
    public static String currentPage2;
    private ParseImageView mImageView;
    private ProgressBar loading;

    private ImageButton backButton;

    public static IndividualPhotoFragment newInstance(@Nullable String currentPage,Integer position, ParseObject currentItems, String origin) {
        if (currentPage != null){
        currentPage2 = currentPage;}
        currentPos = position;
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
        mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mImageView.setPlaceholder(getResources().getDrawable(R.drawable.image_placeholder));
        mImageView.loadInBackground();
        loading.setVisibility(View.INVISIBLE);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                switch (originator) {
                    default:
                    case "PF":
                        Fragment fragment1 = new PhotosFragment();
                        fragmentManager.beginTransaction().replace(R.id.container, fragment1).commit();
                        PhotosFragment.currentItem = currentPos;
                        PhotosFragment.fromIndiv = currentPage2;
                        break;
                    case "UF":
                        Fragment fragment2 = new UserContentFragment();
                        fragmentManager.beginTransaction().replace(R.id.container, fragment2).commit();
                        break;
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