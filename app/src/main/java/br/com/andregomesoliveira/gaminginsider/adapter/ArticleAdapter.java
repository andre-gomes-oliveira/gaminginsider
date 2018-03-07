package br.com.andregomesoliveira.gaminginsider.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.prof.rssparser.Article;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import br.com.andregomesoliveira.gaminginsider.R;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private ArrayList<Article> articles;

    private int cardLayout;
    private Context mContext;
    WebView articleView;

    public ArticleAdapter(ArrayList<Article> list, int cardLayout, Context context) {

        this.articles = list;
        this.cardLayout = cardLayout;
        this.mContext = context;
    }

    @Override
    public long getItemId(int item) {
        return item;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(cardLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        Article currentArticle = articles.get(position);

        Locale.setDefault(Locale.getDefault());
        Date date = currentArticle.getPubDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
        final String pubDateString = dateFormat.format(date);

        viewHolder.title.setText(currentArticle.getTitle());
        if(!currentArticle.getImage().isEmpty()){
            Picasso.with(mContext)
                    .load(currentArticle.getImage())
                    .placeholder(R.drawable.placeholder)
                    .fit()
                    .centerCrop()
                    .into(viewHolder.image);
        }
        else{
            viewHolder.image.setVisibility(View.INVISIBLE);
        }


        viewHolder.pubDate.setText(pubDateString);

        String categories = "";
        if (currentArticle.getCategories() != null) {
            for (int i = 0; i < currentArticle.getCategories().size(); i++) {
                if (i == currentArticle.getCategories().size() - 1) {
                    categories = categories + currentArticle.getCategories().get(i);
                } else {
                    categories = categories + currentArticle.getCategories().get(i) + ", ";
                }
            }
        }

        viewHolder.category.setText(categories);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //TODO: implement the click listener
            }
        });
    }

    @Override
    public int getItemCount() {

        return articles == null ? 0 : articles.size();

    }

    public void clearData() {
        if (articles != null)
            articles.clear();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView pubDate;
        ImageView image;
        TextView category;

        ViewHolder(View itemView) {

            super(itemView);

            title = itemView.findViewById(R.id.title);
            pubDate = itemView.findViewById(R.id.pubDate);
            image = itemView.findViewById(R.id.image);
            category = itemView.findViewById(R.id.categories);
        }
    }
}
