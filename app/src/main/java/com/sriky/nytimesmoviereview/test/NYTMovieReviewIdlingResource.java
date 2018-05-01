package com.sriky.nytimesmoviereview.test;

import android.support.annotation.Nullable;
import android.support.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A simple idling resource used to facilitate UI testing when there are background tasks.
 */
public class NYTMovieReviewIdlingResource implements IdlingResource {
    @Nullable
    private volatile IdlingResource.ResourceCallback mCallback;

    // Idleness is controlled with this boolean.
    private AtomicBoolean mIsIdleNow = new AtomicBoolean(true);

    // used to identify the resource by appending it to the class name in getName method.
    private long mTime = System.currentTimeMillis();

    @Override
    public String getName() {
        return NYTMovieReviewIdlingResource.class.getName() + mTime;
    }

    @Override
    public boolean isIdleNow() {
        return mIsIdleNow.get();
    }

    @Override
    public void registerIdleTransitionCallback(IdlingResource.ResourceCallback callback) {
        mCallback = callback;
    }

    /**
     * Sets the new idle state, if isIdleNow is true, it pings the {@link IdlingResource.ResourceCallback}.
     *
     * @param isIdleNow false if there are pending operations, true if idle.
     */
    public void setIdleState(boolean isIdleNow) {
        mIsIdleNow.set(isIdleNow);
        if (isIdleNow && mCallback != null) {
            mCallback.onTransitionToIdle();
        }
    }
}
