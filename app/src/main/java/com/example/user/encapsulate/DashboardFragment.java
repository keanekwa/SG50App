package com.example.user.encapsulate;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
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
    View mTextEntryView;
    TextView noOfUserPhotos;
    TextView noOfUserPosts;

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
            final TextView countdownTitle = (TextView) view.findViewById(R.id.countdownTitle);
            final TextView dayNo = (TextView) view.findViewById(R.id.dayNo);
            final TextView hourNo = (TextView) view.findViewById(R.id.hourNo);
            final TextView minuteNo = (TextView) view.findViewById(R.id.minuteNo);
            final TextView secondNo = (TextView) view.findViewById(R.id.secondNo);
            noOfPhotos = (TextView) view.findViewById(R.id.photoNo);
            noOfPosts = (TextView) view.findViewById(R.id.postNo);
            Typeface custom_font = Typeface.createFromAsset(activity.getAssets(), "fonts/Din.ttf");
            dayNo.setTypeface(custom_font);
            hourNo.setTypeface(custom_font);
            minuteNo.setTypeface(custom_font);
            secondNo.setTypeface(custom_font);
            countdownTitle.setTypeface(custom_font);
            noOfPhotos.setTypeface(custom_font);
            noOfPosts.setTypeface(custom_font);
            noOfUserPhotos = (TextView) view.findViewById(R.id.userPhotoNo);
            noOfUserPosts = (TextView) view.findViewById(R.id.userPostNo);
            final LinearLayout countdownLinearLayout = (LinearLayout) view.findViewById(R.id.countdownLinearLayout);

            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
            final long nowTime = calendar.getTimeInMillis();
            final long ndpTime = 1439049600000L;
            long countdownTime = ndpTime-nowTime;
            new CountDownTimer(countdownTime, 1000) {
                 public void onTick(long millisUntilFinished) {
                    Calendar countdownCalendar = Calendar.getInstance();
                    countdownCalendar.setTimeInMillis(millisUntilFinished);
                    dayNo.setText(Integer.toString(countdownCalendar.get(Calendar.DAY_OF_YEAR)));
                    hourNo.setText(DateFormat.format("HH", millisUntilFinished));
                    minuteNo.setText(DateFormat.format("mm", millisUntilFinished));
                    secondNo.setText(DateFormat.format("ss", millisUntilFinished));
                }
                public void onFinish() {
                    countdownLinearLayout.setVisibility(View.INVISIBLE);
                    countdownTitle.setText("Happy 50th Birthday, Singapore!!!");
                    countdownTitle.setTextSize(28);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    params.setMargins(0, 0, 0, 0);
                    countdownTitle.setLayoutParams(params);
                    countdownTitle.setGravity(Gravity.CENTER);
                }
            }.start();

            slidingimage = (ParseImageView) view.findViewById(R.id.imageOfTheDay);
            final ParseQuery<ParseObject> query = ParseQuery.getQuery("allPostings");
            query.addDescendingOrder("likeNumber");
                query.setLimit(5);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(final List<ParseObject> parseObjects, ParseException e) {
                        if (e == null) {
                            for (int j = 0; j < parseObjects.size(); j++) {
                                placeholder.add(parseObjects.get(j));
                            }
                            final Handler mHandler = new Handler();

                            // Create runnable for posting
                            final Runnable mUpdateResults = new Runnable() {
                                public void run() {

                                    AnimateandSlideShow(parseObjects.size());

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
            TextView profileTitle = (TextView) view.findViewById(R.id.profileTitle);
            profileTitle.setText(ParseUser.getCurrentUser().getUsername() + "\'s Profile");
        }
        recount();
        return view;
    }

    public void recount(){
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("allPostings");
        query.addDescendingOrder("likeNumber");
        query.countInBackground(new CountCallback() {
            public void done(int count, ParseException e) {
                if (e == null) {
                    // The count request succeeded. Log the count
                    totalPostings = count;
                    photoNo = totalPostings.toString();
                    if(noOfPhotos!=null) noOfPhotos.setText(photoNo);
                    query.cancel();
                }
            }
        });

        final ParseQuery<ParseObject> query2 = ParseQuery.getQuery("onNationalDay");
        query2.countInBackground(new CountCallback() {
            public void done(int count, ParseException e) {
                if (e == null) {
                    // The count request succeeded. Log the count
                    totalPosts = count;
                    postNo = totalPosts.toString();
                    if(noOfPosts!=null) noOfPosts.setText(postNo);
                    query2.cancel();
                }
            }
        });

        final ParseQuery<ParseObject> query3 = ParseQuery.getQuery("allPostings");
        query3.whereEqualTo("createdBy", ParseUser.getCurrentUser().getUsername());
        query3.countInBackground(new CountCallback() {
            public void done(int count, ParseException e) {
                if (e == null) {
                    if(noOfUserPhotos!=null) noOfUserPhotos.setText(Integer.toString(count));
                    query3.cancel();
                }
            }
        });

        final ParseQuery<ParseObject> query4 = ParseQuery.getQuery("onNationalDay");
        query4.whereEqualTo("createdBy", ParseUser.getCurrentUser().getUsername());
        query4.countInBackground(new CountCallback() {
            public void done(int count, ParseException e) {
                if (e == null) {
                    if(noOfUserPosts!=null) noOfUserPosts.setText(Integer.toString(count));
                    query4.cancel();
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

    private void AnimateandSlideShow(int size) {
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
    }}


}
