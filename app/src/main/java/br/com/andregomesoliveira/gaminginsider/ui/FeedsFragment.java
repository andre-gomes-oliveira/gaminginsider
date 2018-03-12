package br.com.andregomesoliveira.gaminginsider.ui;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prof.rssparser.Article;
import com.prof.rssparser.Parser;

import java.util.ArrayList;
import java.util.Map;

import br.com.andregomesoliveira.gaminginsider.R;
import br.com.andregomesoliveira.gaminginsider.adapter.ArticleAdapter;
import br.com.andregomesoliveira.gaminginsider.model.Category;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class FeedsFragment extends Fragment {

    //The Category of feeds this fragment is presenting.
    private Category mCategory;

    //The adapter used by the recycler view
    private ArticleAdapter mAdapter;

    //The recycler view used to display the feeds
    @BindView(R.id.feeds_list)
    RecyclerView mRecyclerView;

    //The swipe layout
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

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

        if (savedInstanceState != null) {

            Parcelable recyclerLayoutState = savedInstanceState.getParcelable
                    (getString(R.string.bundle_recycler_position));

            if (recyclerLayoutState != null) {
                if (context != null) {
                    Resources resources = context.getResources();
                    GridLayoutManager layoutManager = new GridLayoutManager(context,
                            resources.getInteger(R.integer.list_column_count));
                    layoutManager.onRestoreInstanceState(recyclerLayoutState);
                    mRecyclerView.setLayoutManager(layoutManager);
                }
            }
        }

        loadFeed();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                mAdapter.clearData();
                mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(true);
                loadFeed();
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.intent_category), mCategory);

        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        outState.putParcelable(getString(R.string.bundle_recycler_position),
                layoutManager.onSaveInstanceState());

    }

    public void loadFeed() {
        final Context context = getContext();

        if (mCategory != null) {

            for (Map.Entry<String, String> pair : mCategory.getSources().entrySet()) {
                String url = pair.getValue();
                Parser parser = new Parser();
                parser.execute(url);
                parser.onFinish(new Parser.OnTaskCompleted() {

                    @Override
                    public void onTaskCompleted(ArrayList<Article> articles) {

                        mAdapter = new ArticleAdapter(articles, R.layout.feed_list_content, context);
                        mRecyclerView.setAdapter(mAdapter);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Timber.e(context.getString(R.string.log_feed_error));
                    }
                });
            }
        }
    }
}

