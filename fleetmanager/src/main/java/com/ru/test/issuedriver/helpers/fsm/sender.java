package com.ru.test.issuedriver.helpers.fsm;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ru.test.issuedriver.data.Token;
import com.ru.test.issuedriver.data.order;

import java.util.EventListener;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ru.test.issuedriver.helpers.MyFirebaseMessagingService.token_tbl;

public class sender {
    public static void send(String reciever){
        JsonObject payload = buildNotificationPayload(reciever);
        try {
        // send notification to receiver ID
        ApiClient.getApiService().sendNotification(payload).enqueue(
                new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
//                            Toast.makeText(MainActivity.this, "Notification send successful",
//                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });
        } catch (Exception ex){
            Log.e("ErrorLog", ex.getMessage());
        }

    }


    private static JsonObject buildNotificationPayload(String reciever) {
        // compose notification json payload
        JsonObject payload = new JsonObject();
        //payload.addProperty("to", "ebw-X8q_rE0:APA91bG_BLdQ1Zk49JobVMzuNzDoP8KATAC9oXhdR0awrQzwL0EpRP59e9GlBgENlSFeeWtX-0C4TQiuToP7J0BYMyo_UtSZvycTsmIaXUkDu0fjJ2FBfm1J_r4pZB7uk2BrvayuZAHr"); //receiverFdmId.getText().toString());
        //payload.addProperty("to", "d-I4PeNk0Mg:APA91bGELE6M76NMIRfyrrBO2FmPPdvtP-SFhjAzKhcjzMhm85XgK1ztjRRIoG1UoyONfIN1QtNTX9s15hWf4PEwLxlLjyGCkGhFZDZanZdTJwmSDW51WRh1vYteofVtwcxp-dX9SUpd"); //receiverFdmId.getText().toString());

        payload.addProperty("to", "/topics/toall");
        // compose data payload here
        JsonObject data = new JsonObject();
        data.addProperty("title", "service");
        data.addProperty("message", reciever);
        data.addProperty("0", "0");
        // add data payload
        payload.add("data", data);
        return payload;
    }

    public static void send(String reciever, String title, String body){
        JsonObject payload = buildNotificationPayload(reciever, title, body);
        // send notification to receiver ID
        ApiClient.getApiService().sendNotification(payload).enqueue(
                new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
//                            Toast.makeText(MainActivity.this, "Notification send successful",
//                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });
    }

    private static JsonObject buildNotificationPayload(String reciever, String title, String body) {
        JsonObject payload = new JsonObject();

        payload.addProperty("to", reciever);
        // compose data payload here
        JsonObject data = new JsonObject();
        data.addProperty("title", title);
        data.addProperty("message", body);
        data.addProperty("0", "0");
        // add data payload
        payload.add("data", data);
        return payload;
    }

    // push #1 - to performer, for accept order
    public static void send(order currentOrder, orderStateType orderType) {
        Gson gson = new Gson();
        String json = gson.toJson(currentOrder);
        String uuid = "", title = "";
        switch (orderType){
            case new_order:
                title = "new_order";
                uuid = currentOrder.performer_uuid;
                break;
            case accepted_order:
                title = "accepted_order";
                uuid = currentOrder.customer_uuid;
                break;
            case performing_order:
                title = "performing_order";
                uuid = currentOrder.customer_uuid;
                break;
            case complete_order:
                title = "complete_order";
                uuid = currentOrder.customer_uuid;
                break;
            case cancel_order_from_customer:
                title = "cancel_order_from_customer";
                uuid = currentOrder.performer_uuid;
                break;
            case cancel_order_from_performer:
                title = "cancel_order_from_performer";
                uuid = currentOrder.customer_uuid;
                break;
            case msg_from_customer:
                title = "msg_from_customer";
                uuid = currentOrder.performer_uuid;
                break;
            case msg_from_performer:
                title = "msg_from_performer";
                uuid = currentOrder.customer_uuid;
                break;
        }

        updateTokenAndSend(uuid, title, json);
    }

    private static void updateTokenAndSend(String uuid, String title, String json) {
        FirebaseDatabase dbToken=FirebaseDatabase.getInstance();
        DatabaseReference tokens = dbToken.getReference(token_tbl);
        tokens.child(uuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String token = dataSnapshot.getValue(String.class);
                send(token, title, json);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static enum orderStateType {
        new_order,
        accepted_order,
        performing_order,
        complete_order,
        cancel_order_from_customer,
        cancel_order_from_performer,
        msg_from_customer,
        msg_from_performer
    }
}
