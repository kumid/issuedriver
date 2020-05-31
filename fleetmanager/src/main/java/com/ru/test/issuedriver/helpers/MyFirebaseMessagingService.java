package com.ru.test.issuedriver.helpers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.SplashScreen;
import com.ru.test.issuedriver.data.user;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import static com.ru.test.issuedriver.helpers.mysettings.APP_PREFERENCES;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static int counter = 0;
    @Override
    public void onNewToken(String s) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                           // Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        Log.e("Token","0");
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.e("My Token",token);

                        FirebaseMessaging.getInstance().subscribeToTopic("toall")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        //String msg = getString(R.string.msg_subscribed);
                                        //if (!task.isSuccessful()) {
                                        //    msg = getString(R.string.msg_subscribe_failed);
                                        //}
                                        Log.e("My Token", "subscribe to topic all");
                                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    }
                                }) .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("My Token", e.getLocalizedMessage());
                            }
                        });
                    }
                });
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

//        if (Owin.hasConnection(this)) {
//            Owin.GetCurrent().GetWaysFromServer();
//
//            Owin.callback4Ways = new ResponseCompleate() {
//                @Override
//                public void callbackCall(final boolean pass) {
//                    if (pass == true) {
//
//
//                    }
//                }
//            };
//        }


        if(remoteMessage.getData().size() >0){
            showNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("message"));
        }

        if(remoteMessage.getNotification()!=null){
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    private RemoteViews getCustomDesign(String title,String message){
        RemoteViews remoteViews=new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.title,title);
        remoteViews.setTextViewText(R.id.message,message);
        remoteViews.setImageViewResource(R.id.icon,R.drawable.logo_small);
        return remoteViews;
    }

    public void showNotification(String title,String message){
        mysettings.Init(getApplicationContext());
        if(title.equals("service")) {
            user curr = mysettings.GetUser();
            if(curr == null)
                return;

            if(message.equals(curr.email)) {
                if(curr.is_performer){
                    title = "Новая заявка";
                    message = "Поступила новая заявка";
                } else {
                   title = "Заявка подтверждена";
                    message = "Заявка принята водителем";
                }
                Log.e("Token","Внимание");
            } else {
                Log.e("Token","4");
                return;
            }
        }
        Log.e("Token","5");

        Intent intent=new Intent(this, SplashScreen.class);
        String channel_id="fleet_channel";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT); // .FLAG_ONE_SHOT);
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(getApplicationContext(),channel_id)
                .setSmallIcon(R.drawable.logo_small)
                .setSound(uri)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000,1000,1000,1000,1000})
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
            builder=builder.setContent(getCustomDesign(title,message));
        }
        else{
            builder=builder.setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.logo_small);
        }

        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(channel_id,"fleet_app", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setSound(uri,null);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(counter++, builder.build());
        }

}