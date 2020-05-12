package com.ru.test.issuedriver.performer.ui.registration;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ru.test.issuedriver.data.user;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegistrationViewModel extends ViewModel {
    FirebaseFirestore db;

    public MutableLiveData<user> currentUser;

    public RegistrationViewModel() {
        currentUser = new MutableLiveData<>();
        db = FirebaseFirestore.getInstance();
//        mText.setValue("This is home fragment");
    }

    public LiveData<user> getCurrentUser() {
        return currentUser;
    }

    public void getUserFromServer(String email) {
        if (currentUser.getValue() == null) {
            db.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<user> questionsList = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    user curr = document.toObject(user.class);
                                    questionsList.add(curr);
                                    currentUser.setValue(curr);
                                    Log.d("TAG", document.getId() + " => " + document.getData());
                                }
                            } else {
                                Log.w("TAG", "Error getting documents.", task.getException());
                            }
                        }
                    });
        }
    }
//    public static getUserComplete getUserCompleteCalback;
//    public interface getUserComplete{
//        void callback(boolean pass, user current);

}