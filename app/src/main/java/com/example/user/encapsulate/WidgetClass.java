package com.example.user.encapsulate;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.RemoteViews;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WidgetClass extends AppWidgetProvider {

    public int currentimageindex=0;
    ArrayList<ParseObject>placeholder = new ArrayList<>();
    ArrayList<Bitmap>btmPlaceholder = new ArrayList<>();

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (final int appWidgetId : appWidgetIds) {
            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_design);
            views.setOnClickPendingIntent(R.id.widgetImage, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget

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
                        for (int j = 0; j < parseObjects.size(); j++) {
                            if (j != 5){
                            ParseFile fileObject = placeholder.get(j).getParseFile("actualImage");
                            fileObject.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null) {

                                        Bitmap bmp = BitmapFactory
                                                .decodeByteArray(
                                                        data, 0,
                                                        data.length);
                                        scaleDownBitmap(bmp,100,context);
                                        btmPlaceholder.add(bmp);
                                    }}});}

                        else{
                                break;
                            }}
                        final Handler mHandler = new Handler();

                        // Create runnable for posting
                        final Runnable mUpdateResults = new Runnable() {
                            public void run() {
                                            views.setImageViewBitmap(R.id.widgetImage,btmPlaceholder.get(currentimageindex));
                                            appWidgetManager.updateAppWidget(appWidgetId, views);


                                currentimageindex++;
                                if (currentimageindex == 4) {
                                    currentimageindex = 0;
                                }

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

        }}

    public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int h= (int) (newHeight*densityMultiplier);
        int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));

        photo=Bitmap.createScaledBitmap(photo, w, h, true);

        return photo;
    }
}
