package ooo.oxo.excited.utils;

import android.content.Context;

import java.util.Calendar;
import java.util.Locale;

import ooo.oxo.excited.R;

public class RelativeDateFormat {

    private static final long ONE_MINUTE = 60000L;
    private static final long ONE_HOUR = 3600000L;
    private static final long ONE_DAY = 86400000L;
    private static final long ONE_WEEK = 604800000L;

    private static final int ONE_SECOND_AGO = R.string.s_ago;
    private static final int ONE_MINUTE_AGO = R.string.min_ago;
    private static final int ONE_HOUR_AGO = R.string.h_ago;
    private static final int ONE_DAY_AGO = R.string.d_ago;
    private static final int ONE_MONTH_AGO = R.string.month_ago;
    private static final int ONE_YEAR_AGO = R.string.y_ago;
    private static final int YESTERDAY = R.string.yesterday;

    public static String format(Context context, long timestamp) {
        long delta = Calendar.getInstance(Locale.getDefault()).getTimeInMillis() - timestamp;
        if (delta < ONE_MINUTE) {
            long seconds = toSeconds(delta);
            return (seconds <= 0 ? 1 : seconds) + context.getString(ONE_SECOND_AGO);
        }
        if (delta < 45L * ONE_MINUTE) {
            long minutes = toMinutes(delta);
            return (minutes <= 0 ? 1 : minutes) + context.getString(ONE_MINUTE_AGO);
        }
        if (delta < 24L * ONE_HOUR) {
            long hours = toHours(delta);
            return (hours <= 0 ? 1 : hours) + context.getString(ONE_HOUR_AGO);
        }
        if (delta < 48L * ONE_HOUR) {
            return context.getString(YESTERDAY);
        }
        if (delta < 30L * ONE_DAY) {
            long days = toDays(delta);
            return (days <= 0 ? 1 : days) + context.getString(ONE_DAY_AGO);
        }
        if (delta < 12L * 4L * ONE_WEEK) {
            long months = toMonths(delta);
            return (months <= 0 ? 1 : months) + context.getString(ONE_MONTH_AGO);
        } else {
            long years = toYears(delta);
            return (years <= 0 ? 1 : years) + context.getString(ONE_YEAR_AGO);
        }
    }

    private static long toSeconds(long date) {
        return date / 1000L;
    }

    private static long toMinutes(long date) {
        return toSeconds(date) / 60L;
    }

    private static long toHours(long date) {
        return toMinutes(date) / 60L;
    }

    private static long toDays(long date) {
        return toHours(date) / 24L;
    }

    private static long toMonths(long date) {
        return toDays(date) / 30L;
    }

    private static long toYears(long date) {
        return toMonths(date) / 365L;
    }

}