package com.ru.test.issuedriver.data;

import java.sql.Timestamp;

public class order {
    public String data;
    public String from;
    public String to;
    public String purpose;
    public String comment;
    public String customer_email;
    public String customer_fio;
    public String customer_phone;
    public String performer_email;
    public String performer_fio;
    public String performer_phone;
    public String car;
    public String car_number;

    public boolean accept;
    public boolean completed;
    public boolean is_notify;
    public Timestamp timestamp;

    public String id;

    public order() {}

    public order(String data, String from, String to, String purpose, String comment,
                 String customer_fio, String customer_phone, String customer_email,
                 String performer_fio, String performer_phone, String performer_email,
                 String car, String car_number
                  ) {
        this.id = "";
        this.data = data;
        this.from = from;
        this.to = to;
        this.purpose = purpose;
        this.comment = comment;
        this.customer_fio = customer_fio;
        this.customer_phone = customer_phone;
        this.customer_email = customer_email;
        this.performer_fio = performer_fio;
        this.performer_phone = performer_phone;
        this.performer_email = performer_email;
        this.accept = false;
        this.completed = false;
        this.is_notify = false;
        this.car = car;
        this.car_number = car_number;
//        this.timestamp = timestamp;
    }
}