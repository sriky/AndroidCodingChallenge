package com.sriky.nytimesmoviereview.utils;

import android.content.Context;

import com.sriky.nytimesmoviereview.R;

/**
 * Class containing generic utility methods used the app.
 */
public class MovieReviewUtils {
    private static final float ONE_HOUR = 1;
    private static final float ONE_DAY = 1;
    private static final float TWENTY_FOUR_HOURS = 24;
    private static final float SECONDS_IN_MILLI = 1000;
    private static final float HOUR_IN_MINUTES = 60;
    private static final float MINUTES_IN_MILLI = SECONDS_IN_MILLI * HOUR_IN_MINUTES;
    private static final float HOURS_IN_MILLI = MINUTES_IN_MILLI * HOUR_IN_MINUTES;
    private static final float DAYS_IN_MILLI = HOURS_IN_MILLI * TWENTY_FOUR_HOURS;

    /**
     * Formats supplied date(in milli) to hours or mins from current time.
     *
     * @param context      The calling activity or service.
     * @param dateInMillis The date in millis.
     * @return String formatted to either hour or min from current time.
     */
    public static String getFormattedDateFromNow(Context context, long dateInMillis) {
        // calculate the days
        float difference = System.currentTimeMillis() - dateInMillis;
        float elapsedDays = difference / DAYS_IN_MILLI;
        if (elapsedDays >= ONE_DAY) {
            return String.format(context.getString(R.string.date_format_days), elapsedDays);
        }

        // calculate the hours
        difference = difference % DAYS_IN_MILLI;
        float elapsedHours = difference / HOURS_IN_MILLI;
        if (elapsedHours >= ONE_HOUR) {
            return String.format(context.getString(R.string.date_format_hours), elapsedHours);
        }

        // calculate the minutes.
        difference = difference % HOURS_IN_MILLI;
        float elapsedMinutes = difference / MINUTES_IN_MILLI;

        return String.format(context.getString(R.string.date_format_mins), elapsedMinutes);
    }
}
