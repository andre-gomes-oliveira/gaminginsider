package br.com.andregomesoliveira.gaminginsider.provider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.prof.rssparser.Article;

import java.util.ArrayList;

import br.com.andregomesoliveira.gaminginsider.R;
import br.com.andregomesoliveira.gaminginsider.ui.MainActivity;
import br.com.andregomesoliveira.gaminginsider.utils.ParserUtilities;

public class FeedsWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, ArrayList<String> articles, ArrayList<String> links) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.feeds_widget);

        //The list of feeds
        Intent serviceIntent = new Intent(context, FeedsRemoteViewsService.class);
        serviceIntent.putStringArrayListExtra(
                context.getString(R.string.intent_widget_feeds), articles);
        serviceIntent.putStringArrayListExtra(
                context.getString(R.string.intent_widget_links), links);
        views.setRemoteAdapter(R.id.appwidget_feeds_list, serviceIntent);

        //The pending intent that launches the app if no feed is being displayed
        Intent activityIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                activityIntent, 0);
        views.setOnClickPendingIntent(R.id.empty_view, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        FeedsIntentService.startActionUpdateFeedsWidgets(context, null, null);
    }

    public static void updateFeedsWidgets(Context context, AppWidgetManager appWidgetManager,
                                           int[] appWidgetIds, ArrayList<String> articles,
                                          ArrayList<String> links) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, articles, links);
        }
    }
}
