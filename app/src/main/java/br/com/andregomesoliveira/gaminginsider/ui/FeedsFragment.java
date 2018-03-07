package br.com.andregomesoliveira.gaminginsider.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

public class FeedsFragment extends Fragment {

    //The Category of feeds this fragment is presenting.
    private Category mCategory;

    //The adapter used by the recycler view
    private ArticleAdapter mAdapter;

    //The recycler view used to display the feeds
    @BindView(R.id.feeds_list)
    RecyclerView mRecyclerView;

    //The swipe layout
    @BindView(R.id.container)
    SwipeRefreshLayout mRefreshLayout;

    public FeedsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();

        mRefreshLayout.canChildScrollUp();
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                mAdapter.clearData();
                mAdapter.notifyDataSetChanged();
                mRefreshLayout.setRefreshing(true);
                //loadFeed();
            }
        });

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.bind(this, rootView);

        if (mCategory != null) {

            for (Map.Entry<String, String> pair : mCategory.getSources().entrySet()) {
                String url = pair.getValue();
                Parser parser = new Parser();
                parser.execute(url);
                parser.onFinish(new Parser.OnTaskCompleted() {

                    @Override
                    public void onTaskCompleted(ArrayList<Article> articles) {

                        String title = articles.get(0).getTitle();
                        String description = articles.get(0).getDescription();
                        String content = articles.get(0).getContent();
                    }

                    @Override
                    public void onError() {
                    }
                });
            }
        }
        return rootView;
    }
}
