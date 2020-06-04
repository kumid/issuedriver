package com.ru.test.issuedriver.performer.helpers;

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

}


