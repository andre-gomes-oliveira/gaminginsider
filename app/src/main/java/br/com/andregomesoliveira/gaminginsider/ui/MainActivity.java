package br.com.andregomesoliveira.gaminginsider.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.andregomesoliveira.gaminginsider.R;
import br.com.andregomesoliveira.gaminginsider.model.Category;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Constants
    public static final int RC_SIGN_IN = 1;

    //The activity's Toolbar
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    //The drawer Layout
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;

    //The navigation view
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    //Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mUser;
    private DatabaseReference mCategoriesDatabaseReference;
    private ChildEventListener mChildEventListener;

    //The map of news categories
    private List<Category> mCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting up Timber
        Timber.plant(new Timber.DebugTree());

        //Setting up ButterKnife
        ButterKnife.bind(this);

        //Setting up the ui
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getTitle());

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        //Setting up Firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mCategoriesDatabaseReference = database.getReference(getString(R.string.firebase_categories));

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    // User is signed in
                    onSignedInInitialize();
                } else {
                    // User is signed out
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.FacebookBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        mCategories = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Bundle arguments = new Bundle();

        switch (item.getItemId()) {
            case R.id.nav_category_all:
                arguments.putParcelable(getString(R.string.intent_category), mCategories.get(0));
                break;
            case R.id.nav_category_articles:
                arguments.putParcelable(getString(R.string.intent_category), mCategories.get(1));
                break;
            case R.id.nav_category_generated:
                arguments.putParcelable(getString(R.string.intent_category), mCategories.get(2));
                break;
            case R.id.nav_category_news:
                arguments.putParcelable(getString(R.string.intent_category), mCategories.get(3));
                break;
            case R.id.nav_category_reviews:
                arguments.putParcelable(getString(R.string.intent_category), mCategories.get(4));
                break;
            case R.id.nav_add:
                mDrawer.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_settings:
                mDrawer.closeDrawer(GravityCompat.START);
                return true;
        }

        FeedsFragment fragment = new FeedsFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.feeds_container, fragment)
                .commit();
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void onSignedInInitialize() {
        attachDatabaseReadListener();

        TextView userNameView = findViewById(R.id.tv_user_name);
        TextView userEmailView = findViewById(R.id.tv_user_email);
        ImageView avatarView = findViewById(R.id.iv_user_avatar);

        userNameView.setText(mUser.getDisplayName());
        userEmailView.setText(mUser.getEmail());

        /* No need for an error image or a placeholder
           If no image can be loaded, the default from the nav_header layout will be used
        */
        Picasso.with(this)
                .load(mUser.getPhotoUrl())
                .into(avatarView);
    }

    private void onSignedOutCleanup() {
        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    mCategories.add(dataSnapshot.getValue(Category.class));
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    mCategories.remove(dataSnapshot.getValue(Category.class));
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mCategoriesDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mCategoriesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }
}
