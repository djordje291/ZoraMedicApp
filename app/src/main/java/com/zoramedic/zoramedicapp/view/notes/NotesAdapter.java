package com.zoramedic.zoramedicapp.view.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.zoramedic.zoramedicapp.R;
import com.zoramedic.zoramedicapp.data.Note;
import com.zoramedic.zoramedicapp.data.Pharmacy;
import com.zoramedic.zoramedicapp.data.User;
import com.zoramedic.zoramedicapp.data.UserMe;
import com.zoramedic.zoramedicapp.databinding.ActivityNotesCardBinding;
import com.zoramedic.zoramedicapp.ui.ItemTouchHelperDelete;
import com.zoramedic.zoramedicapp.viewmodel.DatabaseViewModel;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> implements ItemTouchHelperDelete {

    private Context context;
    private List<Note> noteList;
    private DatabaseViewModel databaseViewModel;
    private View view;

    public NotesAdapter (Context context, List<Note> noteList, DatabaseViewModel databaseViewModel, View view) {
        this.context = context;
        this.noteList = noteList;
        this.databaseViewModel = databaseViewModel;
        this.view = view;
    }

    @NonNull
    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityNotesCardBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()
        ), R.layout.activity_notes_card, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.ViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.bind(note);
        if (!note.getCreatorID().equals(UserMe.getInstance().getUserFirebaseID())) {

        }

    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ActivityNotesCardBinding binding;

        public ViewHolder(ActivityNotesCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Note note) {
            binding.setNote(note);
//            binding.setLifecycleOwner(NotesActivity.class);
            binding.setUserMe(UserMe.getInstance());
            binding.setAdapter(NotesAdapter.this);
            binding.executePendingBindings();
        }
    }

    public void deleteNote(Note note) {
        databaseViewModel.deleteNote(note);
        Toast.makeText(context, R.string.note_deleted, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemDelete(int position) {
        Note note = noteList.get(position);
        deleteNote(note);

//        Snackbar snackbar = Snackbar.make(view, R.string.pharmacy_is_removed, Snackbar.LENGTH_LONG)
//                .setAction("UNDO", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        if (databaseViewModel != null) {
//                            databaseViewModel.setNote(note);
//                        }
//                        Snackbar snackbar1 = Snackbar.make(view, R.string.pharmacy_is_restored, Snackbar.LENGTH_SHORT);
//                        snackbar1.show();
//                    }
//                });
//        snackbar.show();
    }
}
