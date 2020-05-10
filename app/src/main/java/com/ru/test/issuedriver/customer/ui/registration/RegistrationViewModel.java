package com.ru.test.issuedriver.customer.ui.registration;

import com.ru.test.issuedriver.data.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegistrationViewModel extends ViewModel {
    public MutableLiveData<user> currentUser;

    public RegistrationViewModel() {
        currentUser = new MutableLiveData<>();
//        mText.setValue("This is home fragment");
    }

    public LiveData<user> getCurrentUser() {
        return currentUser;
    }

}