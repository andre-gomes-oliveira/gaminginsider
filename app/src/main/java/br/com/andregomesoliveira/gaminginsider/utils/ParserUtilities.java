package br.com.andregomesoliveira.gaminginsider.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.prof.rssparser.Article;
import com.prof.rssparser.Parser;

import java.util.ArrayList;
import java.util.Map;

import br.com.andregomesoliveira.gaminginsider.R;
import br.com.andregomesoliveira.gaminginsider.model.Category;
import br.com.andregomesoliveira.gaminginsider.provider.FeedsIntentService;
import timber.log.Timber;

import static com.google.android.gms.internal.zzahn.runOnUiThread;

public class ParserUtilities {

    private static Category mCurrentCategory;
    private static ArrayList<Article> mCurrentArticles;

    public static Category getCurrentCategory() {
        return mCurrentCategory;
    }

    public static void setCurrentCategory(Category newCategory) {
        mCurrentCategory = newCategory;
    }

    public static ArrayList<Article> getCurrentArticles() {
        return mCurrentArticles;
    }

    public static void loadFeeds(@NonNull final Context context,
                                 @Nullable final Category category,
                                 @Nullable final ProgressBar progressBar) {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        if (category != null) {
            mCurrentCategory = category;
        }

        if(mCurrentCategory != null){


            mCurrentArticles = new ArrayList<>();

            for (Map.Entry<String, String> pair : mCurrentCategory.getSources().entrySet()) {

                String url = pair.getValue();
                Parser parser = new Parser();
                parser.execute(url);
                parser.onFinish(new Parser.OnTaskCompleted() {

                    @Override
                    public void onTaskCompleted(ArrayList<Article> articles) {
                        mCurrentArticles.addAll(articles);

                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }

                        updateWidgets(context);
                    }

                    @Override
                    public void onError() {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (progressBar != null) {
                                    progressBar.setVisibility(View.GONE);
                                }

                                Timber.e(context.getString(R.string.log_feed_error));

                                Toast.makeText(context,
                                        context.getString(R.string.log_feed_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }
    }

    public static void addFeed(@NonNull final View view,
                               @NonNull final Context appContext,
                               @NonNull final String url,
                               @NonNull final DatabaseReference databaseRef) {
        if (!URLUtil.isValidUrl(url)) {
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

    private static void updateWidgets(@NonNull final Context context){
        ArrayList<String> articles, links;
        articles = new ArrayList<>();
        links = new ArrayList<>();

        for (Article article : mCurrentArticles){
            articles.add(article.getTitle());
            links.add(article.getLink());
        }

        FeedsIntentService.startActionUpdateFeedsWidgets(context, articles, links);
    }
}
