package com.taghawk.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.taghawk.R;
import com.taghawk.constants.AppConstants;
import com.taghawk.databinding.BottomSheetDialogDeleteBinding;
import com.taghawk.interfaces.OnDialogItemClickListener;
import com.taghawk.interfaces.RecyclerViewCallback;
import com.taghawk.model.home.ImageList;
import com.taghawk.ui.home.ZoomImageActivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

public class AppUtils {
    private static double PI_RAD = Math.PI / 180.0;
    private static ProgressDialog pgdialog;


    //Function is use for hiding keyboard
    public static void hideKeyboard(Activity context) {
        try {
            // use application level context to avoid unnecessary leaks.
            if (context != null) {
                InputMethodManager inputManager = (InputMethodManager) context.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(Objects.requireNonNull(context.getCurrentFocus()).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //This Function is use for show keyboard
    public static void showKeyboard(Activity context) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(context.getCurrentFocus(), InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Finction is use for get unique device Id
    @SuppressLint("HardwareIds")
    public static String getUniqueDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Gets the version name of the application. For e.g. 1.9.3
     **/
    public static String getApplicationVersionNumber(Context context) {
        String versionName = null;
        if (context == null) {
            return versionName;
        }
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * Gets the version code of the application. For e.g. Maverick Meerkat or 2013050301
     **/
    public static int getApplicationVersionCode(Context ctx) {
        int versionCode = 0;
        try {
            versionCode = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * Gets the version number of the Android OS For e.g. 2.3.4 or 4.1.2
     **/
    public static String getOsVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * This function is use for getting Date Object from Timestamp
     */
    public static Date timeStampToDate(long timeStamp) {

        SimpleDateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS Z");

        Date result = new Date(timeStamp);

        System.out.println(simple.format(result));
        return result;
    }

    /**
     * This function is use to convert timestamp to string date
     */
    public static String timestampToStringDate(long timeStamp) {

//        SimpleDateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS Z");
        SimpleDateFormat simple = new SimpleDateFormat("dd MMM yyyy");

        Date result = new Date(timeStamp);

        // Formatting Date according to the
        // given format
        System.out.println(simple.format(result));
        //            return sdf.parse(dat);
        return simple.format(result);
//        return null;
    }


    public static String timeStampStringDate(long timeStamp) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timeStamp);
        String dat = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();

        try {
            return AppUtils.getDateInMonthDay(dat);
        } catch (Exception e) {
            return "";
        }
    }


    public static String convertDate(String time) {
        time = getDateTimeFromUTC(time);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss aa", Locale.ENGLISH);
        Date d = null;
        try {
            d = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        long timeStamp = d.getTime();

        String relavetime = "" + DateUtils.getRelativeTimeSpanString(timeStamp, Calendar.getInstance().getTimeInMillis(), DateUtils.FORMAT_ABBREV_ALL);
        return relavetime;
    }

    /*
     * Convet UTC Date Zone to Local
     * */
    public static String getDateTimeFromUTC(String utcdate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss aa", Locale.ENGLISH);
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date d = convertStringToDate(utcdate, inputFormat);
        java.text.DateFormat outputFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss aa", Locale.ENGLISH);
        outputFormat.setTimeZone(java.util.TimeZone.getDefault());

        String s = convertDatetoString(d, outputFormat);

        return s;
    }

    /**
     * Convert String date to Date object
     */
    public static Date convertStringToDate(String dateString, SimpleDateFormat format) {
        Date mdate = null;
        try {
            mdate = format.parse(dateString);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return mdate;
    }

    public static String convertDatetoString(Date date, java.text.DateFormat format) {
        String dateString = "";
        if (date != null) {
            dateString = format.format(date);
        }
        return dateString;
    }

    public static String getState(Context context, LatLng place) {
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {

            addresses = gcd.getFromLocation(place.latitude, place.longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                System.out.println(addresses.get(0).getAdminArea());
                return addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    //its return City Name using ZipCode

    public static String getCityName(Context context, String zipCode) {
        final Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(zipCode, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            // handle exception
        }
        return "";
    }
    //its return City Name using ZipCode

    public static String getStateName(Context context, String zipCode) {
        final Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(zipCode, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAdminArea();
            }
        } catch (IOException e) {
            // handle exception
        }
        return "";
    }


    public static String getCountryCode(Context context, String zipCode) {
        final Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(zipCode, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getCountryCode();
            }
        } catch (IOException e) {
            // handle exception
        }
        return "";
    }


    public static String getStateSortName(Context context, String zipCode) {
        final Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(zipCode, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0).substring(addresses.get(0).getLocality().length() + 1, addresses.get(0).getLocality().length() + 4);
            }
        } catch (IOException e) {
            // handle exception
        }
        return "";
    }

    //    /*Convert the Date into May 01 this formate*/
    public static String getDateInMonthDay(String strDate) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            Date date = format.parse(strDate);
//            DateFormat weekDay = new SimpleDateFormat("E");
            java.text.DateFormat monthFormate = new SimpleDateFormat("MMMM", Locale.ENGLISH);
            java.text.DateFormat dateNumberice = new SimpleDateFormat("dd", Locale.ENGLISH);
            java.text.DateFormat yearFormate = new SimpleDateFormat("y");
//            String day = weekDay.format(date);
            String month = monthFormate.format(date);
//            String date1 = dateNumberice.format(date);
            String year = yearFormate.format(date);
            return month + " " + year;
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /*
     * mehtod is ued to customized the status bar
     * */
    public static void setStatusBar(Activity activity, int color, boolean wantBlackIcons, int transparencyValue, boolean wantTranslucentStatusBar) {
        if (!wantBlackIcons) {
            StatusBarCompat.cancelLightStatusBar(activity);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarCompatLollipop.setStatusBarColor(activity, color);
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP ||
                    Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1 &&
                            color == activity.getResources().getColor(R.color.White)) {
                StatusBarCompat.setStatusBarColor(activity, color, 100);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            StatusBarCompatKitKat.setStatusBarColor(activity, color);
        }
        if (transparencyValue != 0) {
            StatusBarCompat.setStatusBarColor(activity, color, transparencyValue);
        }
        if (wantTranslucentStatusBar) {
            StatusBarCompat.translucentStatusBar(activity, wantTranslucentStatusBar);
        }
        if (wantBlackIcons) {
            StatusBarCompat.changeToLightStatusBar(activity);
        }
    }

    public static Location getCurrentLocation(Context context) {
        LocationManager loc = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Location location = loc.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double clong = location.getLongitude();
        double clat = location.getLatitude();
        return location;
    }


    public static int calculateDistance(Double eventLat, Double eventLong, Location location) {
//
        Location location1 = location;
        Double distance = null;
        Double myLat;
        Double myLong;
        myLat = 0.0;
        myLong = 0.0;
        myLat = location.getLatitude();
        myLong = location.getLongitude();

        Location startPoint = new Location("A");
        startPoint.setLatitude(myLat);
        startPoint.setLongitude(myLong);

        Location endPoint = new Location("B");
        endPoint.setLatitude(eventLat);
        endPoint.setLongitude(eventLong);
        distance = Double.valueOf((startPoint.distanceTo(endPoint)));
        return roundOffTheMeters(0.621371 * distance, 10);
    }


    public static int calculateDistance(Double eventLat, Double eventLong, Double lat, Double longi) {
//
        Double distance = null;
        Double myLat;
        Double myLong;
        myLat = 0.0;
        myLong = 0.0;
        myLat = lat;
        myLong = longi;

        Location startPoint = new Location("A");
        startPoint.setLatitude(myLat);
        startPoint.setLongitude(myLong);

        Location endPoint = new Location("B");
        endPoint.setLatitude(eventLat);
        endPoint.setLongitude(eventLong);
        distance = Double.valueOf((startPoint.distanceTo(endPoint)));
        return roundOffTheMeters(0.621371 * distance, 10);
    }

    public static int roundOffTheMeters(Double number, int roundOffDigits) {
        String finalDistance = "";
        double result = 0;
        result = meterTokm(number, roundOffDigits);
        int result1 = (int) result;
        try {
            return result1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static double convertNumber(Double number, int roundOffDigits) {
        DecimalFormat decimalFormat = new DecimalFormat("#.0");
        decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
        Double twoDigitsF = Double.valueOf((decimalFormat.format(number)));
        int intNumber = (int) (twoDigitsF * 10);

        int a = (intNumber / roundOffDigits) * roundOffDigits;

        int b = a + roundOffDigits;
        Double result = (double) ((intNumber - a > b - intNumber) ? b : a);
        result = result / 10;
        return result;
    }

    private static Double meterTokm(Double number, int roundOffDigits) {

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
        DecimalFormat formatter = (DecimalFormat) nf;
        formatter.applyPattern("0.0");
        Double twoDigitsF = Double.valueOf((formatter.format(number / 1000)));
        int intNumber = (int) (twoDigitsF * 10);


        int a = (intNumber / roundOffDigits) * roundOffDigits;

        int b = a + roundOffDigits;
        Double result = (double) ((intNumber - a > b - intNumber) ? b : a);
        result = result / 10;
        return result;
    }

    /*Getting address of the selected eventLatitude lng from Geocoder when another location is selected*/
    public static Address getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address = null;
        try {
            address = coder.getFromLocationName(strAddress, 5);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (address == null) {
            return null;
        }
        Address location = null;
        if (!address.isEmpty())
            location = address.get(0);


        return location;
    }

    public static Address getAddressByLatLng(Context context, Double lat, Double lng) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(context, Locale.getDefault());
        Address address = null;
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//            address = addresses.get(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            address = addresses.get(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }

    public static String getAddressByLatLng1(Context context, Double lat, Double lng) {
        Geocoder geocoder;

        List<Address> addresses = null;
        geocoder = new Geocoder(context, Locale.getDefault());
        Address address = null;
        String address1 = null;
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//            address = addresses.get(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            address = addresses.get(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            address1 = city;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return address1;
    }

    public static String getAddressByLatLng2(Context context, Double lat, Double lng) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(context, Locale.getDefault());
        Address address = null;
        String address1 = null;
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//            address = addresses.get(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            address = addresses.get(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            address1 = state;




        } catch (IOException e) {
            e.printStackTrace();
        }

        return address1;
    }

    public static void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("Facebook", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("Facebook", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("Facebook", "printHashKey()", e);
        }
    }

    public static void share(Context ctx, String sharingMsg, String emailSubject, String title) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);

        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, sharingMsg);
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);

        ctx.startActivity(Intent.createChooser(sharingIntent, title));
    }

    public static void slideRight(LinearLayout view) {
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                view.getWidth(),                 // toXDelta
                0,                 // fromYDelta
                0); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public static void calculateDeviceWidth(Context context) {

    }

    public static void slideUp(final LinearLayout view) {
//        view.setVisibility(View.VISIBLE);
//        if (view.getVisibility() == View.VISIBLE) {
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(300);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animate);
        view.setClickable(true);
//        navigationView.setVisibility(View.VISIBLE);

//        }
    }

    // slide the view from its current position to below itself
    public static void slideDown(final LinearLayout view) {
        if (view.getVisibility() == View.VISIBLE) {
            TranslateAnimation animate = new TranslateAnimation(
                    0,                 // fromXDelta
                    0,                 // toXDelta
                    0,                 // fromYDelta
                    view.getHeight()); // toYDelta
            animate.setDuration(500);
            animate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.startAnimation(animate);
            view.setClickable(false);

//            navigationView.setVisibility(View.GONE);
        }
    }

    public static String getDateStringFromTimestamp(long timeInMillies) {
        String strFormattedDate = "";
//        java.text.DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy");
        java.text.DateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");
        try {
            strFormattedDate = dateFormat.format(new Date(timeInMillies));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strFormattedDate;
    }

    public static String getProductCondition(Context context, int condition) {
        String productCondition = "";
        switch (condition) {
            case 1:
                productCondition = context.getString(R.string.new_never_used);
                break;
            case 2:
                productCondition = context.getString(R.string.like_new_rarely_used);
                break;
            case 3:
                productCondition = context.getString(R.string.good_gently_used);
                break;
            case 4:
                productCondition = context.getString(R.string.normal_normal_wear);
                break;
            case 5:
                productCondition = context.getString(R.string.flawed_with_flaw);
                break;
        }

        return productCondition;
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor;
        String filePath = "";
        if (contentUri == null)
            return filePath;
        if (contentUri.getAuthority() != null) {
            if (isNewGooglePhotosUri(contentUri)) {
                try {
                    InputStream is = context.getContentResolver().openInputStream(contentUri);
                    Bitmap bmp = BitmapFactory.decodeStream(is);
                    File file = FileUtils.getInstance().saveBitmapAsTempFile(context, bmp);
                    if (file.exists())
                        filePath = file.getPath();
                    if (is != null)
                        is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (!TextUtils.isEmpty(filePath))
                return filePath;
        }
        File file = new File(contentUri.getPath());
        if (file.exists())
            filePath = file.getPath();
        if (!TextUtils.isEmpty(filePath))
            return filePath;
        String[] proj = {MediaStore.Images.Media.DATA};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                String wholeID = DocumentsContract.getDocumentId(contentUri);
                // Split at colon, use second item in the array
//                String[] split = wholeID.split(":");
                String id;
                if (wholeID.contains(":"))
                    id = wholeID.split(":")[1];

                else id = wholeID;
//                if (split.length > 1)
//                    id = split[1];
//                else id = wholeID;
                // where id is equal to
                cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj, MediaStore.Images.Media._ID + "='" + id + "'", null, null);
                if (cursor != null) {
                    int columnIndex = cursor.getColumnIndex(proj[0]);
                    if (cursor.moveToFirst())
                        filePath = cursor.getString(columnIndex);
                    if (!TextUtils.isEmpty(filePath))
                        contentUri = Uri.parse(filePath);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(filePath))
            return filePath;
        try {
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            if (cursor == null)
                return contentUri.getPath();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.moveToFirst())
                filePath = cursor.getString(column_index);
            if (!cursor.isClosed())
                cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            filePath = contentUri.getPath();
        }
        if (filePath == null)
            filePath = "";
        return filePath;
    }

    private static boolean isNewGooglePhotosUri(Uri uri) {
        return uri.getAuthority().startsWith("com.google.android.apps");
    }

    /**
     * This function is used to show a progress dialog
     */
    public static void showDialog(Context mContext, String strMessage) {
        try {
            if (pgdialog != null)
                if (pgdialog.isShowing())
                    pgdialog.dismiss();
            pgdialog = ProgressDialog.show(mContext, null, null, true);
            pgdialog.setContentView(R.layout.progress_dialog);
            pgdialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pgdialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This function is used to dismiss the dialog
     */
    public static void dismissDialog() {
        try {
            if (pgdialog != null && pgdialog.isShowing())
                pgdialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    /*
     * open my application setting
     * */
    public static void openAppSettings(Context mActivity) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", mActivity.getPackageName(), null);
        intent.setData(uri);

        mActivity.startActivity(intent);
    }

    public static void loadImage(Context context, String url, Drawable placeHolder, AppCompatImageView view) {
        Glide.with(context).asBitmap().load(url).apply(RequestOptions.placeholderOf(placeHolder)).into(view);
    }

    @SuppressLint("CheckResult")
    public static void loadNormalImage(Context context, String url, int placeholder, ImageView imageView, boolean isCenterCrop) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(placeholder);
        requestOptions.error(placeholder);
        if (isCenterCrop)
            requestOptions.centerCrop();
        Glide.with(context).applyDefaultRequestOptions(requestOptions).load(url).into(imageView);
    }

    @SuppressLint("CheckResult")
    public static void loadCircularImage(Context context, String url, int radius, int placeholder, ImageView imageView, boolean isCenterCrop) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(placeholder);
        requestOptions.error(placeholder);
        if (isCenterCrop)
            requestOptions.centerCrop();

        if (context != null) {
            try {
                Glide.with(context).load(url).apply(requestOptions.transforms(new RoundedCorners(radius))).into(imageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isConnection(Context mContext) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
        return ni != null && ni.isAvailable() && ni.isConnected();
    }

    public static void openBottomSheetDialog(Context context, String negativeButtonText, String positiveButtonText, String title, String subtitle, final OnDialogItemClickListener onDialogItemClickListener) {
        final BottomSheetDialog mDialog = new BottomSheetDialog(context);
        final BottomSheetDialogDeleteBinding binding = BottomSheetDialogDeleteBinding.inflate(LayoutInflater.from(context));
        mDialog.setContentView(binding.getRoot());
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) binding.main.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        ((View) binding.main.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        binding.tvCancel.setText(negativeButtonText);
        binding.tvDelete.setText(positiveButtonText);
        binding.tvTitle.setText(title);
        binding.tvSubtitle.setText(subtitle);
        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDialogItemClickListener.onNegativeBtnClick();
                mDialog.dismiss();
            }
        });
        binding.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                onDialogItemClickListener.onPositiveBtnClick();
            }
        });
        mDialog.show();
    }

    public static long getTimeStamp() {
        Date date = new Date();
        long time = date.getTime();
//        System.out.println("Time in Milliseconds: " + time);
        return time;
    }

    /**
     * Checks if the Internet connection is available.
     *
     * @return Returns true if the Internet connection is available. False otherwise.
     **/
    public static boolean isInternetAvailable(Context ctx) {
        // using received context (typically activity) to get SystemService causes memory link as this holds strong reference to that activity.
        // use application level context instead, which is available until the app dies.
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // if network is NOT available networkInfo will be null
        // otherwise check if we are connected
        return networkInfo != null && networkInfo.isConnected();
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static SpannableString getSpannableString(Context mContext, String text, int color, float textSize, boolean isUnderline, boolean isBold, boolean isClickable, final RecyclerViewCallback onClickCallBack) {
        SpannableString ss = new SpannableString(text);
        if (isClickable) {
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    onClickCallBack.onClick(0, textView);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(true);
                }
            };
            ss.setSpan(clickableSpan, 0, text.length(), 0);
        }
        ss.setSpan(new RelativeSizeSpan(textSize), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // set size
        if (isUnderline)
            ss.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        if (isBold) {
            StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
            ss.setSpan(boldSpan, 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        ss.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(color)), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);// set color
        return ss;
    }

    public static String generateRandomAlphanumericSting() {
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    public static boolean isForground() {
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE);
    }

    //    public static int getAge(){
//        Calendar today = Calendar.getInstance();
//        24
//
//        25
//        int curYear = today.get(Calendar.YEAR);
//        26
//        int dobYear = dob.get(Calendar.YEAR);
//        27
//
//        28
//        int age = curYear - dobYear;
//        29
//
//        30
//        // if dob is month or day is behind today's month or day
//        31
//        // reduce age by 1
//        32
//        int curMonth = today.get(Calendar.MONTH);
//        33
//        int dobMonth = dob.get(Calendar.MONTH);
//        34
//        if (dobMonth > curMonth) { // this year can't be counted!
//            35
//            age--;
//            36
//        } else if (dobMonth == curMonth) { // same month? check for day
//            37
//            int curDay = today.get(Calendar.DAY_OF_MONTH);
//            38
//            int dobDay = dob.get(Calendar.DAY_OF_MONTH);
//            39
//            if (dobDay > curDay) { // this year can't be counted!
//                40
//                age--;
//                41
//            }
//                 }
//        return age;
//
//    }

    //this method will create a directory in which captured image can be set
    public static File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "TagHawk");
        if (!storageDir.exists())
            storageDir.mkdirs();
        File image = File.createTempFile(
                imageFileName, ".jpg", storageDir
        );
        return image;
    }

    public static String getFilePath() {
        File file = new File(AppConstants.APP_IMAGE_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;
    }

    public static void openFullViewImage(Context context, String url) {
        ArrayList<ImageList> imageLists = new ArrayList<>();
        ImageList image = new ImageList();
        image.setThumbUrl(url);
        image.setUrl(url);
        imageLists.add(image);
        Intent intent = new Intent(context, ZoomImageActivity.class);
        intent.putExtra("ImageUrl", imageLists);
        context.startActivity(intent);
    }

    public static double roundOffDigits(double num) {
        return Math.round((num * 100.0) / 100.0);
    }

    public static String getVideoId(String url) {
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url); //url is youtube url for which you want to extract the id.
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

//    private static boolean isAppIsInBackground(Context context) {
//        boolean isInBackground = true;
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
//            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
//            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
//                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                    for (String activeProcess : processInfo.pkgList) {
//                        if (activeProcess.equals(context.getPackageName())) {
//                            isInBackground = false;
//                        }
//                    }
//                }
//            }
//        } else {
//            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
//            ComponentName componentInfo = taskInfo.get(0).topActivity;
//            if (componentInfo.getPackageName().equals(context.getPackageName())) {
//                isInBackground = false;
//            }
//        }
//
//        return isInBackground;
//    }

    public static DisplayMetrics getDisplayMetrics(AppCompatActivity context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    //7:22
    public static int getHieghtInSixteenNine(AppCompatActivity context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        float calculatedHieght = (float) ((0.5625) * width);
        return Math.round(calculatedHieght);
    }

    public static String numberCalculation(double number) {
        String numberString = "";
        if (Math.abs(number / 1000000) > 1) {
//            numberString = (number / 1000000);
            numberString = new DecimalFormat("##.##").format((number / 1000000)) + "m";
//            numberString = (number / 1000000) + "m";

        } else if (Math.abs(number / 1000) > 1) {
//            numberString = (number / 1000) + "k";
            numberString = new DecimalFormat("##.##").format((number / 1000)) + "k";


        } else {
            numberString = "" + number;

        }
        return numberString;
    }

    public static void launchMarket(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            context.startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
        }
    }

    /**
     * this method is used to set image to the textview text
     *
     * @param imageResId image to be added
     * @param text       text to which image is added
     * @return final string with image
     */
    private SpannableStringBuilder getImageSpan(Context context, int imageResId, String text) {
        text = "  " + text;
        SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        ssb.setSpan(new ImageSpan(context, imageResId), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return ssb;
    }

    public static String getStateShortName(String state) {
        Map<String, String> states = new HashMap<String, String>();
        states.put("Alabama","AL");
        states.put("Alaska","AK");
        states.put("Alberta","AB");
        states.put("American Samoa","AS");
        states.put("Arizona","AZ");
        states.put("Arkansas","AR");
        states.put("Armed Forces (AE)","AE");
        states.put("Armed Forces Americas","AA");
        states.put("Armed Forces Pacific","AP");
        states.put("British Columbia","BC");
        states.put("California","CA");
        states.put("Colorado","CO");
        states.put("Connecticut","CT");
        states.put("Delaware","DE");
        states.put("District Of Columbia","DC");
        states.put("Florida","FL");
        states.put("Georgia","GA");
        states.put("Guam","GU");
        states.put("Hawaii","HI");
        states.put("Idaho","ID");
        states.put("Illinois","IL");
        states.put("Indiana","IN");
        states.put("Iowa","IA");
        states.put("Kansas","KS");
        states.put("Kentucky","KY");
        states.put("Louisiana","LA");
        states.put("Maine","ME");
        states.put("Manitoba","MB");
        states.put("Maryland","MD");
        states.put("Massachusetts","MA");
        states.put("Michigan","MI");
        states.put("Minnesota","MN");
        states.put("Mississippi","MS");
        states.put("Missouri","MO");
        states.put("Montana","MT");
        states.put("Nebraska","NE");
        states.put("Nevada","NV");
        states.put("New Brunswick","NB");
        states.put("New Hampshire","NH");
        states.put("New Jersey","NJ");
        states.put("New Mexico","NM");
        states.put("New York","NY");
        states.put("Newfoundland","NF");
        states.put("North Carolina","NC");
        states.put("North Dakota","ND");
        states.put("Northwest Territories","NT");
        states.put("Nova Scotia","NS");
        states.put("Nunavut","NU");
        states.put("Ohio","OH");
        states.put("Oklahoma","OK");
        states.put("Ontario","ON");
        states.put("Oregon","OR");
        states.put("Pennsylvania","PA");
        states.put("Prince Edward Island","PE");
        states.put("Puerto Rico","PR");
        states.put("Quebec","PQ");
        states.put("Rhode Island","RI");
        states.put("Saskatchewan","SK");
        states.put("South Carolina","SC");
        states.put("South Dakota","SD");
        states.put("Tennessee","TN");
        states.put("Texas","TX");
        states.put("Utah","UT");
        states.put("Vermont","VT");
        states.put("Virgin Islands","VI");
        states.put("Virginia","VA");
        states.put("Washington","WA");
        states.put("West Virginia","WV");
        states.put("Wisconsin","WI");
        states.put("Wyoming","WY");
        states.put("Yukon Territory","YT");

        return states.get(state);
    }

}


