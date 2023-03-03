package com.zoramedic.zoramedicapp.view;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.zoramedic.zoramedicapp.viewmodel.DatabaseViewModel;

public class BaseActivity extends InternetActivity {

    DatabaseViewModel databaseViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseViewModel = new ViewModelProvider(this).get(DatabaseViewModel.class);
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseViewModel.updateUserAvailability(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        databaseViewModel.updateUserAvailability(true);
    }
}
