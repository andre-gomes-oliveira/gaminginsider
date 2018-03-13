package br.com.andregomesoliveira.gaminginsider.ui;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.andregomesoliveira.gaminginsider.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleActivity extends AppCompatActivity {

    @BindView(R.id.article_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.article_toolbar_layout)
    CollapsingToolbarLayout mAppBarLayout;

    @BindView(R.id.tv_article_author)
    TextView mAuthorView;

    @BindView(R.id.iv_article_image)
    ImageView mImageView;

    @BindView(R.id.adView)
    AdView mAdView;

    private String mArticleLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Intent starterIntent = getIntent();

            mAuthorView.setText(starterIntent.getStringExtra(getString(R.string.intent_article_author)));
            mAppBarLayout.setTitle(starterIntent.getStringExtra(getString(R.string.intent_article_title)));
            mArticleLink = starterIntent.getStringExtra(getString(R.string.intent_article_link));

            String imageUrl = starterIntent.getStringExtra(getString(R.string.intent_article_image));
            if (imageUrl != null &&
                    !imageUrl.isEmpty()) {
                Picasso.with(getBaseContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholder)
                        .fit()
                        .centerCrop()
                        .into(mImageView);
            }

            Bundle arguments = new Bundle();
            arguments.putString(getString(R.string.intent_article_content),
                    starterIntent.getStringExtra(getString(R.string.intent_article_content)));
            arguments.putString(getString(R.string.intent_article_description),
                    starterIntent.getStringExtra(getString(R.string.intent_article_description)));

            ArticleFragment fragment = new ArticleFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.article_container, fragment)
                    .commit();
        }

        // Load an ad into the AdMob banner view.
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(this)
                        .setType(getString(R.string.share_mime_type))
                        .setText(mAppBarLayout.getTitle() + "\n" + mArticleLink)
                        .getIntent(), getString(R.string.action_share)));
                break;
            case R.id.action_link:
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mArticleLink));

                startActivity(webIntent);
                break;
        }

        return true;
    }
}
