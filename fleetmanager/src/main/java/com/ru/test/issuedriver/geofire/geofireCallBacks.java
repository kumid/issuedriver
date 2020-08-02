package com.ru.test.issuedriver.geofire;

import com.firebase.geofire.GeoLocation;
import com.ru.test.issuedriver.data.user;

// изменение состояния водителя
public class geofireCallBacks {


    // Получили геоданные по 1 машине в радиусе
    public static geofireItemRecieveInterface callback4geofireItemRecieve;
    public interface  geofireItemRecieveInterface {
        void callback(String key, GeoLocation location);
    }

    // машина опкунула  радиус действия
    public static geofireItemExitRecieveInterface callback4geofireItemExitRecieve;
    public interface  geofireItemExitRecieveInterface {
        void callback(String key);
    }

    // Получили новые геоданные по радиусу
    public static geofireFinishRecieveInterface callback4geofireFinishRecieve;
    public interface  geofireFinishRecieveInterface {
        void callback();
    }

    public static AvaibleUserAddInterface callback4AvaibleUserAdd;
    public interface AvaibleUserAddInterface {
        void callback(user addedUser);
    }

    public static UserChangeStateInterface callback4UserChangeStateInterface;
    public interface UserChangeStateInterface {
        void callback(user changedUser);
    }
}


