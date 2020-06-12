package com.ru.test.issuedriver.taxi.data;

public class user_position {
    public double latitude;
    public double longitude;
    public long last_geo_time;

    public user_position() { }

    public user_position(double latitude, double longitude, long last_geo_time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.last_geo_time = last_geo_time;
    }
}
