package com.ru.test.issuedriver.taxi.helpers;

import com.firebase.geofire.GeoLocation;
import com.ru.test.issuedriver.taxi.data.order;

// изменение состояния водителя
public class callBacks {
    public static userStateChanged userStateChangedCalback;

    public interface userStateChanged {
        void callback(int state);
    }

    public static orderStateChanged orderStateChangedCalback;
    public interface orderStateChanged {
        void callback(boolean success);
    }

    public static CancelOrderInterface callback4cancelOrder;
    public interface  CancelOrderInterface {
        void callback(order order);
    }

    public static DeleteOrderInterface callback4deleteOrder;
    public interface  DeleteOrderInterface {
        void callback(boolean success);
    }

    public static exitTaskInterface callback4exitTask;
    public interface  exitTaskInterface {
        void callback();
    }


    /// переход на гугл карту с навигацией
    public static goToNavigateInterface callback4goToNavigate;
    public interface  goToNavigateInterface {
        void callback(order currentOrder);
    }

    // Получили геоданные по 1 машине в радиусе
    public static geofireItemRecieveInterface callback4geofireItemRecieve;
    public interface  geofireItemRecieveInterface {
        void callback(String key, GeoLocation location);
    }

    // Получили новые геоданные по радиусу
    public static geofireFinishRecieveInterface callback4geofireFinishRecieve;
    public interface  geofireFinishRecieveInterface {
        void callback();
    }

}


