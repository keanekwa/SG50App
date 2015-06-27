package com.example.user.encapsulate;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class OnNatDayFragment extends Fragment {

    public static ArrayList<ParseObject> mPosts;
    private ListView lvToShow;
    View mTextEntryView;
    private ProgressBar loading;

    public OnNatDayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_on_nat_day, container, false);
        FloatingActionButton fabImageButton = (FloatingActionButton) view.findViewById(R.id.action_a3);
        FloatingActionButton searchButton = (FloatingActionButton) view.findViewById(R.id.action_b3);
        lvToShow = (ListView) view.findViewById(R.id.postListView);
        loading = (ProgressBar) view.findViewById(R.id.natDayLoading);

        if(mPosts==null){
            mPosts = new ArrayList<>();
            refreshOnNatDay(true);
        }
        else setListNatDay();

        fabImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, SearchFragment.newInstance("WF")).commit();
            }
        });

        return view;
    }

    public void refreshOnNatDay(final Boolean toSetList){
        loading.setVisibility(View.VISIBLE);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("onNationalDay");
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    for (int j = 0; j < parseObjects.size(); j++) {
                        mPosts.add(parseObjects.get(j));
                    }
                    if(toSetList) setListNatDay();
                }
            }
        });
    }

    public void setListNatDay(){
        if(getActivity()==null) return;
        loading.setVisibility(View.VISIBLE);
        ArrayAdapter<ParseObject> adapter;
        adapter = new wantAdapter(getActivity(), R.layout.want_list, mPosts);
        lvToShow.setAdapter(adapter);
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
            }
            else {
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
            alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
            if (post.equals("")){
                Toast.makeText(getActivity(),"Please enter text.", Toast.LENGTH_LONG).show();
            }

            else{
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

}
