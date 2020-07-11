package com.ru.test.issuedriver.data;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import java.util.UUID;

public class user {
    public String fio;
    public String staff;
    public String email;
    public String corp;
    public String automarka;
    public String automodel;
    public String autovin;
    public String autonumber;
    public String tel;
    public boolean accept;
    public GeoPoint position;
    public boolean is_performer;

    public String osago_number;
    public String osago_start_date;
    public String osago_expire_date;
    public String texservice_start_date;
    public String texservice_expire_date;

    public Timestamp last_geo_time;

    public String UUID;
    /// 0 - свободен, 1 - занят, 2 - ремонт
    public int state;
    public String fcmToken;
    public String photoPath;
    @Exclude
    public boolean is_busy() { return state != 0; }
    public user() {}

    public user(String fio, String staff, String email, String corp, String automarka, String automodel, String autovin, String autonumber, String tel, boolean is_performer, boolean isAccepted,
                String osago_number, String osago_start_date, String osago_expire_date, String  texservice_start_date, String texservice_expire_date) {
        this.fio = fio;
        this.staff = staff;
        this.email = email;
        this.corp = corp;
        this.automarka = automarka;
        this.automodel = automodel;
        this.autovin = autovin;
        this.autonumber = autonumber;
        this.tel = tel;
        this.accept = false;
        this.is_performer = is_performer;
        this.accept = isAccepted;

        UUID = java.util.UUID.randomUUID().toString();
        state = 0;
        fcmToken = "";
        photoPath = "";

        this.osago_number = osago_number;
        this.osago_start_date = osago_start_date;
        this.osago_expire_date = osago_expire_date;
        this.texservice_start_date = texservice_start_date;
        this.texservice_expire_date = texservice_expire_date;
    }
}
