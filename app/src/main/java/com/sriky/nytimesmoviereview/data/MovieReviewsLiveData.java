package com.sriky.nytimesmoviereview.data;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;

import com.sriky.nytimesmoviereview.BuildConfig;
import com.sriky.nytimesmoviereview.data.model.MovieReviewResponse;
import com.sriky.nytimesmoviereview.data.model.Result;

import java.util.List;

import timber.log.Timber;

public class MovieReviewsLiveData extends LiveData<List<Result>>
        implements FetchMovieReviewsAsyncTask.FetchMovieReviewsAsyncTaskCallback {

    // the time limit to wait for response before timing out.
    private static final long DATA_LOAD_TIMEOUT_LIMIT = 15000;
    private static final long COUNT_DOWN_INTERVAL = 1000;

    // async task responsible of fetching data in the background.
    private volatile FetchMovieReviewsAsyncTask mFetchMovieReviewsAsyncTask;
    // CountDownTimer used to keep track of time to bail out of data fetch operation.
    private volatile CountDownTimer mDataFetchTimer;
    // The application context passed from the FortuneViewModel.
    private Context mContext;

    public MovieReviewsLiveData(@NonNull Context context) {
        mContext = context;
    }

    /**
     * Loads the data from backend API using an {@link android.os.AsyncTask}
     *
     * @param sortOrder The sort order for the query.
     */
    public void loadData(@NonNull String sortOrder) {
        // sanity check, if the timer is already going then bail out.
        if (mDataFetchTimer != null) return;

        // set/reset the data to null as initial value.
        // setValue(null);

        // start the async task to trigger the data fetch operation.
        mFetchMovieReviewsAsyncTask = new FetchMovieReviewsAsyncTask(mContext,
                MovieReviewsLiveData.this, sortOrder, BuildConfig.NYTIMES_API_KEY);
        mFetchMovieReviewsAsyncTask.execute();

        // countdown timer used to keep track of time for the data fetch operation.
        mDataFetchTimer = new CountDownTimer(DATA_LOAD_TIMEOUT_LIMIT, COUNT_DOWN_INTERVAL) {

            public void onTick(long millisUntilFinished) {
                Timber.i("waiting on reviews... %d secs remaining for timeout!",
                        millisUntilFinished / COUNT_DOWN_INTERVAL);
            }

            public void onFinish() {
                onCountDownTimerEnded();
            }
        };
        mDataFetchTimer.start();
    }

    /**
     * Triggered when {@link CountDownTimer} ends.
     */
    private void onCountDownTimerEnded() {
        Timber.d("onCountDownTimerEnded()");
        mDataFetchTimer = null;
        if (mFetchMovieReviewsAsyncTask != null) {
            mFetchMovieReviewsAsyncTask.cancel(true);
        }
        mFetchMovieReviewsAsyncTask = null;

        // set the data
        setValue(null);
    }

    /**
     * Cancels {@link CountDownTimer} if running.
     */
    private void cancelDataFetchTimer() {
        if (mDataFetchTimer != null) {
            mDataFetchTimer.cancel();
            mDataFetchTimer = null;
        }
    }

    @Override
    public void onSuccess(MovieReviewResponse movieReviewResponse) {
        Timber.d("onSuccess()");
        // if the timer was running then cancel it.
        cancelDataFetchTimer();
        // set the data
        setValue(movieReviewResponse.getResults());
    }

    @Override
    public void onFailed() {
        Timber.d("onFailed()");
        // if the timer was running then cancel it.
        cancelDataFetchTimer();
        // set the data
        setValue(null);
    }
}
