package com.ru.test.issuedriver.performer.feedback;

class feedback {
    public String message;
    public String phone;
    public boolean accept;
    public feedback(String msg, String phone) {
        this.message = msg;
        this.phone = phone;
        accept = false;
    }
}
