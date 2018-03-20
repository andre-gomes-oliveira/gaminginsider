package br.com.andregomesoliveira.gaminginsider.provider;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import static br.com.andregomesoliveira.gaminginsider.utils.ParserUtilities.loadFeeds;

public class FeedsUpdateJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        loadFeeds(getApplicationContext(), null, null);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
