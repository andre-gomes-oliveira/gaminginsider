package br.com.andregomesoliveira.gaminginsider.ui;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.prof.rssparser.Article;
import com.prof.rssparser.Parser;

import java.util.ArrayList;
import java.util.Map;

import br.com.andregomesoliveira.gaminginsider.R;
import br.com.andregomesoliveira.gaminginsider.adapter.ArticleAdapter;
import br.com.andregomesoliveira.gaminginsider.model.Category;
import br.com.andregomesoliveira.gaminginsider.provider.FeedsIntentService;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.google.android.gms.internal.zzahn.runOnUiThread;

public class FeedsFragment extends Fragment {

    //The Category of feeds this fragment is presenting.
    private Category mCategory;

    //The adapter used by the recycler view
    private ArticleAdapter mAdapter;

    // Layout manager used by the RecyclerView
    private GridLayoutManager mLayoutManager;

    //The recycler view used to display the feeds
    @BindView(R.id.feeds_list)
    RecyclerView mRecyclerView;

    //The swipe layout
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    //The progress bar used to signal that the feeds are being loaded
    @BindView(R.id.pb_feeds_loading_indicator)
    ProgressBar mProgressBar;

    public FeedsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();

        if (savedInstanceState != null) {
            mCategory = savedInstanceState.getParcelable(getString(R.string.intent_category));
        } else {
            if (arguments != null) {
                if (getArguments().containsKey(getString(R.string.intent_category))) {
                    mCategory = arguments.getParcelable(getString(R.string.intent_category));
                }
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.bind(this, rootView);

        Context context = getContext();
        if (context != null) {
            Resources resources = context.getResources();
            if (resources != null) {
                mLayoutManager = new GridLayoutManager(context,
                        resources.getInteger(R.integer.list_column_count),
                        LinearLayoutManager.VERTICAL, false);
            }
        }

        if (savedInstanceState != null) {

            Parcelable recyclerLayoutState = savedInstanceState.getParcelable
                    (getString(R.string.bundle_recycler_position));

            if (recyclerLayoutState != null && mLayoutManager != null) {
                mLayoutManager.onRestoreInstanceState(recyclerLayoutState);
            }
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                mAdapter.clearData();
                mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(true);
                loadFeed();
            }
        });

        loadFeed();
        return rootView;
    }

    @Override
    public void onResume() {

        super.onResume();
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        if (mAdapter != null)
            mAdapter.clearData();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.intent_category), mCategory);

        outState.putParcelable(getString(R.string.bundle_recycler_position),
                mLayoutManager.onSaveInstanceState());
    }

    private void loadFeed() {
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

                            ArrayList<String> titles, links;
                            titles = new ArrayList<>();
                            links = new ArrayList<>();

                            for (Article article : articles){
                                titles.add(article.getTitle());
                                links.add(article.getLink());
                            }

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
    }
}

