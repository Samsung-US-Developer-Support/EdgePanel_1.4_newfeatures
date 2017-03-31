/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.samsung.looksdk.sample;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.samsung.android.sdk.look.cocktailbar.SlookCocktailManager;
import com.samsung.android.sdk.look.cocktailbar.SlookCocktailProvider;

import java.util.Random;


public class WeatherWidgetProvider extends SlookCocktailProvider {

    private static String ACTION_LONG_PRESS = "com.samsung.looksdk.sample.ACTION_LONG_PRESS";
    private static final String ACTION_PULL_TO_REFRESH = "com.samsung.looksdk.sample.ACTION_PULL_TO_REFRESH";

    private static final String TAG = WeatherWidgetProvider.class.getSimpleName();

    private static final int sMaxDegrees = 96;
    private RemoteViews remoteViews;
    static int appId;

    @Override
    public void onEnabled(Context context) {

    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        final String action = intent.getAction();
        Log.v(TAG, "onReceive method");
        Log.v(TAG, "action " + action);

        if (action.equals(ACTION_LONG_PRESS) || action.equals(ACTION_PULL_TO_REFRESH)) {

            SlookCocktailManager mSlookCocktailManager = SlookCocktailManager.getInstance(ctx);
            final ComponentName cn = new ComponentName(ctx, WeatherWidgetProvider.class);
            final int[] cocktailIds = mSlookCocktailManager.getCocktailIds(cn);

            remoteViews = new RemoteViews(ctx.getPackageName(), R.layout.widget_layout);
            if (action.equals(ACTION_LONG_PRESS))
                remoteViews.setViewVisibility(R.id.progress, View.VISIBLE);
            mSlookCocktailManager.updateCocktail(appId, remoteViews);

            try {
                // thread to sleep for 1700 milliseconds
                Thread.sleep(1700);
            } catch (Exception e) {
                Log.v(TAG, "Exception in onReceive" + e.getMessage());
            }

            final int count = StackRemoteViewsFactory.sDataNew.size();
            Log.v(TAG, "Setting the temperature to random value on pull to refresh/long press");

            for (int i = 0; i < count; ++i) {
                StackRemoteViewsFactory.sDataNew.clear();
                StackRemoteViewsFactory.sDataNew.add(new WeatherDataPointNew("Monday", new Random().nextInt(sMaxDegrees)));
                StackRemoteViewsFactory.sDataNew.add(new WeatherDataPointNew("Tuesday", new Random().nextInt(sMaxDegrees)));
                StackRemoteViewsFactory.sDataNew.add(new WeatherDataPointNew("Wednesday", new Random().nextInt(sMaxDegrees)));
                StackRemoteViewsFactory.sDataNew.add(new WeatherDataPointNew("Thursday", new Random().nextInt(sMaxDegrees)));
                StackRemoteViewsFactory.sDataNew.add(new WeatherDataPointNew("Friday", new Random().nextInt(sMaxDegrees)));
                StackRemoteViewsFactory.sDataNew.add(new WeatherDataPointNew("Saturday", new Random().nextInt(sMaxDegrees)));
                StackRemoteViewsFactory.sDataNew.add(new WeatherDataPointNew("Sunday", new Random().nextInt(sMaxDegrees)));


                WeatherDataPointNew mWeatherDataPointNew = StackRemoteViewsFactory.sDataNew.get(i);
                final String formatStr = ctx.getResources().getString(R.string.item_format_string);
                remoteViews.setTextViewText(R.id.widget_item, String.format(formatStr, mWeatherDataPointNew.degreesN, mWeatherDataPointNew.dayN));
                remoteViews.setViewVisibility(R.id.progress, View.INVISIBLE);

                for (int cocktailId : cocktailIds)
                    mSlookCocktailManager.updateCocktail(cocktailId, remoteViews);
            }
            for (int cocktailId : cocktailIds)
                mSlookCocktailManager.notifyCocktailViewDataChanged(cocktailId, R.id.widgetlist);

        }
        super.onReceive(ctx, intent);
    }

    private RemoteViews buildLayout(final Context context, final int appWidgetId) {

        Log.v(TAG, "buildLayout Called...");
        final Intent intent = new Intent(context, WeatherWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        remoteViews.setRemoteAdapter(R.id.widgetlist, intent);

        final SlookCocktailManager mgr = SlookCocktailManager.getInstance(context);
        mgr.notifyCocktailViewDataChanged(appWidgetId, remoteViews.getLayoutId());

        remoteViews.setEmptyView(R.id.widgetlist, R.id.emptylist);

        /********************Long Press pending intent on a button- start**************************************************************************************/

        Intent longPressBtnIntent = new Intent(context, WeatherWidgetProvider.class);
        longPressBtnIntent.setAction(WeatherWidgetProvider.ACTION_LONG_PRESS);
        PendingIntent pendingLongPressIntent = PendingIntent.getBroadcast(context, 0xff, longPressBtnIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        SlookCocktailManager.getInstance(context).setOnLongClickPendingIntent(remoteViews, R.id.refresh, pendingLongPressIntent);

        /********************Long Press pending intent on a button- end**************************************************************************************/

        /********************Long Press pending intent on all item of a List View- start*********************************************************************/

        Intent longPress = new Intent(context, WeatherWidgetProvider.class);
        longPress.setAction(WeatherWidgetProvider.ACTION_LONG_PRESS);

        PendingIntent pendingLongPressListIntent = PendingIntent.getBroadcast(context, 0xff, longPress, PendingIntent.FLAG_UPDATE_CURRENT);
        SlookCocktailManager.getInstance(context).setOnLongClickPendingIntentTemplate(remoteViews, R.id.widgetlist, pendingLongPressListIntent);

        /********************Long Press pending intent on all item of a List View- end***********************************************************************/

        return remoteViews;
    }

    @Override
    public void onUpdate(Context context,
                         SlookCocktailManager appWidgetManager, int[] appWidgetIds) {
        Log.v(TAG, "onUpdate Called...");
        // Update each of the widgets with the remote adapter
        for (int appWidgetId : appWidgetIds) {
            appId = appWidgetId;
            RemoteViews layout = buildLayout(context, appWidgetId);
            appWidgetManager.updateCocktail(appWidgetId, layout);
        }

        /********************Pull to refresh pending intent - start*****************************************************************************************/
        Intent refreshintent = new Intent(context, WeatherWidgetProvider.class);
        refreshintent.setAction(WeatherWidgetProvider.ACTION_PULL_TO_REFRESH);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0xff, refreshintent, PendingIntent.FLAG_UPDATE_CURRENT);
        SlookCocktailManager.getInstance(context).setOnPullPendingIntent(appId, R.id.widgetlist, pendingIntent);

        /********************Pull to refresh pending intent - end******************************************************************************************/
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}