package br.com.andregomesoliveira.gaminginsider.ui;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import br.com.andregomesoliveira.gaminginsider.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ArticleActivity extends AppCompatActivity {

    @BindView(R.id.article_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.article_toolbar_layout)
    CollapsingToolbarLayout mAppBarLayout;

    @BindView(R.id.iv_article_image)
    ImageView mImageView;

    private InterstitialAd mInterstitialAd;
    private String mArticleLink;
    private String mArticleTitle;
    private String mArticleImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        if (savedInstanceState == null) {
            Intent starterIntent = getIntent();

            mArticleTitle = starterIntent.getStringExtra(getString(R.string.intent_article_title));
            mArticleLink = starterIntent.getStringExtra(getString(R.string.intent_article_link));
            mArticleImageUrl = starterIntent.getStringExtra(getString(R.string.intent_article_image));

            Bundle arguments = new Bundle();
            arguments.putString(getString(R.string.intent_article_content),
                    starterIntent.getStringExtra(getString(R.string.intent_article_content)));
            arguments.putString(getString(R.string.intent_article_description),
                    starterIntent.getStringExtra(getString(R.string.intent_article_description)));


            ArticleFragment fragment = new ArticleFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.nsv_article_container, fragment)
                    .commit();

            mInterstitialAd = newInterstitialAd();
            loadInterstitial();
        }
        else{
            mArticleLink = savedInstanceState.getString(getString(R.string.bundle_article_link));
            mArticleTitle = savedInstanceState.getString(getString(R.string.bundle_article_title));
            mArticleImageUrl = savedInstanceState.getString(getString(R.string.bundle_article_image));
        }

        mAppBarLayout.setTitle(mArticleTitle);

        if (mArticleImageUrl != null &&
                !mArticleImageUrl.isEmpty()) {
            Picasso.with(getBaseContext())
                    .load(mArticleImageUrl)
                    .placeholder(R.drawable.ic_sync)
                    .fit()
                    .centerCrop()
                    .into(mImageView);
        }
        else{
            mImageView.setVisibility(View.GONE);
        }
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
                if(mArticleLink != null && !mArticleLink.isEmpty()) {
                    startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(this)
                            .setType(getString(R.string.share_mime_type))
                            .setText(mAppBarLayout.getTitle() + "\n" + mArticleLink)
                            .getIntent(), getString(R.string.action_share)));
                }
                else{
                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.article_layout),
                                    getString(R.string.error_opening_article), Snackbar.LENGTH_LONG);

                    snackbar.show();

                    Timber.e(getString(R.string.error_opening_article));
                }
                break;
            case R.id.action_link:
                if(mArticleLink != null && !mArticleLink.isEmpty()){
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mArticleLink));
                    startActivity(webIntent);
                }
                else{
                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.article_layout),
                                    getString(R.string.error_opening_article), Snackbar.LENGTH_LONG);

                    snackbar.show();

                    Timber.e(getString(R.string.error_opening_article));
                }

                break;
        }

        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getString(R.string.bundle_article_link), mArticleLink);
        outState.putString(getString(R.string.bundle_article_title), mArticleTitle);
        outState.putString(getString(R.string.bundle_article_image), mArticleImageUrl);
    }

    private InterstitialAd newInterstitialAd() {
        InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                showInterstitial();
            }
        });

        return interstitialAd;
    }

    private void showInterstitial() {
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Snackbar.make(findViewById(R.id.article_layout), getString(R.string.log_feed_error),
                    Snackbar.LENGTH_LONG).show();
        }
    }

    private void loadInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        mInterstitialAd.loadAd(adRequest);
    }
}
