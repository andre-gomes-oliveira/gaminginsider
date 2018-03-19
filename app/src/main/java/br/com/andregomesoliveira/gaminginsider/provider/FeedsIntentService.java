package br.com.andregomesoliveira.gaminginsider.provider;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import br.com.andregomesoliveira.gaminginsider.R;

public class FeedsIntentService extends IntentService {

    private static final String ACTION_UPDATE_FEEDS =
            "br.com.andregomesoliveira.gaminginsider.action.update_feeds";

    public FeedsIntentService() {
        super(FeedsIntentService.class.getSimpleName());
    }

    public FeedsIntentService(String name) {
        super(name);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Context context = getApplicationContext();
        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_UPDATE_FEEDS.equals(action)) {
                ArrayList<String> articles = intent.getStringArrayListExtra(context.getString(R.string.intent_widget_feeds));
                ArrayList<String> links = intent.getStringArrayListExtra(context.getString(R.string.intent_widget_links));
                handleActionUpdateFeeds(articles, links);
            }
        }
    }

    /**
     * Starts this service to update the widget with the most recent feeds.
     */
    public static void startActionUpdateFeedsWidgets(Context context, ArrayList<String> articles,
                                                     ArrayList<String> links) {
        Intent intent = new Intent(context, FeedsIntentService.class);

        intent.putStringArrayListExtra(context.getString(R.string.intent_widget_feeds), articles);
        intent.putStringArrayListExtra(context.getString(R.string.intent_widget_links), links);
        intent.setAction(ACTION_UPDATE_FEEDS);
        context.startService(intent);
    }

    private void handleActionUpdateFeeds(ArrayList<String> articles, ArrayList<String> links) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(this, FeedsWidgetProvider.class));

        FeedsWidgetProvider.updateFeedsWidgets(this, appWidgetManager, appWidgetIds,
                articles, links);
    }
}
