package br.com.andregomesoliveira.gaminginsider.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.webkit.URLUtil;

import com.google.firebase.database.DatabaseReference;
import com.prof.rssparser.Article;
import com.prof.rssparser.Parser;

import java.util.ArrayList;

import br.com.andregomesoliveira.gaminginsider.R;

import static com.google.android.gms.internal.zzahn.runOnUiThread;

public class ParserUtilities {
/*
    public static void loadFeeds() {
        if (!mSwipeRefreshLayout.isRefreshing())
            mProgressBar.setVisibility(View.VISIBLE);

        if (mCategory != null) {
            final Context context = getContext();
            for (Map.Entry<String, String> pair : mCategory.getSources().entrySet()) {

                String url = pair.getValue();
                Parser parser = new Parser();
                parser.execute(url);
                parser.onFinish(new Parser.OnTaskCompleted() {

                    @Override
                    public void onTaskCompleted(ArrayList<Article> articles) {
                        if (mAdapter == null) {
                            mAdapter = new ArticleAdapter(articles, context);
                            mRecyclerView.setAdapter(mAdapter);
                        } else {
                            mAdapter.getArticles().addAll(articles);
                            mAdapter.notifyDataSetChanged();
                        }

                        mProgressBar.setVisibility(View.GONE);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError() {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgressBar.setVisibility(View.GONE);
                                mSwipeRefreshLayout.setRefreshing(false);

                                if (context != null) {
                                    Snackbar.make(mSwipeRefreshLayout, context.getString(R.string.log_feed_error),
                                            Snackbar.LENGTH_LONG).show();
                                    Timber.e(context.getString(R.string.log_feed_error));
                                }
                            }
                        });
                    }
                });
            }
        }
    }*/

    public static void addFeed(@NonNull final View view,
                                  @NonNull final Context appContext,
                                  @NonNull final String url,
                                  @NonNull final DatabaseReference databaseRef) {
        if(!URLUtil.isValidUrl(url)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(view, appContext.getString(R.string.add_feed_url_error),
                            Snackbar.LENGTH_LONG).show();
                }
            });
            return;
        }

        Parser parser = new Parser();
        parser.execute(url);
        parser.onFinish(new Parser.OnTaskCompleted() {

            @Override
            public void onTaskCompleted(ArrayList<Article> articles) {
                //The users wil only add feeds to the "User Generated" category
                databaseRef.child(appContext.getString(R.string.user_generated_category))
                        .child(appContext.getString(R.string.user_generated_category_child))
                        .push().setValue(url);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(view, appContext.getString(R.string.add_feed_success),
                                Snackbar.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onError() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(view, appContext.getString(R.string.add_feed_error),
                                Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
