package com.samsung.looksdk.sample;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

/**
 * Created by a.sharma2 on 3/30/17.
 */

class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private WeatherDataPointNew mWeatherDataPointNew;
    private static final String TAG = StackRemoteViewsFactory.class.getSimpleName();
    static final ArrayList<WeatherDataPointNew> sDataNew = new ArrayList<>();

    StackRemoteViewsFactory(Context context) {
        mContext = context;
    }

    public void onCreate() {
        Log.v(TAG,"onCreate Called...");

        sDataNew.clear();
        sDataNew.add(new WeatherDataPointNew("Monday", 1));
        sDataNew.add(new WeatherDataPointNew("Tuesday", 2));
        sDataNew.add(new WeatherDataPointNew("Wednesday", 3));
        sDataNew.add(new WeatherDataPointNew("Thursday", 4));
        sDataNew.add(new WeatherDataPointNew("Friday", 5));
        sDataNew.add(new WeatherDataPointNew("Saturday", 6));
        sDataNew.add(new WeatherDataPointNew("Sunday", 7));

    }
    public void onDestroy() {

    }

    public int getCount() {
        return (sDataNew.size());
    }

    public RemoteViews getViewAt(int position) {

        mWeatherDataPointNew=sDataNew.get(position);

        final String formatStr = mContext.getResources().getString(R.string.item_format_string);
        final int itemId = R.layout.widget_item;
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), itemId);
        rv.setTextViewText(R.id.widget_item, String.format(formatStr, mWeatherDataPointNew.degreesN, mWeatherDataPointNew.dayN));

        final Intent fillInIntent = new Intent();
        rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent);

        return rv;
    }

    public RemoteViews getLoadingView() {
        return null;
    }

    public int getViewTypeCount() {
        return (sDataNew.size());
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
        Log.d(TAG, "onDataSetChanged Called.");

    }
}
