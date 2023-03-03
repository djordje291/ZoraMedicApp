package com.zoramedic.zoramedicapp.viewmodel;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.zoramedic.zoramedicapp.model.FirebaseRepository;
import com.zoramedic.zoramedicapp.view.LoginActivity;

public class LoginViewModel extends AndroidViewModel {

    private FirebaseRepository firebaseRepository;
    private MutableLiveData<FirebaseUser> userLiveData;
    private MutableLiveData<Boolean> loggedOutLiveData;

    public LoginViewModel(@NonNull Application application) {
        super(application);

        firebaseRepository = new FirebaseRepository(application);
        userLiveData = firebaseRepository.getUserLiveData();
        loggedOutLiveData = firebaseRepository.getLoggedOutLiveData();
    }

    public void login(String email, String password, LoginActivity a) {
        firebaseRepository.login(email, password, a);
    }

    public void logout() {
        firebaseRepository.logOut();
    }

    public MutableLiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    public MutableLiveData<Boolean> getLoggedOutLiveData() {
        return loggedOutLiveData;
    }

    public void setUserClassAndIntent(String id, Activity activity, boolean b) {
        firebaseRepository.setUserClassAndIntent(id, activity, b);
    }

    public void getToken() {
        firebaseRepository.getToken();
    }
}
