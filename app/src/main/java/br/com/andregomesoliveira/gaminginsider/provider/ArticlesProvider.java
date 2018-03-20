package br.com.andregomesoliveira.gaminginsider.provider;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import br.com.andregomesoliveira.gaminginsider.R;

class ArticlesProvider implements RemoteViewsService.RemoteViewsFactory {

    private ArrayList<String> mArticles = new ArrayList<>();
    private ArrayList<String> mLinks = new ArrayList<>();
    private Context mContext = null;

    ArticlesProvider(Context applicationContext, Intent intent) {
        this.mContext = applicationContext;
        this.mArticles = intent.getStringArrayListExtra(
                applicationContext.getString(R.string.intent_widget_feeds));
        this.mLinks = intent.getStringArrayListExtra(
                applicationContext.getString(R.string.intent_widget_links));
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {
        if(mArticles != null){
            mArticles.clear();
        }

        if(mLinks != null){
            mLinks.clear();
        }
    }

    @Override
    public int getCount() {
        if(mArticles != null){
            return mArticles.size();
        }

        else{
            return 0;
        }
    }

    @Override
    public RemoteViews getViewAt(int i) {
        String link = mLinks.get(i);
        String title = mArticles.get(i);

        RemoteViews rv = new RemoteViews(mContext.getPackageName(),
                R.layout.feeds_widget_list_item);
        rv.setTextViewText(R.id.appwidget_feed_title, title);
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0,
                webIntent, 0);
        rv.setOnClickPendingIntent(R.id.appwidget_feed_title, pendingIntent);
        rv.setEmptyView(R.id.appwidget_feeds_list, R.id.empty_view);
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        //Displaying ImageButtons and TextViews
        return 2;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
