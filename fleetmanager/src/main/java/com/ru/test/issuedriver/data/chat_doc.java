package com.ru.test.issuedriver.data;

public class chat_doc {
    public String id;

    public String member1;
    public String member1name;
    public String member1_photo;
    public String member2;
    public String member2name;
    public String member2_photo;

    public chat_doc(String customer_uuid, String customer_name, String customer_photo, String performer_uuid,  String performer_name, String performer_photo) {
        member1 = customer_uuid;
        member1name = customer_name;
        member1_photo = customer_photo;
        member2 = performer_uuid;
        member2name = performer_name;
        member2_photo = performer_photo;
    }
    public chat_doc() {}

    public String getSenderName(String sender_id) {
        if(member1.equals(sender_id))
            return member1name;
        return member2name;
    }

    public String getSenderPhoto(String sender_id) {
        if(member1.equals(sender_id))
            return member1_photo;
        return member2_photo;
    }

    public boolean getIsSenderMember1(String sender_id) {
        if(member1.equals(sender_id))
            return true;
        return false;
    }


}
