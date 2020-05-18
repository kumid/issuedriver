package com.ru.test.issuedriver.helpers.fcm;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class sender {
    public static void send(String reciever){
        JsonObject payload = buildNotificationPayload(reciever);
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
}
