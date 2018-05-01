package com.sriky.nytimesmoviereview;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;
import android.widget.TextView;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.sriky.nytimesmoviereview.helper.RestServiceTestHelper;
import com.sriky.nytimesmoviereview.sync.MovieReviewRetrofitClient;
import com.sriky.nytimesmoviereview.ui.MovieReviewActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.Iterator;

import timber.log.Timber;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Test cases to test the following API responses:
 * 1). Expected response.
 * 2). Error response.
 */

@RunWith(AndroidJUnit4.class)
public class APIResponseTests extends InstrumentationTestCase {
    private static final String SUCCESS_JSON_FILENAME = "success.json";
    private static final String ERROR_JSON_FILENAME = "404_error.json";
    private static final int SUCCESS_SERVER_RESPONSE_CODE = 200;
    private static final int ERROR_SERVER_RESPONSE_CODE = 404;
    private static final String SUCCESS_RESPONSE_MOVIE_TITLE = "Rogers Park";
    private static final String SNACKBAR_ERROR_MSG = "Unable to get reviews!";

    @Rule
    public ActivityTestRule<MovieReviewActivity> activityTestRule =
            new ActivityTestRule<>(MovieReviewActivity.class, true, false);

    private MockWebServer mServer;

    private IdlingResource mIdlingResource;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        //set up the mock server.
        mServer = new MockWebServer();
        mServer.start();

        injectInstrumentation(InstrumentationRegistry.getInstrumentation());

        MovieReviewRetrofitClient.resetClient();
        MovieReviewRetrofitClient.TEST_MODE = true;
        MovieReviewRetrofitClient.BASE_TEST_URL = mServer.url("/").toString();

        if (Timber.treeCount() == 0) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    @Test
    public void test_MovieReviewsAreDisplayedOnSuccessfulResponse() throws Exception {
        queueSuccessResponseToMockServer();

        Intent intent = new Intent();
        activityTestRule.launchActivity(intent);

        // snackbar with correct msg is displayed.
        onView(withText(R.string.data_updating))
                .check(matches(isDisplayed()));

        //setup the idling resource.
        mIdlingResource = activityTestRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);

        onView(withText(SUCCESS_RESPONSE_MOVIE_TITLE))
                .check(matches(isDisplayed()));
    }

    @Test
    public void test_ErrorIsDisplayedOnFailedResponse() throws Exception {
        queueFailedResponseToMockServer();

        Intent intent = new Intent();
        activityTestRule.launchActivity(intent);

        //setup the idling resource.
        mIdlingResource = activityTestRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);

        // snackbar with error msg.
        validateSnackbarIsDisplayingErrorMsg();
    }

    @After
    public void tearDown() throws Exception {
        mServer.shutdown();

        //unregister idling resources.
        Collection<IdlingResource> idlingResourceList = IdlingRegistry.getInstance().getResources();
        Iterator<IdlingResource> iterator = idlingResourceList.iterator();
        // while loop
        while (iterator.hasNext()) {
            IdlingRegistry.getInstance().unregister(iterator.next());
        }
    }

    /**
     * Validates if the snackbar is displaying the error msg.
     */
    private void validateSnackbarIsDisplayingErrorMsg() {
        Snackbar snackbar = activityTestRule.getActivity().getSnackbar();
        assertTrue(snackbar != null);

        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        assertTrue(snackbarLayout != null);

        // Now we can use standard findViewById
        TextView snackbarTextView = snackbarLayout.findViewById(R.id.snackbar_text);
        assertTrue(snackbarTextView != null);
        String errorMsg = snackbarTextView.getText().toString();
        assertTrue(errorMsg.equals(SNACKBAR_ERROR_MSG));
    }

    /**
     * Queues successful response
     */
    private void queueSuccessResponseToMockServer() {
        try {
            mServer.enqueue(new MockResponse()
                    .setResponseCode(SUCCESS_SERVER_RESPONSE_CODE)
                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(),
                            SUCCESS_JSON_FILENAME)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Queues 404 failure response
     */
    private void queueFailedResponseToMockServer() {
        try {
            mServer.enqueue(new MockResponse()
                    .setResponseCode(ERROR_SERVER_RESPONSE_CODE)
                    .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(),
                            ERROR_JSON_FILENAME)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
