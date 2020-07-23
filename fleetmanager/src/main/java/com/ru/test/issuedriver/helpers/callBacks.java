package com.ru.test.issuedriver.helpers;

import com.ru.test.issuedriver.data.car.car;
import com.ru.test.issuedriver.data.order;

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

    public static StartOrderPerformingInterface callback4StartOrderPerforming;
    public interface  StartOrderPerformingInterface {
        void callback(boolean success);
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

    // список заказов с изменениями загружен
    public static orderListChangedInterface callback4orderListChangedInterface;
    public interface  orderListChangedInterface {
        void callback();
    }

     // список заказов с изменениями загружен
    public static autoSearchCompleteInterface callback4autoSearchComplete;
    public interface  autoSearchCompleteInterface {
        void callback(boolean finded, car obj);
    }
}


