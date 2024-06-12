package com.zoramedic.zoramedicapp.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.zoramedic.zoramedicapp.R;
import com.zoramedic.zoramedicapp.data.UserMe;
import com.zoramedic.zoramedicapp.databinding.ActivityMainBinding;
import com.zoramedic.zoramedicapp.viewmodel.LoginViewModel;

public class MainActivity extends AppCompatActivity {
    //testCommit
    private ActivityMainBinding binding;

    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        Animation alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.main_anim);
        alphaAnimation.setRepeatMode(1);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //ovo si ubacio ovde da bi prvo napravio UserMe pre nego sto ode u HomeActivity
//                if (isOnline()) {
//                    if (loginViewModel.getUserLiveData().getValue() != null) {
//                        UserMe.getInstance().setUserFirebaseID(loginViewModel.getUserLiveData().getValue().getUid());
//                        loginViewModel.setUserClass(loginViewModel.getUserLiveData().getValue().getUid());
//                    }
//                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isOnline()) {
                    if (loginViewModel.getUserLiveData().getValue() != null) {
                        UserMe.getInstance().setUserFirebaseID(loginViewModel.getUserLiveData().getValue().getUid());
                        loginViewModel.setUserClassAndIntent(loginViewModel.getUserLiveData().getValue().getUid(), MainActivity.this, false);
                        Toast.makeText(MainActivity.this, R.string.logged_in, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, R.string.not_logged_in, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        binding.animationStart.startAnimation(alphaAnimation);
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}