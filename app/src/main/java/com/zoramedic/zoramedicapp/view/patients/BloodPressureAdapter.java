package com.zoramedic.zoramedicapp.view.patients;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.zoramedic.zoramedicapp.R;
import com.zoramedic.zoramedicapp.data.BloodPressure;
import com.zoramedic.zoramedicapp.data.Patient;
import com.zoramedic.zoramedicapp.data.User;
import com.zoramedic.zoramedicapp.data.UserMe;
import com.zoramedic.zoramedicapp.databinding.BloodPressureCardBinding;

import java.util.List;

public class BloodPressureAdapter extends RecyclerView.Adapter<BloodPressureAdapter.ViewHolder> {


    private SelectedPatientActivity context;
    private List<BloodPressure> bloodPressureList;
    private List<User> userList;

    public BloodPressureAdapter(SelectedPatientActivity context, Patient patient, List<User> userList) {
        this.context = context;
        this.bloodPressureList = patient.getBloodPressureHistory();
        this.userList = userList;
    }

    @NonNull
    @Override
    public BloodPressureAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BloodPressureCardBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.blood_pressure_card,
                parent,
                false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BloodPressureAdapter.ViewHolder holder, int position) {
        BloodPressure bloodPressure = bloodPressureList.get(position);
        for (User u : userList) {
            if (u.getUserFirebaseID().equals(bloodPressure.getCreatorID())) {
                holder.bind(bloodPressure, u);
            }
        }
    }

    public void getNewBloodPressureHistory(Patient patient) {
        this.bloodPressureList = patient.getBloodPressureHistory();
    }

    @Override
    public int getItemCount() {
        return bloodPressureList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public BloodPressureCardBinding binding;

        public ViewHolder(BloodPressureCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(BloodPressure bloodPressure, User user) {
            binding.setBloodPressure(bloodPressure);
            binding.setUser(user);
            binding.setActivity(context);
            binding.setUserMe(UserMe.getInstance());
            binding.executePendingBindings();
        }
    }
}
