package br.com.andregomesoliveira.gaminginsider.ui;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//testing
import com.prof.rssparser.Article;
import com.prof.rssparser.Parser;

import java.util.ArrayList;

import br.com.andregomesoliveira.gaminginsider.R;
import br.com.andregomesoliveira.gaminginsider.model.Category;

public class FeedsFragment extends Fragment {

    /**
     * The Category of feeds this fragment is presenting.
     */
    private Category mCategory;

    public FeedsFragment() {

        String urlString = "http://feeds.ign.com/ign/all";
        Parser parser = new Parser();
        parser.execute(urlString);
        parser.onFinish(new Parser.OnTaskCompleted() {

            @Override
            public void onTaskCompleted(ArrayList<Article> articles){

                String title = articles.get(0).getTitle();
                String description = articles.get(0).getDescription();
                String content = articles.get(0).getContent();
                //what to do when the parsing is done
                //the Array List contains all article's data. For example you can use it for your adapter.
            }

            @Override
            public void onError() {
                //what to do in case of error
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }
}
