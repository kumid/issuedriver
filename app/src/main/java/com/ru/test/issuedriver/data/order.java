package com.ru.test.issuedriver.data;

public class order {
    public String fio;
    public String data;
    public String from;
    public String to;
    public String purpose;
    public String comment;
    public String customer;
    public String performer;

    public boolean accept;
    public boolean completed;

    public order() {}

    public order(String fio, String data, String from, String to, String purpose, String comment, String customer, String performer) {
        this.fio = fio;
        this.data = data;
        this.from = from;
        this.to = to;
        this.purpose = purpose;
        this.comment = comment;
        this.customer = customer;
        this.performer = performer;
        this.accept = false;
        this.completed = false;
    }
}
