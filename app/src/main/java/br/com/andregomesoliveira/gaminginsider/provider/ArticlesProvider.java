package br.com.andregomesoliveira.gaminginsider.provider;

import android.content.Context;
import android.content.Intent;
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
        mArticles.clear();
    }

    @Override
    public int getCount() {
        return mArticles.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        String link = mLinks.get(i);
        String title = mArticles.get(i);

        RemoteViews rv = new RemoteViews(mContext.getPackageName(),
                R.layout.feeds_widget_list_item);
        rv.setTextViewText(R.id.appwidget_feed_title, title);

        //TODO: Include the links to open the articles
        rv.setEmptyView(R.id.appwidget_ingredient_list, R.id.empty_view);
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
