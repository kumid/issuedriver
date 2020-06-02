package com.ru.test.issuedriver.taxi.data;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import org.joda.time.DateTime;

import java.util.Date;

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
    /// Время подачи машины - задается Инженером
    public Timestamp order_timestamp;
    /// Время принятия заказа - задается Водителем
    public Timestamp accept_timestamp;
    /// Время начала выполнения заказа - задается Водителем
    public Timestamp start_timestamp;
    /// Время окончания выполнения заказа - задается Водителем
    public Timestamp end_timestamp;
    /// Время подачи машины - задается Инженером
    public Timestamp order_active_timestamp;
    @Exclude
    public Date order_active_time;

    public String spent_time;
    public String distance;
    public String fuel;

    public String id;

    public order() {}

    public order(Date time, String from, String to, String purpose, String comment, String customer_fio, String customer_phone, String customer_email, String performer_fio, String performer_phone, String performer_email, String car, String car_number) {
        this.id = "";
        this.order_timestamp = new Timestamp(time);
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


        DateTime jtime = new DateTime(time);
        jtime = jtime.minusMinutes(30);
        this.order_active_timestamp = new Timestamp(jtime.toDate());

    }

//    public order(String data, String from, String to, String purpose, String comment,
//                 String customer_fio, String customer_phone, String customer_email,
//                 String performer_fio, String performer_phone, String performer_email,
//                 String car, String car_number
//                  ) {
//        this.id = "";
//        this.data = data;
//        this.from = from;
//        this.to = to;
//        this.purpose = purpose;
//        this.comment = comment;
//        this.customer_fio = customer_fio;
//        this.customer_phone = customer_phone;
//        this.customer_email = customer_email;
//        this.performer_fio = performer_fio;
//        this.performer_phone = performer_phone;
//        this.performer_email = performer_email;
//        this.accept = false;
//        this.completed = false;
//        this.is_notify = false;
//        this.car = car;
//        this.car_number = car_number;
//
//    }
}