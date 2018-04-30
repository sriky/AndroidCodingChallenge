package com.sriky.nytimesmoviereview.data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.sriky.nytimesmoviereview.R;
import com.sriky.nytimesmoviereview.data.model.Result;

import java.util.List;

public class MovieReviewsViewModel extends AndroidViewModel {
    private MovieReviewsLiveData mMovieReviewsLiveData;

    public MovieReviewsViewModel(@NonNull Application application) {
        super(application);
        mMovieReviewsLiveData = new MovieReviewsLiveData(application);
    }

    /**
     * Fetches movie reviews from API ordered by date.
     */
    public void fetchMovieReviewsOrderbyDate() {
        mMovieReviewsLiveData.loadData(getApplication().getString(R.string.ordered_by_date));
    }

    public LiveData<List<Result>> getData() { return mMovieReviewsLiveData; }
}
