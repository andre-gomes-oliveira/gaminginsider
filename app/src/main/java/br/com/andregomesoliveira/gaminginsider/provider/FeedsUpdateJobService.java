package br.com.andregomesoliveira.gaminginsider.provider;

import android.content.Context;
import android.os.Bundle;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.ArrayList;

import br.com.andregomesoliveira.gaminginsider.R;

public class FeedsUpdateJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        Context context = getApplicationContext();
        Bundle extras = jobParameters.getExtras();

        if (extras != null && context != null) {
            ArrayList<String> articles = extras.getStringArrayList(context.getString(R.string.intent_widget_feeds));
            ArrayList<String> links = extras.getStringArrayList(context.getString(R.string.intent_widget_links));

            FeedsIntentService.startActionUpdateFeedsWidgets(context, articles, links);
            return false;
        }

        //The job could not be done, so it must be re-scheduled
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
