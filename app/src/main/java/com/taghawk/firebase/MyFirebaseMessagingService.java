package com.taghawk.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.taghawk.R;
import com.taghawk.TagHawkApplication;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.data.preferences.PreferenceManager;
import com.taghawk.ui.splash.SplashActivity;
import com.taghawk.util.AppUtils;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    /**
     * This variable is used to have a unique id of the every notification
     */
    private int mUniqueId;
    /**
     * This variable is used to set the title of the notification
     */
    private String mTitle;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        PreferenceManager.getInstance(this).putString(AppConstants.PreferenceConstants.DEVICE_TOKEN, s);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //handling notification
        sendNotification(remoteMessage.getData());
    }

    //method to get notificationManager
    public NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    //This method is only generating push notification
    private void sendNotification(Map<String, String> data) {
        mUniqueId = 0;
        String id = "channel" + mUniqueId;
        try {
            //Generating unique id
            generateUniqueId();
            if (DataManager.getInstance().getUserDetails().isMute()) {
                return;
            }
            Log.d("PUSH_DATA", data.toString());
            Log.d("PUSH_DATA_TYPE", data.get("type"));
            if (data.get("type").equalsIgnoreCase(AppConstants.FIREBASE.PUSH_TYPE_MESSAGE)) {
                id = getString(R.string.chat);
                if (DataManager.getInstance().getAccessToken() == null || DataManager.getInstance().getAccessToken().equalsIgnoreCase(""))
                    return;
                else if (DataManager.getInstance().getUserDetails().getUserId() == null || DataManager.getInstance().getUserDetails().getUserId().equalsIgnoreCase("") || !DataManager.getInstance().getUserDetails().getUserId().equalsIgnoreCase(data.get("userId")))
                    return;
            } else if (data.get("type").equalsIgnoreCase(AppConstants.PAYMENT) || data.get("type").equalsIgnoreCase(AppConstants.NOTIFICATION_ACTION.PRODUCT_SOLD)) {
                if (AppUtils.isForground()) {
                    Intent intent = new Intent(AppConstants.BROAD_CAST_PAYMENT_ACTION);
                    // You can also include some extra data.
                    intent.putExtra(AppConstants.KEY_CONSTENT.SELLER_ID, data.get(AppConstants.KEY_CONSTENT.SELLER_ID));
                    intent.putExtra(AppConstants.KEY_CONSTENT.USER_ID, data.get(AppConstants.KEY_CONSTENT.USER_ID));
                    intent.putExtra(AppConstants.KEY_CONSTENT.PRODUCT_ID, data.get(AppConstants.NOTIFICATION_ACTION.ENTITY_ID));
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                }
            } else if (data.get("type").equalsIgnoreCase(AppConstants.TAG_JOINED_ACCEPTED) && AppUtils.isForground()) {
                Intent intent = new Intent(AppConstants.BROAD_CAST_TAG_JOINED_ACTION);
                intent.putExtra(AppConstants.KEY_CONSTENT.TAG_ID, data.get(AppConstants.NOTIFICATION_ACTION.ENTITY_ID));
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
            Intent intent = new Intent(this, SplashActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(AppConstants.NOTIFICATION_TYPE, data.get("type"));
            intent.putExtra(AppConstants.BUNDLE_DATA, data.toString());
            intent.putExtra(AppConstants.NOTIFICATION_ACTION.ENTITY_ID, data.get(AppConstants.NOTIFICATION_ACTION.ENTITY_ID));
            //Set title by getting from the push notification
            if (data.get("title") != null)
                mTitle = data.get("title");

            PendingIntent pendingIntent = PendingIntent.getActivity(this, mUniqueId, intent, PendingIntent.FLAG_ONE_SHOT);

            NotificationManager notificationManager = getNotificationManager();
            if (AppUtils.isForground()) {
                if (data.get("type").equalsIgnoreCase(AppConstants.FIREBASE.PUSH_TYPE_MESSAGE) && TagHawkApplication.getInstance().isMessageTabVisible())
                    return;
            }
            if (notificationManager != null) {
                //Check for oreo (Making notification channel
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel mChannel = new NotificationChannel(id, mTitle, importance);
                    mChannel.setDescription(getString(R.string.app_name));
                    if (id.equalsIgnoreCase(getString(R.string.chat)))
                        mChannel.setShowBadge(true);
                    else
                        mChannel.setShowBadge(false);
                    mChannel.enableLights(true);
                    mChannel.setLightColor(ContextCompat.getColor(this, R.color.colorPrimary));
                    mChannel.enableVibration(true);
                    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    notificationManager.createNotificationChannel(mChannel);
                }

                //Set Notification for other devices
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, id)
                                .setSmallIcon(getNotificationIcon())
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(mTitle))
                                .setContentText(mTitle)
                                .setNumber(data.get("badge") != null ? Integer.parseInt(data.get("badge")) : 0)
                                .setContentTitle(getString(R.string.app_name))
                                .setAutoCancel(true).setChannelId(id)
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setContentIntent(pendingIntent);

                // Set a message count to associate with this notification in the long-press menu.
                // Create a notification and set a number to associate with it.

                Notification notification = notificationBuilder.build();

                notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                notification.flags |=
                        Notification.FLAG_AUTO_CANCEL; //Do not clear  the notification
                notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
                notification.defaults |= Notification.DEFAULT_VIBRATE;//Vibration

                notificationManager.notify(mUniqueId, notification);

                //Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                //NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                //notificationManager.notify(mUniqueId, notificationBuilder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * This method is used to generate unique id for the notifications
     */
    private void generateUniqueId() {
        mUniqueId = (int) System.currentTimeMillis();
    }

    /**
     * This method is used to set the notification icon on the push notifications for marshmallow
     * or oreo devices
     *
     * @return drawable on the basis of the type of device
     */
    private int getNotificationIcon() {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_notification_icon : R.drawable.ic_launcher;
    }
}
