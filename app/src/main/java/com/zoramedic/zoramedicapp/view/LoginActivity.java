package com.zoramedic.zoramedicapp.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.zoramedic.zoramedicapp.R;
import com.zoramedic.zoramedicapp.data.UserMe;
import com.zoramedic.zoramedicapp.databinding.ActivityLoginBinding;
import com.zoramedic.zoramedicapp.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel =  new ViewModelProvider(this).get(LoginViewModel.class);

        loginViewModel.getUserLiveData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser != null) {
                    UserMe.getInstance().setUserFirebaseID(firebaseUser.getUid());
                    loginViewModel.setUserClassAndIntent(firebaseUser.getUid(), LoginActivity.this, true);
                    Toast.makeText(LoginActivity.this, R.string.logged_in, Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.loginEmail.getText().toString().trim().isEmpty()
                        && !binding.loginPassword.getText().toString().trim().isEmpty()) {

                    loading(true);
                    loginViewModel.login(binding.loginEmail.getText().toString().trim(),
                            binding.loginPassword.getText().toString().trim(), LoginActivity.this);

                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.login_warning), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void loading(boolean b) {
        if (b) {
            binding.loginButton.setVisibility(View.INVISIBLE);
            binding.loading.setVisibility(View.VISIBLE);
        } else {
            binding.loginButton.setVisibility(View.VISIBLE);
            binding.loading.setVisibility(View.INVISIBLE);
        }
    }
}