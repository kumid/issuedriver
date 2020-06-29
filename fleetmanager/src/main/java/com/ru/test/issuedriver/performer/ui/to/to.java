package com.ru.test.issuedriver.performer.ui.to;

class to {
    public String message;
    public String phone;
    public boolean accept;
    public to(String msg, String phone) {
        this.message = msg;
        this.phone = phone;
        accept = false;
    }
}
