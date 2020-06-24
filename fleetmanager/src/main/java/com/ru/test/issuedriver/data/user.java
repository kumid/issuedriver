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
    public String automodel;
    public String autovin;
    public String autonumber;
    public String tel;
    public boolean accept;
    public GeoPoint position;
    public boolean is_performer;

    public Timestamp last_geo_time;

    public String UUID;
    /// 0 - свободен, 1 - занят, 2 - ремонт
    public int state;
    public String fcmToken;
    @Exclude
    public boolean is_busy() { return state != 0; }
    public user() {}

    public user(String fio, String staff, String email, String corp, String automodel, String autovin, String autonumber, String tel, boolean is_performer, boolean isAccepted) {
        this.fio = fio;
        this.staff = staff;
        this.email = email;
        this.corp = corp;
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
    }
}
