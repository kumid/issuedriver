package com.ru.test.issuedriver.taxi.rider;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.ru.test.issuedriver.taxi.helpers.fsm.sender;

import androidx.annotation.NonNull;

public class Common {
    public static final String driver_tbl="Drivers";
    public static final String user_driver_tbl="DriversInformation";
    public static final String history_rider = "RiderHistory";
    public static final String user_rider_tbl="RidersInformation";
    public static final String pickup_request_tbl="PickupRequest";
    public static final String CHANNEL_ID_ARRIVED="ARRIVED";
    public static String token_tbl="Tokens";
    public static String rate_detail_tbl="RateDetails";
    public static final int PICK_IMAGE_REQUEST = 9999;

    public static String userID;

    public static boolean driverFound=false;
    public static String driverID="";
    public static LatLng currenLocation;
    public static boolean isDirectionEnabled = false;


    private static double baseFare=2.55;
    private static double timeRate=0.35;
    private static double distanceRate=1.75;

    public static double getPrice(double km, int min){
        return (baseFare+(timeRate*min)+(distanceRate*km));
    }

    public static void sendRequestToDriver(final String driverID,  final Context context, final LatLng lastLocation) {
        DatabaseReference tokens= FirebaseDatabase.getInstance().getReference(Common.token_tbl);

        tokens.orderByKey().equalTo(driverID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot:dataSnapshot.getChildren()){
                    Token token=postSnapShot.getValue(Token.class);
                    Pickup pickup=new Pickup();
                    pickup.setLastLocation(lastLocation);
                    pickup.setID(userID);
                    pickup.setToken(token);
                    String json_pickup=new Gson().toJson(pickup);

                    String riderToken= FirebaseInstanceId.getInstance().getToken();

//                    sender.send(token.getToken(), json_pickup);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
