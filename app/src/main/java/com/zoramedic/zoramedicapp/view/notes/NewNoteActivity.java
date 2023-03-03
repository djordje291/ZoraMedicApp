package com.zoramedic.zoramedicapp.view.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.firebase.Timestamp;
import com.zoramedic.zoramedicapp.R;
import com.zoramedic.zoramedicapp.data.Note;
import com.zoramedic.zoramedicapp.data.User;
import com.zoramedic.zoramedicapp.data.UserMe;
import com.zoramedic.zoramedicapp.databinding.ActivityNewNoteBinding;
import com.zoramedic.zoramedicapp.view.InternetActivity;
import com.zoramedic.zoramedicapp.view.MainActivity;
import com.zoramedic.zoramedicapp.viewmodel.DatabaseViewModel;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NewNoteActivity extends InternetActivity {
    //TODO: najverovatnije kad dodas 20 radnika to nece lepo izgledati testiraj

    private DatabaseViewModel databaseViewModel;
    private List<User> userList = new ArrayList<>();
    private ActivityNewNoteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        databaseViewModel = new ViewModelProvider(this).get(DatabaseViewModel.class);

        String userID = UserMe.getInstance().getUserFirebaseID();

        databaseViewModel.getUserList().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                if (!users.isEmpty()) {
                    if (!userList.isEmpty()) {
                        userList.clear();
                    }

                    for (User u : users) {
                        if (!u.getUserFirebaseID().equals(userID)) {
                            userList.add(u);
                        }
                    }
                    addChips();


                } else {
                    userList.clear();
                }
            }
        });

        binding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.titleNote.getText().toString().trim().isEmpty()) {
                    if (binding.chipGroup.getCheckedChipIds().size() != 0) {
                        Note note = new Note();
                        note.setTitle(binding.titleNote.getText().toString().trim());
                        note.setCreatorID(UserMe.getInstance().getUserFirebaseID());
                        note.setCreatorName(UserMe.getInstance().getFirstName() + " " + UserMe.getInstance().getLastName());
                        note.setDateAndTime(Timestamp.now());

                        List<User> receiverList = new ArrayList<>();
                        for (Integer i : binding.chipGroup.getCheckedChipIds()) {
                            //they start from 1, but list starts from 0
                            //ne znam sto ali ovde chipovi svaki put kad udjes samo se dodaje id broj, ne pocinje od 1
                            int index = (i - 1) % userList.size();
                            receiverList.add(userList.get(index));
                        }
                        note.setReceiverList(receiverList);
                        databaseViewModel.setNote(note);

                        closeActivity();
                    } else {
                        Toast.makeText(NewNoteActivity.this, R.string.select_people_to_notify, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(NewNoteActivity.this, R.string.fill_in_the_note, Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.allChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStateOfChips(binding.allChip.isChecked());
            }
        });
    }

    private void addChips() {
        for (User u : userList) {
            ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(this,
                    null,
                    0,
                    R.style.CustomChipStyle);
            Chip chip = new Chip(this);
            chip.setText(u.getFirstName() + " " + u.getLastName());
            chip.setChipDrawable(chipDrawable);
            chip.setTextSize(11);
            chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (!b) {
                        binding.allChip.setChecked(false);
                        chip.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_purple));
                    } else {
                        chip.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                        if (binding.chipGroup.getCheckedChipIds().size() == binding.chipGroup.getChildCount()) {
                            binding.allChip.setChecked(true);
                        }
                    }
                }
            });
            binding.chipGroup.addView(chip);

        }
        setStateOfChips(false);
    }

    private void closeActivity() {
        Intent intent = new Intent();
        intent.setClass(NewNoteActivity.this, NotesActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void setStateOfChips(boolean b) {
        if (b) {
            for (int i = 0; i < binding.chipGroup.getChildCount(); i++) {
                Chip chip = (Chip) binding.chipGroup.getChildAt(i);
                chip.setChecked(true);
            }
        } else {
            for (int i = 0; i < binding.chipGroup.getChildCount(); i++) {
                Chip chip = (Chip) binding.chipGroup.getChildAt(i);
                chip.setChecked(false);
            }
        }
    }

}