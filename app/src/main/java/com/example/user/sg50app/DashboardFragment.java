package com.example.user.sg50app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.CountCallback;
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
import java.util.Timer;
import java.util.TimerTask;


public class DashboardFragment extends Fragment {

    public DashboardFragment() {
        // Required empty public constructor
    }
    private ArrayList<ParseObject> placeholder = new ArrayList<>();

    public int currentimageindex=0;
    Integer totalPostings;
    Integer totalPosts;
    ParseImageView slidingimage;
    TextView noOfPhotos;
    String photoNo;
    TextView noOfPosts;
    String postNo;
    Button postPhoto;
    Button postWish;
    View mTextEntryView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        Activity activity = getActivity();
        if(activity != null) {
            slidingimage = (ParseImageView) view.findViewById(R.id.imageOfTheDay);
            final ParseQuery<ParseObject> query = ParseQuery.getQuery("allPostings");
            query.addDescendingOrder("likeNumber");
            query.countInBackground(new CountCallback() {
                public void done(int count, ParseException e) {
                    if (e == null) {
                        // The count request succeeded. Log the count
                        totalPostings = count;
                        noOfPhotos = (TextView) view.findViewById(R.id.photoNo);
                        photoNo = totalPostings.toString() + " Photos";
                        noOfPhotos.setText(photoNo);
                        query.cancel();
                        query.setLimit(5);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> parseObjects, ParseException e) {
                                if (e == null) {
                                    for (int j = 0; j < parseObjects.size(); j++) {
                                        placeholder.add(parseObjects.get(j));
                                    }
                                    final Handler mHandler = new Handler();

                                    // Create runnable for posting
                                    final Runnable mUpdateResults = new Runnable() {
                                        public void run() {

                                            AnimateandSlideShow();

                                        }
                                    };

                                    int delay = 1000; // delay for 1 sec.

                                    int period = 8000; // repeat every 4 sec.

                                    Timer timer = new Timer();

                                    timer.scheduleAtFixedRate(new TimerTask() {

                                        public void run() {

                                            mHandler.post(mUpdateResults);

                                        }

                                    }, delay, period);
                                }
                            }
                        });

                    }
                }
            });

            final ParseQuery<ParseObject> query2 = ParseQuery.getQuery("onNationalDay");
            query2.countInBackground(new CountCallback() {
                public void done(int count, ParseException e) {
                    if (e == null) {
                        // The count request succeeded. Log the count
                        totalPosts = count;
                        noOfPosts = (TextView) view.findViewById(R.id.postNo);
                        postNo = totalPosts.toString() + " Wishes";
                        noOfPosts.setText(postNo);
                        query2.cancel();
                    }
                }
            });

            postPhoto = (Button) view.findViewById(R.id.photoButton);
            postPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), PostNewActivity.class);
                    startActivity(intent);
                }
            });
            postWish = (Button) view.findViewById(R.id.wishButton);
            postWish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog();
                }
            });
        }
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

    private void AnimateandSlideShow() {
        Activity activity = getActivity();
        if(activity != null) {
            ParseFile fileObject = placeholder.get(currentimageindex).getParseFile("actualImage");
            slidingimage.setPlaceholder(getResources().getDrawable(R.drawable.image_placeholder));
            slidingimage.setParseFile(fileObject);
            slidingimage.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                }
            });

            Animation rotateimage = AnimationUtils.loadAnimation(getActivity(), R.anim.abc_fade_in);

            slidingimage.startAnimation(rotateimage);

            currentimageindex++;
            if (currentimageindex == 4) {
                currentimageindex = 0;
            }
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
        postObject.put("postTitle", post);
        postObject.put("likeNumber", 0);
        postObject.put("createdBy", ParseUser.getCurrentUser().getUsername());
        postObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(getActivity(), "Posted!", Toast.LENGTH_LONG).show();
            }
        });
    }

}
