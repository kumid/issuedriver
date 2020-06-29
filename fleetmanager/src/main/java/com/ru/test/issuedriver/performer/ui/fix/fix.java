package com.ru.test.issuedriver.performer.ui.fix;

class fix {
    public String message;
    public String phone;
    public boolean accept;
    public fix(String msg, String phone) {
        this.message = msg;
        this.phone = phone;
        accept = false;
    }
}
