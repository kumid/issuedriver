package com.ru.test.issuedriver.helpers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.SplashScreen;
import com.ru.test.issuedriver.data.Token;
import com.ru.test.issuedriver.data.order;
import com.ru.test.issuedriver.data.user;
import com.ru.test.issuedriver.helpers.fsm.NotificationActivity;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static int counter = 0;
    public static String token_tbl="Tokens";
    @Override
    public void onNewToken(String s) {

        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference(token_tbl);

        Token token = new Token(s);
        mysettings.Init(getApplicationContext());
        mysettings.SetFCMToken(token);

        user curr = mysettings.GetUser();

        if (curr != null)
            firestoreHelper.setUserToken(curr.UUID, mysettings.GetFCMToken().getToken(), true);

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

        if(remoteMessage.getData().size() > 0) {
            String title = remoteMessage.getData().get("title");
            switch (title) {
                case "new_order":
                    showNotification4Order(remoteMessage.getData().get("message"), "Новая заявка", "Поступила новая заявка", 1);
                    break;
                case "accepted_order":
                    showNotification4Order(remoteMessage.getData().get("message"), "Статус заявки изменен", "Заявка принята водителем", 2);
                    break;
                case "performing_order":
                    showNotification4Order(remoteMessage.getData().get("message"), "Статус заявки изменен", "Поездка начата", 3);
                    break;
                case "complete_order":
                    showNotification4Order(remoteMessage.getData().get("message"), "Статус заявки изменен", "Поездка успешно завершена", 4);
                    break;
                case "cancel_order_from_customer":
                    showNotification4Order(remoteMessage.getData().get("message"), "Статус заявки изменен", "Поездка отменена заказчиком", 5);
                    break;
                case "cancel_order_from_performer":
                    showNotification4Order(remoteMessage.getData().get("message"), "Статус заявки изменен", "Поездка отменена водителем", 6);
                    break;
                case "msg_from_customer":
                    showNotification4Order(remoteMessage.getData().get("message"), "Новое сообщение", "Заказчик отправил Вам сообщение", 7);
                    break;
                case "msg_from_performer":
                    showNotification4Order(remoteMessage.getData().get("message"), "Новое сообщение", "Водитель отправил Вам сообщение", 8);
                    break;
                default:
                    showNotification(title, remoteMessage.getData().get("message"));
                    break;
            }
        }
        if(remoteMessage.getNotification()!=null){
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    private void showNotification4Order(String message, String title, String body, int nitifiMode) {
        Gson gson = new Gson();
        order newOrder = gson.fromJson(message, order.class);

        notify(title, body, nitifiMode);
    }

    private void notify(String title, String message, int notifiMode) {
        counter++;
        PendingIntent dismissIntent = NotificationActivity.getDismissIntent(counter, getApplicationContext());

        Intent intent = new Intent(this, SplashScreen.class);
        intent.putExtra("msg_mode", notifiMode);

        String channel_id = "fleet_channel";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT); // .FLAG_ONE_SHOT);
        PendingIntent notifyPIntent =
                PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), 0);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel_id)
                .setSmallIcon(R.drawable.logo_small)
                .setSound(uri)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setShowWhen(true)
                .setWhen(System.currentTimeMillis())
                .setUsesChronometer(true)
                .setOnlyAlertOnce(true)
//                .setContentIntent(notifyPIntent);
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder = builder.setContent(getCustomDesign(title, message));
        } else {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo_small, options);
            builder = builder.setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.logo_small)
                    .setLargeIcon(bitmap);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "fleet_app", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setSound(uri, null);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        builder.setAutoCancel(true);
        Notification notification = builder.build();

        notificationManager.notify(counter, notification);
    }

    private RemoteViews getCustomDesign(String title,String message){
        RemoteViews remoteViews=new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.title, title);
        remoteViews.setTextViewText(R.id.message, message);
        remoteViews.setImageViewResource(R.id.icon,R.drawable.logo_small);
        remoteViews.setChronometer(R.id.myChronometere, SystemClock.elapsedRealtime(),
                null, true); //pausing
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

        notify(title, message, 0);
        }

}