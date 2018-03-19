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

import br.com.andregomesoliveira.gaminginsider.provider.FeedsUpdateJobService;

class FeedsFetchingUtilities {
    private static final int RELOADING_INTERVAL_MINUTES = 30; // Good time both for the widget as well as to wait for actual news
    private static final int RELOADING_INTERVAL_SECONDS = (int) (TimeUnit.MINUTES.toSeconds(RELOADING_INTERVAL_MINUTES));
    private static final int SYNC_FLEXTIME_SECONDS = RELOADING_INTERVAL_SECONDS;

    private static final String RELOADING_JOB_TAG = "feeds_reloading_tag";

    //TODO: Figure out the best place to call this

    /* Testes showed that I can not pass array lists here... */
    synchronized public static void scheduleFeedsreload(@NonNull final Context context) {
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
