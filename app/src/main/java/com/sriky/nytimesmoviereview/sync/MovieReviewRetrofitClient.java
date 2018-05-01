package com.sriky.nytimesmoviereview.sync;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.sriky.nytimesmoviereview.R;
import com.sriky.nytimesmoviereview.data.model.MovieReviewResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import timber.log.Timber;

/**
 * Class responsible for handling MovieReview API calls using {@link retrofit2.Retrofit}.
 */
public class MovieReviewRetrofitClient {
    @VisibleForTesting
    public static boolean TEST_MODE = false;
    @VisibleForTesting
    public static String BASE_TEST_URL = "";

    private static Retrofit sRetrofitInstance = null;

    /**
     * Gets the {@link MovieReviewResponse}
     * for API (http://api.nytimes.com/svc/movies/v2/reviews/dvd-picks.json).
     *
     * @return {@link MovieReviewResponse} object.
     */
    @Nullable
    public static MovieReviewResponse getMovieReviews(Context context, String orderBy, String apiKey) {
        try {
            Response<MovieReviewResponse> response = getApiService(context)
                    .getJsonResponse(orderBy, apiKey).execute();

            Timber.i("fetching data from: %s, sortOrder:%s, ApiKey: %s",
                    sRetrofitInstance.baseUrl(), orderBy, apiKey);
            if (response.isSuccessful()) {
                return response.body();
            } else {
                Timber.e(response.errorBody() != null ? response.errorBody().string() : null);
            }
        } catch (IOException e) {
            Timber.e(e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * Get Retrofit Instance
     */
    private static Retrofit getRetrofitInstance(Context context) {
        if (sRetrofitInstance == null) {
            sRetrofitInstance = new Retrofit.Builder()
                    .baseUrl(TEST_MODE ?
                            BASE_TEST_URL : context.getString(R.string.api_root_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return sRetrofitInstance;
    }

    @VisibleForTesting
    public static void resetClient() {
        sRetrofitInstance = null;
    }

    /**
     * Get API Service
     *
     * @return API Service
     */
    public static ApiService getApiService(Context context) {
        return getRetrofitInstance(context).create(ApiService.class);
    }


    /**
     * Interface to the REST API
     */
    private interface ApiService {

        @GET("dvd-picks.json")
        Call<MovieReviewResponse> getJsonResponse(@Query("order") String orderBy,
                                                  @Query("api-key") String apiKey);
    }
}
