package com.ru.test.issuedriver.taxi.data;

import com.google.android.gms.maps.model.LatLng;
import com.ru.test.issuedriver.taxi.rider.Token;

public class Pickup {
    LatLng lastLocation;
    String UUID;
    Token token;
    String Email;

    public Pickup() {
    }

    public Pickup(LatLng lastLocation, String ID, Token token) {
        this.lastLocation = lastLocation;
        this.UUID = ID;
        this.token = token;
    }

    public LatLng getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(LatLng lastLocation) {
        this.lastLocation = lastLocation;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
