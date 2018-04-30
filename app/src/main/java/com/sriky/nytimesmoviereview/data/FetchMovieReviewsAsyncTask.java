package com.sriky.nytimesmoviereview.data;

import android.content.Context;
import android.os.AsyncTask;

import com.sriky.nytimesmoviereview.data.model.MovieReviewResponse;
import com.sriky.nytimesmoviereview.sync.MovieReviewRetrofitClient;

import java.lang.ref.WeakReference;

/**
 * {@link AsyncTask} used to fetch {@link com.sriky.nytimesmoviereview.data.model.MovieReviewResponse}.
 */
public class FetchMovieReviewsAsyncTask extends AsyncTask<Void, Void, MovieReviewResponse> {
    private WeakReference<Context> mContextWeakReference;
    private FetchMovieReviewsAsyncTaskCallback mCallback;
    private String mSortOrder;
    private String mApiKey;

    public FetchMovieReviewsAsyncTask(Context context, FetchMovieReviewsAsyncTaskCallback callback,
                                      String sortOrder, String apiKey) {
        mContextWeakReference = new WeakReference<>(context);
        mCallback = callback;
        mSortOrder = sortOrder;
        mApiKey = apiKey;
    }

    @Override
    protected MovieReviewResponse doInBackground(Void... voids) {
        return MovieReviewRetrofitClient.getMovieReviews(mContextWeakReference.get(), mSortOrder, mApiKey);
    }

    @Override
    protected void onPostExecute(MovieReviewResponse response) {
        if (response == null || response.getResults() == null) {
            mCallback.onFailed();
            return;
        }
        mCallback.onSuccess(response);
    }

    // callback interface.
    interface FetchMovieReviewsAsyncTaskCallback {
        void onSuccess(MovieReviewResponse movieReviewResponse);

        void onFailed();
    }
}
