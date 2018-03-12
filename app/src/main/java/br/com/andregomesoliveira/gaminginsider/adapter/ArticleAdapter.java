package br.com.andregomesoliveira.gaminginsider.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.prof.rssparser.Article;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import br.com.andregomesoliveira.gaminginsider.R;
import br.com.andregomesoliveira.gaminginsider.ui.ArticleActivity;
import timber.log.Timber;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private ArrayList<Article> mArticles;

    private int cardLayout;
    private Context mContext;

    public ArticleAdapter(ArrayList<Article> list, int cardLayout, Context context) {

        this.mArticles = list;
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
        Article currentArticle = mArticles.get(position);

        if (currentArticle != null) {
            Locale.setDefault(Locale.getDefault());
            Date date = currentArticle.getPubDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
            if(date != null){
                final String pubDateString = dateFormat.format(date);
                viewHolder.pubDate.setText(pubDateString);
            }

            viewHolder.title.setText(currentArticle.getTitle());
            if (currentArticle.getImage() != null &&
                    !currentArticle.getImage().isEmpty()) {
                Picasso.with(mContext)
                        .load(currentArticle.getImage())
                        .placeholder(R.drawable.placeholder)
                        .fit()
                        .centerCrop()
                        .into(viewHolder.image);
            }

            viewHolder.itemView.setTag(currentArticle);
            viewHolder.itemView.setOnClickListener(mOnClickListener);
        }
    }

    @Override
    public int getItemCount() {

        return mArticles == null ? 0 : mArticles.size();

    }

    public void clearData() {
        if (mArticles != null)
            mArticles.clear();
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Article article = (Article) view.getTag();
            Context context = view.getContext();

            Class destinationClass = ArticleActivity.class;
            Intent intent = new Intent(context, destinationClass);

            intent.putExtra(context.getString(R.string.intent_article_author), article.getAuthor());
            intent.putExtra(context.getString(R.string.intent_article_content), article.getContent());
            intent.putExtra(context.getString(R.string.intent_article_description), article.getDescription());
            intent.putExtra(context.getString(R.string.intent_article_image), article.getImage());
            intent.putExtra(context.getString(R.string.intent_article_link), article.getLink());
            context.startActivity(intent);
        }
    };

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView pubDate;
        ImageView image;

        ViewHolder(View itemView) {

            super(itemView);

            title = itemView.findViewById(R.id.title);
            pubDate = itemView.findViewById(R.id.pubDate);
            image = itemView.findViewById(R.id.image);
        }
    }
}
