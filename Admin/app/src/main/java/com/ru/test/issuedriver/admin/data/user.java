package com.ru.test.issuedriver.admin.data;

import com.google.firebase.firestore.GeoPoint;

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
    public boolean is_busy;

    public user() {}

    public user(String fio, String staff, String email, String corp, String automodel, String autovin, String autonumber, String tel, boolean is_performer) {
        this.fio = fio;
        this.staff = staff;
        this.email = email;
        this.corp = corp;
        this.automodel = automodel;
        this.autovin = autovin;
        this.autonumber = autonumber;
        this.tel = tel;
        this.accept = false;
        this.is_busy = false;
        this.is_performer = is_performer;
    }
}
