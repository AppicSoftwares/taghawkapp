package com.taghawk.util;

import android.content.Context;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;
import com.taghawk.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by AppInventiv .
 */
public class TimeAgo {

    SimpleDateFormat simpleDateFormat, dateFormat;
    DateFormat timeFormat;
    Date dateTimeNow;
    String timeFromData;
    String pastDate;
    String sDateTimeNow;
    private HashMap<String, Object> timestampCreated;

    @Nullable
    Context context;

    private static final long SECOND_MILLIS = 1000;
    private static final long MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final long DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final long WEEKS_MILLIS = 7 * DAY_MILLIS;
    private static final long MONTHS_MILLIS = 4 * WEEKS_MILLIS;
    private static final long YEARS_MILLIS = 12 * MONTHS_MILLIS;

    public TimeAgo() {
        HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNow;
        simpleDateFormat = new SimpleDateFormat("dd/M/yyyy HH:mm:ss");
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        timeFormat = new SimpleDateFormat("h:mm aa");

        Date now = new Date();
        sDateTimeNow = simpleDateFormat.format(now);

        try {
            dateTimeNow = simpleDateFormat.parse(sDateTimeNow);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Exclude
    public long getTimestampCreatedLong(){
        return (long)timestampCreated.get("timestamp");
    }

    public TimeAgo locale(@NonNull Context context) {
        this.context = context;
        return this;
    }

    public TimeAgo with(@NonNull SimpleDateFormat simpleDateFormat) {
        this.simpleDateFormat = simpleDateFormat;
        this.dateFormat = new SimpleDateFormat(simpleDateFormat.toPattern().split(" ")[0]);
        this.timeFormat = new SimpleDateFormat(simpleDateFormat.toPattern().split(" ")[1]);
        return this;
    }

    public String getTimeAgo(Date startDate, Context context) {

        //  date counting is done till todays date
        Date endDate = dateTimeNow;

        //  time difference in milli seconds
        long different = endDate.getTime() - startDate.getTime();

        if (context == null) {
            if (different < MINUTE_MILLIS) {
                return context.getResources().getString(R.string.just_now);
            } else if (different < 2 * MINUTE_MILLIS) {
                return context.getResources().getString(R.string.a_min_ago);
            } else if (different < 50 * MINUTE_MILLIS) {
                return different / MINUTE_MILLIS + context.getString(R.string.mins_ago);
            } else if (different < 90 * MINUTE_MILLIS) {
                return context.getString(R.string.a_hour_ago);
            } else if (different < 24 * HOUR_MILLIS) {
                timeFromData = timeFormat.format(startDate);
                return timeFromData;
            } else if (different < 48 * HOUR_MILLIS) {
                return context.getString(R.string.yesterday);
            } else if (different < 7 * DAY_MILLIS) {
                return different / DAY_MILLIS + context.getString(R.string.days_ago);
            } else if (different < 2 * WEEKS_MILLIS) {
                return different / WEEKS_MILLIS + context.getString(R.string.week_ago);
            } else if (different < 3.5 * WEEKS_MILLIS) {
                return different / WEEKS_MILLIS + context.getString(R.string.weeks_ago);
            } else if (different < 2 * MONTHS_MILLIS) {
                return different / MONTHS_MILLIS + context.getString(R.string.month_ago);
            } else if (different < 12 * MONTHS_MILLIS) {
                return different / MONTHS_MILLIS + context.getString(R.string.months_ago);
            } else if (different < YEARS_MILLIS) {
                return different / YEARS_MILLIS + context.getString(R.string.year_ago);
            } else {
                return different / YEARS_MILLIS + context.getString(R.string.year_ago);
            }
//            else {
//                pastDate = dateFormat.format(startDate);
//                return pastDate;
//            }
        } else {
            if (different < MINUTE_MILLIS) {
                return context.getResources().getString(R.string.just_now);
            } else if (different < 2 * MINUTE_MILLIS) {
                return context.getResources().getString(R.string.a_min_ago);
            } else if (different < 50 * MINUTE_MILLIS) {
                return different / MINUTE_MILLIS + context.getString(R.string.mins_ago);
            } else if (different < 90 * MINUTE_MILLIS) {
                return context.getString(R.string.a_hour_ago);
            } else if (different < 24 * HOUR_MILLIS) {
                timeFromData = timeFormat.format(startDate);
                return timeFromData;
            } else if (different < 48 * HOUR_MILLIS) {
                return context.getString(R.string.yesterday);
            } else if (different < 7 * DAY_MILLIS) {
                return different / DAY_MILLIS + context.getString(R.string.days_ago);
            } else if (different < 2 * WEEKS_MILLIS) {
                return different / WEEKS_MILLIS + context.getString(R.string.week_ago);
            } else if (different < 3.5 * WEEKS_MILLIS) {
                return different / WEEKS_MILLIS + context.getString(R.string.weeks_ago);
            } else if (different < 2 * MONTHS_MILLIS) {
                return different / MONTHS_MILLIS + context.getString(R.string.month_ago);
            } else if (different < 12 * MONTHS_MILLIS) {
                return different / MONTHS_MILLIS + context.getString(R.string.months_ago);
            } else {
                return different / YEARS_MILLIS + context.getString(R.string.year_ago);
            }
//            else {
//                pastDate = dateFormat.format(startDate);
//                return pastDate;
//            }
        }
    }

    public String getFormattedTimeInDaysAgo(long actualMillis, Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        Calendar actualCalendar=Calendar.getInstance();
        actualCalendar.setTimeInMillis(actualMillis);
        actualCalendar.setTimeZone(TimeZone.getDefault());
        long currentMillis = calendar.getTimeInMillis();
        //  time difference in milli seconds
        long different = currentMillis - actualMillis;
        if (different < MINUTE_MILLIS) {
            return context.getResources().getString(R.string.just_now);
        } else if (different < 2 * MINUTE_MILLIS) {
            return context.getResources().getString(R.string.a_min_ago);
        } else if (different <  HOUR_MILLIS) {
            return different / MINUTE_MILLIS + context.getString(R.string.mins_ago);
        } else if (different < 2 * HOUR_MILLIS) {
            return context.getString(R.string.a_hour_ago);
        }
        else if (different < DAY_MILLIS) {
            return different/HOUR_MILLIS+" "+context.getString(R.string.hours_ago);
        }else if (different < 2* DAY_MILLIS) {
            return context.getString(R.string.yesterday);
        } else if (different < WEEKS_MILLIS) {
            return different / DAY_MILLIS + context.getString(R.string.days_ago);
        } else if (different < 2 * WEEKS_MILLIS) {
            return different / WEEKS_MILLIS + context.getString(R.string.week_ago);
        } else if (different < MONTHS_MILLIS) {
            return different / WEEKS_MILLIS + context.getString(R.string.weeks_ago);
        } else if (different < 2 * MONTHS_MILLIS) {
            return different / MONTHS_MILLIS + context.getString(R.string.month_ago);
        } else if (different < YEARS_MILLIS) {
            return different / MONTHS_MILLIS + context.getString(R.string.months_ago);
        } else if (different < 2*YEARS_MILLIS) {
            return different / YEARS_MILLIS + context.getString(R.string.year_ago);
        } else {
            return different / YEARS_MILLIS +" "+ context.getString(R.string.years_ago);
        }
    }

    public boolean checkCurrentDate(long timeStamp)
    {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MMM-yyyy");
        Date actualDate=new Date(timeStamp);
        Date currentDate=Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
        String currentDateString=simpleDateFormat.format(currentDate);
        String actualDateString=simpleDateFormat.format(actualDate);
        return currentDateString.equalsIgnoreCase(actualDateString);
    }

    public boolean checkTimeDifference(long timeStamp)
    {
        long currentTimeStamp=Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime().getTime();
        long difference=currentTimeStamp-timeStamp;
        int minutes= (int)TimeUnit.MILLISECONDS.toMinutes(difference);
        return minutes<10;
    }

    public String getFormattedDate(Context context,long timeStamp)
    {
        Calendar currentCalendar=Calendar.getInstance(TimeZone.getDefault());
        Date currentDate=currentCalendar.getTime();
        Calendar calendar=Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(timeStamp);
        Date actualDate=calendar.getTime();
        String currentDateString=new SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault()).format(currentDate);
        String actualDateString=new SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault()).format(actualDate);
        if (currentDateString.equalsIgnoreCase(actualDateString))
            return context.getString(R.string.text_today)+", "+new SimpleDateFormat("HH:mm", Locale.getDefault()).format(actualDate);
        else
        {
            currentCalendar.add(Calendar.DATE,-1);
            currentDateString=new SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault()).format(currentCalendar.getTime());
            if (currentDateString.equalsIgnoreCase(actualDateString))
                return context.getString(R.string.yesterday)+", "+new SimpleDateFormat("HH:mm", Locale.getDefault()).format(actualDate);
            else
                return new SimpleDateFormat("dd MMMM, yyyy, HH:mm", Locale.getDefault()).format(actualDate);
        }
    }

    public String getFormattedTime(long timeStamp)
    {
        Calendar calendar=Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(timeStamp);
        return new SimpleDateFormat("hh:mm a",Locale.getDefault()).format(calendar.getTime());
    }
}