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
import com.zoramedic.zoramedicapp.data.Service;
import com.zoramedic.zoramedicapp.data.User;
import com.zoramedic.zoramedicapp.data.UserMe;
import com.zoramedic.zoramedicapp.databinding.BloodPressureCardBinding;
import com.zoramedic.zoramedicapp.databinding.MedicalHistoryCardBinding;

import java.util.List;

public class MedicalHistoryAdapter extends RecyclerView.Adapter<MedicalHistoryAdapter.ViewHolder>{

    private SelectedPatientActivity context;
    private List<Service> serviceList;
    private List<User> userList;

    public MedicalHistoryAdapter(SelectedPatientActivity context, List<Service> serviceList, List<User> userList) {
        this.context = context;
        this.serviceList = serviceList;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MedicalHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MedicalHistoryCardBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.medical_history_card,
                parent,
                false);
        return new MedicalHistoryAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicalHistoryAdapter.ViewHolder holder, int position) {
        Service service = serviceList.get(position);
        for (User u : userList) {
            if (u.getUserFirebaseID().equals(service.getCreatorID())) {
                holder.bind(service, u);
            }
        }
    }

    public void getNewMedicalHistory(Patient patient) {
        this.serviceList = patient.getServices();
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public MedicalHistoryCardBinding binding;

        public ViewHolder(MedicalHistoryCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Service service, User user) {
            binding.setService(service);
            binding.setUser(user);
            binding.setUserMe(UserMe.getInstance());
            binding.setActivity(context);
            binding.executePendingBindings();
        }
    }
}
