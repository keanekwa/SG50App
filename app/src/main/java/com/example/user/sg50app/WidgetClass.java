package com.example.user.sg50app;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ri on 26/6/2015.
 */
public class WidgetClass extends AppWidgetProvider {

    public int currentimageindex=0;
    ArrayList<ParseObject>placeholder = new ArrayList<>();

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int appWidgetId : appWidgetIds) {
            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_design);
            views.setOnClickPendingIntent(R.id.widgetImage, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);

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

                                parseObjects.get(currentimageindex).getParseFile("actualImage").getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, ParseException e) {
                                        if (e == null) {
                                            Bitmap bmp = BitmapFactory
                                                    .decodeByteArray(
                                                            data, 0,
                                                            data.length);
                                            views.setImageViewBitmap(R.id.widgetImage, bmp);
                                            views.setTextViewText(R.id.widgetTitle, parseObjects.get(currentimageindex).getString("imgTitle"));
                                            views.setTextViewText(R.id.widgetPoster,parseObjects.get(currentimageindex).getString("createdBy"));
                                            currentimageindex++;
                                            if (currentimageindex == 4) {
                                                currentimageindex = 0;
                                            }
                                        }

                                        else{
                                            views.setImageViewResource(R.id.widgetImage, R.drawable.image_placeholder);
                                        }
                                    }
                                });

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

}
