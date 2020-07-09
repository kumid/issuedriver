package com.ru.test.issuedriver.chat;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ru.test.issuedriver.data.chat_doc;
import com.ru.test.issuedriver.data.order;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChatViewModel extends ViewModel {

    private static final String TAG = "myLogs";

    FirebaseFirestore db;
    FirebaseDatabase database;

    private MutableLiveData<chat_doc> currentChat_room;
    public LiveData<chat_doc> getCurrentChat_roomLiveData(){
        return currentChat_room;
    }
    public chat_doc CurrentChat_room;
    public order CurrentOrder;

    public ChatViewModel(){
        db = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();
        currentChat_room = new MutableLiveData<>();
    }

    public void Init(order current_order) {
        initChatData(current_order);
    }

    private void initChatData(order current_order) {
        if (current_order == null)
            return;
        CurrentOrder = current_order;

        final Query collectionRef = db.collection("chat_rooms")
                .whereEqualTo("member1", current_order.customer_uuid)
                .whereEqualTo("member2", current_order.performer_uuid);
        collectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }
                for (DocumentSnapshot snapshot :
                        queryDocumentSnapshots.getDocuments()) {
                    if (snapshot != null && snapshot.exists()) {
                        CurrentChat_room = snapshot.toObject(chat_doc.class);
                        CurrentChat_room.id = snapshot.getId();
                        Log.d("TAG", "Current data: " + snapshot.getData());

                        break;
                    } else {
                        Log.d("TAG", "Current data: null");
                    }
                }

                currentChat_room.postValue(CurrentChat_room);
            }
        });
    }

    public void createChatRoom(order currentOrder){
        if(CurrentChat_room != null)
            return;

        chat_doc newChatRoom= new chat_doc(currentOrder.customer_uuid, currentOrder.customer_fio, currentOrder.customer_photo, currentOrder.performer_uuid, currentOrder.performer_fio,  currentOrder.performer_photo);
        db.collection("chat_rooms").add(newChatRoom);
    }
}