package com.adserver.mraid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import com.adserver.mraid.constants.CalendarEvent;
import com.adserver.mraid.constants.CalendarRecurrence;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MraidUtilities {

    private static final String TAG = BrowserActivity.class.getName();

    public static Rect getFullScreenRect(Activity context) {
        Point size = new Point();

        Display display = context.getWindowManager().getDefaultDisplay();

        if (Build.VERSION.SDK_INT >= 17)
            display.getRealSize(size);
        else
            display.getSize(size);

        int realWidth = size.x;
        int realHeight = size.y;

        return new Rect(0, 0, realWidth, realHeight);
    }

    public static Rect getFullScreenRectDP(Activity context) {
        Rect fsRect = getFullScreenRect(context);
        return new Rect(0, 0, convertPixelsToDp(fsRect.width, context), convertPixelsToDp(fsRect.height, context));
    }

    public static void makePhoneCall(String number, Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + number));
            context.startActivity(intent);
        } catch (SecurityException ex) {
            // already checked by this point, so this shouldn't happen.
        }
    }

    public static void sendSMS(String number, Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null));
            context.startActivity(intent);
        } catch (SecurityException ex) {
            // already checked by this point, so this shouldn't happen.
        }
    }

    public static void writeCalendarEvent(CalendarEvent event, Activity context) {
        try {
            Date startDate = null;
            Date endDate = null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            try {
                startDate = dateFormat.parse(event.start);
                endDate = dateFormat.parse(event.end);
            } catch (Exception ex) {
                // null start or end
            }
            long startTime = startDate.getTime();
            long endTime = endDate.getTime();

            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.CALENDAR_ID, 1);
            values.put(CalendarContract.Events.TITLE, event.description);
            values.put(CalendarContract.Events.DESCRIPTION, event.summary);
            values.put(CalendarContract.Events.EVENT_LOCATION, event.location);
            values.put(CalendarContract.Events.DTSTART, startTime);
            values.put(CalendarContract.Events.DTEND, endTime);

            TimeZone timeZone = TimeZone.getDefault();
            values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
            if (event.recurrence != null) {
                String rule = getRecurrenceRule(event.recurrence);
                values.put(CalendarContract.Events.RRULE, rule);
            }

            Uri eventUri;
            eventUri = Uri.parse("content://com.android.calendar/events");

            Uri l_uri = context.getContentResolver().insert(eventUri, values);

            if (event.reminder != null) {
                values.put(CalendarContract.Events.HAS_ALARM, 1);
                try {
                    Date reminder = dateFormat.parse(event.reminder);
                    long reminderTime = reminder.getTime();
                    long difference = startTime - reminderTime;
                    long min = TimeUnit.MILLISECONDS.toMinutes(difference);
                    addReminder(l_uri, min, context);
                } catch (Exception ex) {
                    // not a date.  try a number
                    try {
                        int milliseconds = Math.abs(Integer.parseInt(event.reminder));
                        long min = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
                        addReminder(l_uri, min, context);
                    } catch (Exception numEx) {
                        numEx.printStackTrace();
                    }
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "Unable to save calendar event");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Calendar event added.")
                .setCancelable(false)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private static void addReminder(Uri l_uri, long minutesBefore, Activity context) {
        Uri REMINDERS_URI;
        long id = Long.parseLong(l_uri.getLastPathSegment()); //Added event id
        ContentValues reminders = new ContentValues();
        reminders.put(CalendarContract.Reminders.EVENT_ID, id);

        //METHOD_DEFAULT = 0, METHOD_ALERT = 1, METHOD_EMAIL = 2, METHOD_SMS = 3, METHOD_ALARM = 4
        reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);

        reminders.put(CalendarContract.Reminders.MINUTES, minutesBefore);
        REMINDERS_URI = Uri.parse("content://com.android.calendar/reminders");
        Uri uri = context.getContentResolver().insert(REMINDERS_URI, reminders);
        Log.d(TAG, "Added reminder" + uri.toString());
    }

    public static String getRecurrenceRule(CalendarRecurrence rec) {
        long expires = 0;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Date expiry = dateFormat.parse(rec.expires);
            if (expiry != null) {
                expires = expiry.getTime();
            }
        } catch (Exception ignored) {

        }
        String[] days = {"SU", "MO", "TU", "WE", "TH", "FR", "SA"};

        StringBuilder str = new StringBuilder();
        if (rec.frequency != null) {
            str.append("FREQ=");
            str.append(rec.frequency.toUpperCase());
        }
        if (expires > 0) {
            str.append(";UNTIL=");
            str.append(expires);
        }
        if (rec.daysInWeek.length > 0) {
            str.append(";BYDAY=");
            for (int i = 0; i < rec.daysInWeek.length; i++) {
                str.append(days[i]);
                if (i != rec.daysInWeek.length - 1) {
                    str.append(",");
                }
            }
        }
        if (rec.daysInMonth.length > 0) {
            str.append(";BYMONTHDAY=");
            for (int i = 0; i < rec.daysInMonth.length; i++) {
                str.append(i);
                if (i != rec.daysInMonth.length - 1) {
                    str.append(",");
                }
            }
        }
        if (rec.monthsInYear.length > 0) {
            str.append(";BYMONTH=");
            for (int i = 0; i < rec.monthsInYear.length; i++) {
                str.append(i);
                if (i != rec.monthsInYear.length - 1) {
                    str.append(",");
                }
            }
        }
        if (rec.daysInYear.length > 0) {
            str.append(";BYYEARDAY=");
            for (int i = 0; i < rec.daysInYear.length; i++) {
                str.append(i);
                if (i != rec.daysInYear.length - 1) {
                    str.append(",");
                }
            }
        }
        if (rec.weeksInYear.length > 0) {
            str.append(";BYWEEKNO=");
            for (int i = 0; i < rec.weeksInYear.length; i++) {
                str.append(i);
                if (i != rec.weeksInYear.length - 1) {
                    str.append(",");
                }
            }
        }
        return "";
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static int convertDpToPixel(int dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static int convertPixelsToDp(int px, Context context) {
        return (int) (px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
