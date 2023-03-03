package com.zoramedic.zoramedicapp.view.patients;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.zoramedic.zoramedicapp.R;
import com.zoramedic.zoramedicapp.data.BloodPressure;
import com.zoramedic.zoramedicapp.data.Patient;
import com.zoramedic.zoramedicapp.data.Pharmacy;
import com.zoramedic.zoramedicapp.data.UserMe;
import com.zoramedic.zoramedicapp.databinding.ActivityPatientsHomeCardBinding;
import com.zoramedic.zoramedicapp.ui.ItemTouchHelperDelete;
import com.zoramedic.zoramedicapp.view.util.Constants;
import com.zoramedic.zoramedicapp.viewmodel.DatabaseViewModel;

import java.util.Date;
import java.util.List;
import java.util.Objects;


public class PatientsHomeAdapter extends RecyclerView.Adapter<PatientsHomeAdapter.ViewHolder> implements ItemTouchHelperDelete {

    private PatientsActivity context;
    private List<Patient> patientList;
    private DatabaseViewModel databaseViewModel;

    private List<Patient> save;

    private View view;

    public PatientsHomeAdapter(PatientsActivity context, List<Patient> patientList, DatabaseViewModel databaseViewModel, View view) {
        this.context = context;
        this.patientList = patientList;
        this.databaseViewModel = databaseViewModel;
        this.view = view;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityPatientsHomeCardBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.activity_patients_home_card, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Patient patient = patientList.get(position);
        holder.binding.cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPatient(patient);
            }
        });
        holder.bind(patient);

    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public void setPatientList(List<Patient> list) {
        patientList = list;
    }

    public void saveFullPatientList() {
        save = patientList;
    }

    public void returnFullPatientList() {
        patientList = save;
    }

    @Override
    public void onItemDelete(int position) {
        Patient p = patientList.get(position);
        context.refreshAdapter(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
                .setMessage(context.getString(R.string.are_you_sure_you_want_to_delete) + " " + p.getName())
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        databaseViewModel.deletePatient(p);
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).show();

    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        private ActivityPatientsHomeCardBinding binding;

        public ViewHolder(ActivityPatientsHomeCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Patient patient) {
            binding.setPatient(patient);
            if (patient.isMale()) {
                binding.gender.setColorFilter(ContextCompat.getColor(context, R.color.blue), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                binding.gender.setColorFilter(ContextCompat.getColor(context, R.color.pink), android.graphics.PorterDuff.Mode.SRC_IN);
            }
            binding.executePendingBindings();
        }
    }

    public void openPatient(Patient patient){
        Intent intent = new Intent(context, SelectedPatientActivity.class);
        intent.putExtra(Constants.KEY_PATIENT, patient);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
