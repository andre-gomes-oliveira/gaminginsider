package br.com.andregomesoliveira.gaminginsider.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

import br.com.andregomesoliveira.gaminginsider.R;
import br.com.andregomesoliveira.gaminginsider.model.Category;
import br.com.andregomesoliveira.gaminginsider.utils.FeedsFetchingUtilities;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static br.com.andregomesoliveira.gaminginsider.utils.ParserUtilities.addFeed;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Constants
    private static final int RC_SIGN_IN = 1;

    //The activity's Toolbar
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    //The drawer Layout
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;

    //The navigation view
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    //The welcome message TextView
    @BindView(R.id.tv_welcome_message)
    TextView mWelcomeTextView;

    //Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mUser;
    private DatabaseReference mCategoriesDatabaseReference;
    private ChildEventListener mChildEventListener;

    //The map of news categories
    private ArrayList<Category> mCategories;

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

        if (!isNetworkAvailable()) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.alert_message)
                    .setTitle(R.string.alert_title)
                    .setCancelable(false)
                    .setPositiveButton(R.string.alert_positive,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    finish();
                                }
                            });

            AlertDialog alert = builder.create();
            alert.show();
        }

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
        if (savedInstanceState != null) {
            mCategories = savedInstanceState.getParcelableArrayList(getString(R.string.bundle_categories));
            mWelcomeTextView.setVisibility(View.GONE);
        } else {
            mCategories = new ArrayList<>();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }

        detachDatabaseReadListener();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(getString(R.string.bundle_categories), mCategories);
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
        Category selectedCategory = null;
        switch (item.getItemId()) {
            case R.id.nav_category_all:
                selectedCategory = mCategories.get(0);
                break;
            case R.id.nav_category_articles:
                selectedCategory = mCategories.get(1);
                break;
            case R.id.nav_category_generated:
                selectedCategory = mCategories.get(2);
                break;
            case R.id.nav_category_news:
                selectedCategory = mCategories.get(3);
                break;
            case R.id.nav_category_reviews:
                selectedCategory = mCategories.get(4);
                break;
            case R.id.nav_add:
                showFeedsSourceDialog();
                mDrawer.closeDrawer(GravityCompat.START);
                return true;
        }

        if (selectedCategory != null) {
            FeedsFetchingUtilities.scheduleFeedsReload(this, selectedCategory);

            arguments.putParcelable(getString(R.string.intent_category), selectedCategory);
            FeedsFragment fragment = new FeedsFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.feeds_container, fragment)
                    .commit();

            mWelcomeTextView.setVisibility(View.GONE);
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void onSignedInInitialize() {
        attachDatabaseReadListener();

        if (mUser != null) {
            TextView userNameView = findViewById(R.id.tv_user_name);
            TextView userEmailView = findViewById(R.id.tv_user_email);
            ImageView avatarView = findViewById(R.id.iv_user_avatar);

            if (userNameView != null && (mUser.getDisplayName() != null)) {
                userNameView.setText(mUser.getDisplayName());
            } else {
                Timber.e(getString(R.string.log_user_name_error));
            }

            if (userEmailView != null && (mUser.getEmail() != null)) {
                userEmailView.setText(mUser.getEmail());
            } else {
                Timber.e(getString(R.string.log_user_email_error));
            }

        /* No need for an error image or a placeholder
           If no image can be loaded, the default from the nav_header layout will be used
        */
            if (avatarView != null && (mUser.getPhotoUrl() != null)) {
                Picasso.with(this)
                        .load(mUser.getPhotoUrl())
                        .into(avatarView);
            } else {
                Timber.e(getString(R.string.log_user_avatar_error));
            }
        }
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showFeedsSourceDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        //AlertDialog.Builder alert = new AlertDialog.Builder(getApplicationContext(), R.style.CustomDialogTheme);

        final EditText edittext = new EditText(getApplicationContext());
        alert.setTitle(getString(R.string.app_name));
        alert.setMessage(getString(R.string.dialog_message));

        alert.setView(edittext);

        alert.setPositiveButton(getString(R.string.dialog_confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String newSource = edittext.getText().toString();

                addFeed(findViewById(R.id.drawer_layout), getApplicationContext(),
                        newSource, mCategoriesDatabaseReference);
            }
        });

        alert.setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Snackbar.make(findViewById(R.id.drawer_layout), getString(R.string.dialog_cancel_message),
                        Snackbar.LENGTH_LONG).show();
            }
        });

        alert.show();
    }
}
