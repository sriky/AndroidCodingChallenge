package com.sriky.nytimesmoviereview.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.sriky.nytimesmoviereview.R;
import com.sriky.nytimesmoviereview.data.MovieReviewsViewModel;
import com.sriky.nytimesmoviereview.data.model.Result;
import com.sriky.nytimesmoviereview.databinding.ActivityMovieReviewBinding;

import java.util.List;

import timber.log.Timber;

public class MovieReviewActivity extends AppCompatActivity {
    private ActivityMovieReviewBinding mActivityMovieReviewBinding;
    private MovieReviewsViewModel mMovieReviewsViewModel;
    private Snackbar mSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // plant debug tree if one doesn't exist!
        if (Timber.treeCount() == 0) {
            Timber.plant(new Timber.DebugTree());
        }

        // inflate the layout.
        mActivityMovieReviewBinding = DataBindingUtil.setContentView(MovieReviewActivity.this,
                R.layout.activity_movie_review);

        // set the appbar.
        setSupportActionBar(mActivityMovieReviewBinding.toolbar);
        // disable the tile.
        getSupportActionBar().setTitle("");

        // init the fortune ViewModel.
        mMovieReviewsViewModel = ViewModelProviders.of(MovieReviewActivity.this)
                .get(MovieReviewsViewModel.class);

        // set up the observer to the LiveData, so that UI will stay synced with latest data.
        mMovieReviewsViewModel.getData().observe(MovieReviewActivity.this, new Observer<List<Result>>() {
            @Override
            public void onChanged(@Nullable List<Result> results) {
                Timber.d("onChanged()");
                if (results != null) {
                    Timber.d("Results count: %d", results.size());
                    //TODO update the RV Adaptor.
                } else {
                    diplayError();
                }
            }
        });

        // don't make this call when a configuration change occurs.
        if (savedInstanceState == null) {
            initAndFetchReviews();
        }
    }

    /**
     * Initialize and trigger a data fetch.
     */
    private void initAndFetchReviews() {
        // fetch operation from API.
        mMovieReviewsViewModel.fetchMovieReviewsOrderbyDate();
        // display loading animation.
        displayLoadingAnimation();
    }


    /**
     * Displays the loading animation using {@link Snackbar}
     */
    private void displayLoadingAnimation() {
        if (mSnackbar == null) {
            mSnackbar = Snackbar.make(mActivityMovieReviewBinding.getRoot(),
                    R.string.data_updating, Snackbar.LENGTH_INDEFINITE);
            ViewGroup contentLay = (ViewGroup) mSnackbar.getView()
                    .findViewById(android.support.design.R.id.snackbar_text).getParent();

            ProgressBar item = new ProgressBar(MovieReviewActivity.this);
            contentLay.addView(item, 0);
        }
        mSnackbar.show();
    }

    /**
     * Display error using a Snackbar
     */
    private void diplayError() {
        Timber.d("diplayError");
        // hide the loading Snackbar.
        hideLoadingAnimation();
        // display the error msg.
        Snackbar.make(mActivityMovieReviewBinding.getRoot(),
                R.string.data_fetch_error, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Hides the Loading Animation.
     */
    private void hideLoadingAnimation() {
        if (mSnackbar != null) {
            mSnackbar.dismiss();
        }
    }
}
