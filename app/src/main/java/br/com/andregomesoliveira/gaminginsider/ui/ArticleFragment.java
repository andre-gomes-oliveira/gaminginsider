package br.com.andregomesoliveira.gaminginsider.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import br.com.andregomesoliveira.gaminginsider.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleFragment extends Fragment {

    @BindView(R.id.wv_article_content)
    WebView mContentView;

    private String mContent;
    private String mDescription;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getContext();
        Bundle arguments = getArguments();

        if (savedInstanceState != null) {
            mContent = savedInstanceState.getString(getString(R.string.intent_article_content));
            mDescription = savedInstanceState.getString(getString(R.string.intent_article_description));
        } else {
            if (arguments != null) {
                if (context != null) {
                    if (arguments.containsKey(getString(R.string.intent_article_content))) {
                        mContent = arguments.getString(
                                context.getString(R.string.intent_article_content));
                    }
                    if (arguments.containsKey(getString(R.string.intent_article_description))) {
                        mDescription = arguments.getString(
                                context.getString(R.string.intent_article_description));
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_article, container, false);

        ButterKnife.bind(this, rootView);

        if (mContent != null && !mContent.isEmpty()) {
            mContentView.loadData(mContent, getString(R.string.article_mime_type), getString(R.string.article_encoding));
        } else {
            if (mDescription != null && !mDescription.isEmpty()) {
                mContentView.loadData(mDescription, getString(R.string.article_mime_type), getString(R.string.article_encoding));
            }
        }

        if(savedInstanceState != null){
            mContentView.restoreState(savedInstanceState);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getString(R.string.intent_article_content), mContent);
        outState.putString(getString(R.string.intent_article_description), mDescription);
        mContentView.saveState(outState);
    }
}
