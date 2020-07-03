package com.ru.test.issuedriver.data;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import org.joda.time.DateTime;

import java.util.Date;

public class order implements Parcelable {
    public String data;
    public String from;
    public String to;
    public String purpose;
    public String comment;
    public String customer_uuid;
    public String customer_email;
    public String customer_fio;
    public String customer_phone;
    public String customer_photo;
    public String customer_token;
    public String performer_uuid;
    public String performer_email;
    public String performer_fio;
    public String performer_phone;
    public String performer_photo;
    public String performer_token;
    public String car;
    public String car_number;
    public String customer_staff;

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
    ///  время активации заказа - за 30 минут до "order_timestamp" - Времени подачи машины - задается Инженером
    public Timestamp order_active_timestamp;
    @Exclude
    public Date order_active_time;

    public String spent_time;
    public String distance;
    public String fuel;

    public String id;
    /// 0 - норм, 1 - отменен
    public int state;
    public String cancel_reason;

    public int start_distance;
    public int end_distance;

    public GeoPoint from_position;
    public GeoPoint to_position;

    @Exclude
    public Location curr_position;


    public order() {}

    public order(Date time, String from, String to, String purpose, String comment,
                 String customer_uuid, String customer_fio, String customer_phone, String customer_email,
                 String performer_uuid, String performer_fio, String performer_phone, String performer_email,
                 String car, String car_number) {
        this.id = "";
        this.order_timestamp = new Timestamp(time);
        this.from = from;
        this.to = to;
        this.purpose = purpose;
        this.comment = comment;
        this.customer_uuid = customer_uuid;
        this.customer_fio = customer_fio;
        this.customer_phone = customer_phone;
        this.customer_token = "";
        this.customer_email = customer_email;
        this.performer_uuid = performer_uuid;
        this.performer_fio = performer_fio;
        this.performer_phone = performer_phone;
        this.performer_email = performer_email;
        this.performer_token = "";
        this.accept = false;
        this.completed = false;
        this.is_notify = false;
        this.car = car;
        this.car_number = car_number;
        this.state = 0;
        this.cancel_reason = "";

        start_distance = 0;
        end_distance = 0;

        setOrderActiveTime();
    }

    protected order(Parcel in) {
        data = in.readString();
        from = in.readString();
        to = in.readString();
        purpose = in.readString();
        comment = in.readString();
        customer_uuid = in.readString();
        customer_email = in.readString();
        customer_fio = in.readString();
        customer_phone = in.readString();
        customer_photo = in.readString();
        customer_token = in.readString();
        performer_uuid = in.readString();
        performer_email = in.readString();
        performer_fio = in.readString();
        performer_phone = in.readString();
        performer_photo = in.readString();
        performer_token = in.readString();
        car = in.readString();
        car_number = in.readString();
        customer_staff = in.readString();
        accept = in.readByte() != 0;
        completed = in.readByte() != 0;
        is_notify = in.readByte() != 0;
        order_timestamp = in.readParcelable(Timestamp.class.getClassLoader());
        accept_timestamp = in.readParcelable(Timestamp.class.getClassLoader());
        start_timestamp = in.readParcelable(Timestamp.class.getClassLoader());
        end_timestamp = in.readParcelable(Timestamp.class.getClassLoader());
        order_active_timestamp = in.readParcelable(Timestamp.class.getClassLoader());
        spent_time = in.readString();
        distance = in.readString();
        fuel = in.readString();
        id = in.readString();
        state = in.readInt();
        cancel_reason = in.readString();
        curr_position = in.readParcelable(Location.class.getClassLoader());
        start_distance = in.readInt();
        end_distance = in.readInt();
    }

    public static final Creator<order> CREATOR = new Creator<order>() {
        @Override
        public order createFromParcel(Parcel in) {
            return new order(in);
        }

        @Override
        public order[] newArray(int size) {
            return new order[size];
        }
    };

    private void setOrderActiveTime() {
        DateTime jtime = new DateTime(this.order_timestamp.toDate());
        jtime = jtime.minusMinutes(30);
        this.order_active_timestamp = new Timestamp(jtime.toDate());
    }

    public void setTime(Date toDate) {
        this.order_timestamp = new Timestamp(toDate);
        setOrderActiveTime();
    }

    public void setFrom_position(String name, LatLng location) {
        this.from = name;
        this.from_position = new GeoPoint(location.latitude, location.longitude);
    }
    public void setTo_position(String name, LatLng location) {
        this.to = name;
        this.to_position = new GeoPoint(location.latitude, location.longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(data);
        dest.writeString(from);
        dest.writeString(to);
        dest.writeString(purpose);
        dest.writeString(comment);
        dest.writeString(customer_uuid);
        dest.writeString(customer_email);
        dest.writeString(customer_fio);
        dest.writeString(customer_phone);
        dest.writeString(customer_photo);
        dest.writeString(customer_token);
        dest.writeString(performer_uuid);
        dest.writeString(performer_email);
        dest.writeString(performer_fio);
        dest.writeString(performer_phone);
        dest.writeString(performer_photo);
        dest.writeString(performer_token);
        dest.writeString(car);
        dest.writeString(car_number);
        dest.writeString(customer_staff);
        dest.writeByte((byte) (accept ? 1 : 0));
        dest.writeByte((byte) (completed ? 1 : 0));
        dest.writeByte((byte) (is_notify ? 1 : 0));
        dest.writeParcelable(order_timestamp, flags);
        dest.writeParcelable(accept_timestamp, flags);
        dest.writeParcelable(start_timestamp, flags);
        dest.writeParcelable(end_timestamp, flags);
        dest.writeParcelable(order_active_timestamp, flags);
        dest.writeString(spent_time);
        dest.writeString(distance);
        dest.writeString(fuel);
        dest.writeString(id);
        dest.writeInt(state);
        dest.writeString(cancel_reason);
        dest.writeParcelable(curr_position, flags);
        dest.writeInt(start_distance);
        dest.writeInt(end_distance);
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