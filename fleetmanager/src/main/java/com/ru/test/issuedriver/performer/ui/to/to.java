package com.ru.test.issuedriver.performer.ui.to;

import com.google.firebase.Timestamp;

class to  {
    public String owner_fio;
    public String owner_email;
    public String owner_photo;
    public String reciever_id;
    public Timestamp open_timestamp;
    public Timestamp close_timestamp;
    public String message;
    public String phone;
    public boolean accept;
    public to(String fio, String ownerEmail, String ownerPhoto, String msg, String phone) {
        this.owner_fio = fio;
        this.owner_email = ownerEmail;
        this.owner_photo = ownerPhoto;
        this.open_timestamp = Timestamp.now();
        this.message = msg;
        this.phone = phone;
        accept = false;
    }
}
