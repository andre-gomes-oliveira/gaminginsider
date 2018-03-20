package br.com.andregomesoliveira.gaminginsider.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

import br.com.andregomesoliveira.gaminginsider.model.Category;
import br.com.andregomesoliveira.gaminginsider.provider.FeedsUpdateJobService;

import static br.com.andregomesoliveira.gaminginsider.utils.ParserUtilities.setCurrentCategory;

public class FeedsFetchingUtilities {
    private static final int RELOADING_INTERVAL_MINUTES = 1; // Good time both for the widget as well as to wait for actual news
    private static final int RELOADING_INTERVAL_SECONDS = (int) (TimeUnit.MINUTES.toSeconds(RELOADING_INTERVAL_MINUTES));
    private static final int SYNC_FLEXTIME_SECONDS = RELOADING_INTERVAL_SECONDS;

    private static final String RELOADING_JOB_TAG = "feeds_reloading_tag";

    /* Testes showed that I can not pass array lists here... */
    synchronized public static void scheduleFeedsReload(@NonNull final Context context,
                                                        @NonNull final Category category) {
        //Updating the category so that the job will continuously fetch from the one the user selected
        setCurrentCategory(category);

        //Dispatching the job that will keep the feeds updated
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job reloadingJob = dispatcher.newJobBuilder()
                .setService(FeedsUpdateJobService.class)
                .setTag(RELOADING_JOB_TAG)
                .setConstraints(Constraint.DEVICE_CHARGING)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        RELOADING_INTERVAL_SECONDS,
                        RELOADING_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(reloadingJob);
    }
}
