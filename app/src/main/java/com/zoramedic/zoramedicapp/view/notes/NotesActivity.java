package com.zoramedic.zoramedicapp.view.notes;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.zoramedic.zoramedicapp.App;
import com.zoramedic.zoramedicapp.R;
import com.zoramedic.zoramedicapp.data.Note;
import com.zoramedic.zoramedicapp.data.User;
import com.zoramedic.zoramedicapp.data.UserMe;
import com.zoramedic.zoramedicapp.databinding.ActivityNotesBinding;
import com.zoramedic.zoramedicapp.ui.ItemTouchHelperEdit;
import com.zoramedic.zoramedicapp.ui.SwipeToDeleteAndEditCallback;
import com.zoramedic.zoramedicapp.view.InternetActivity;
import com.zoramedic.zoramedicapp.view.chat.ChatActivity;
import com.zoramedic.zoramedicapp.view.util.Sleeper;
import com.zoramedic.zoramedicapp.viewmodel.DatabaseViewModel;

import java.util.ArrayList;
import java.util.List;

public class NotesActivity extends InternetActivity implements ItemTouchHelperEdit {

    private ActivityNotesBinding binding;
    private DatabaseViewModel databaseViewModel;

    private List<Note> noteList = new ArrayList<>();

    private NotesAdapter notesAdapter;

    private UserMe userMe;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = getApplicationContext();

        loading(true);

        databaseViewModel = new ViewModelProvider(this).get(DatabaseViewModel.class);

        userMe = UserMe.getInstance();

        databaseViewModel.getNoteList().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                if (!notes.isEmpty()) {
                    if (!noteList.isEmpty()) {
                        noteList.clear();
                    }
                    binding.emptyNotes.setVisibility(View.GONE);
                    noteList.addAll(notes);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    binding.recyclerViewNotes.setLayoutManager(layoutManager);

                    notesAdapter = new NotesAdapter(getApplicationContext(), noteList, databaseViewModel, binding.conView);
                    binding.recyclerViewNotes.setAdapter(notesAdapter);

                    loading(false);
                    notesAdapter.notifyDataSetChanged();

                    ItemTouchHelper.Callback callback = new SwipeToDeleteAndEditCallback(context, notesAdapter, NotesActivity.this, noteList);
                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
                    itemTouchHelper.attachToRecyclerView(binding.recyclerViewNotes);

                    updateSeenByList();
                } else {
                    noteList.clear();
                    new Sleeper(binding.emptyNotes, binding.loading, notes, noteList).start();
                    if (binding.recyclerViewNotes.getAdapter() != null) {
                        binding.recyclerViewNotes.getAdapter().notifyDataSetChanged();
                    }
                }
            }
        });

        binding.fabNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNewNote();
            }
        });

    }

    private void notific(Note note) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            builder = new  NotificationCompat.Builder(this,App.NOTES_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_add)
                    .setContentTitle(note.getCreatorName())
                    .setContentText(note.getTitle())
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
            notificationManager.notify(1234, builder.build());
        } else {
            //<26 api
        }
    }

    private void updateSeenByList() {
        User u = convertUserMeToUser();
        boolean flag = false;
        for (Note n : noteList) {
            //mozda nece da nadje isti contains, mozda moras samo po UID da uporedis
            if (!n.getCreatorID().equals(u.getUserFirebaseID())) {
                if (n.getSeenByList() != null) {
                    for (User uu : n.getSeenByList()) {
                        if (uu.getUserFirebaseID().equals(u.getUserFirebaseID())) {
                            flag = true;
                        }
                    }
                }
                if (!flag) {
                    if (n.getSeenByList() == null) {
                        n.setSeenByList(new ArrayList<>());
                    }
                    n.getSeenByList().add(u);
                    databaseViewModel.updateNote(n);

                }
                flag = false;
            }
        }
    }

    private User convertUserMeToUser() {
        User user = new User();
        user.setFirstName(userMe.getFirstName());
        user.setLastName(userMe.getLastName());
        user.setUserFirebaseID(userMe.getUserFirebaseID());
        user.setAvailability(true);
        user.setFcmToken(userMe.getFcmToken());
        user.setClearanceLvl(userMe.getClearanceLvl());
        return user;
    }

    @Override
    public void onItemEdit(int position) {
//        goToNewNote();
    }

    private void goToNewNote(){
        Intent intent = new Intent();
        intent.setClass(NotesActivity.this, NewNoteActivity.class);
        startActivity(intent);
    }

    private void loading (boolean b) {
        if (b) {
            binding.loading.setVisibility(View.VISIBLE);
        } else {
            binding.loading.setVisibility(View.GONE);
        }
    }
}