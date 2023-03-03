package com.zoramedic.zoramedicapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.zoramedic.zoramedicapp.R;
import com.zoramedic.zoramedicapp.data.User;
import com.zoramedic.zoramedicapp.data.UserMe;
import com.zoramedic.zoramedicapp.databinding.ActivityHomeBinding;
import com.zoramedic.zoramedicapp.view.chat.ChatActivity;
import com.zoramedic.zoramedicapp.view.notes.NotesActivity;
import com.zoramedic.zoramedicapp.view.patients.PatientsActivity;
import com.zoramedic.zoramedicapp.view.pharmacy.PharmacyActivity;
import com.zoramedic.zoramedicapp.viewmodel.DatabaseViewModel;
import com.zoramedic.zoramedicapp.viewmodel.LoginViewModel;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private LoginViewModel loginViewModel;
    private DatabaseViewModel databaseViewModel;

    private ActivityHomeBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        databaseViewModel = new ViewModelProvider(this).get(DatabaseViewModel.class);


        loginViewModel.getLoggedOutLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loggedOut) {
                if (loggedOut) {
                    Toast.makeText(HomeActivity.this, getString(R.string.user_logged_out), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

        binding.patientsHomeButton.setOnClickListener(this);
        binding.pharmacyHomeButton.setOnClickListener(this);
        binding.notesHomeButton.setOnClickListener(this);
        binding.chatHomeButton.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logoutHome) {
            loginViewModel.logout();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.patientsHomeButton:
                intent = new Intent(HomeActivity.this, PatientsActivity.class);
                startActivity(intent);
                break;
            case R.id.pharmacyHomeButton:
                intent = new Intent(HomeActivity.this, PharmacyActivity.class);
                startActivity(intent);
                break;
            case R.id.notesHomeButton:
                intent = new Intent(HomeActivity.this, NotesActivity.class);
                startActivity(intent);
                break;
            case R.id.chatHomeButton:
                intent = new Intent(HomeActivity.this, ChatActivity.class);
                startActivity(intent);
                break;
        }
    }
}
