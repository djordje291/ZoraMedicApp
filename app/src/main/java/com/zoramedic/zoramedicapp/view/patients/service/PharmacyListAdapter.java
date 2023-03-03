package com.zoramedic.zoramedicapp.view.patients.service;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.zoramedic.zoramedicapp.R;
import com.zoramedic.zoramedicapp.data.Pharmacy;
import com.zoramedic.zoramedicapp.data.Service;
import com.zoramedic.zoramedicapp.databinding.PharmacyListCardBinding;
import com.zoramedic.zoramedicapp.viewmodel.DatabaseViewModel;

import java.util.List;

public class PharmacyListAdapter extends RecyclerView.Adapter<PharmacyListAdapter.ViewHolder>{

    private Context context;
    private List<Pharmacy> pharmacyList;

    private List<Pharmacy> save;

    public PharmacyListAdapter(Context context, List<Pharmacy> pharmacyList) {
        this.context = context;
        this.pharmacyList = pharmacyList;
    }

    @NonNull
    @Override
    public PharmacyListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PharmacyListCardBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.pharmacy_list_card, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PharmacyListAdapter.ViewHolder holder, int position) {
        Pharmacy pharmacy = pharmacyList.get(position);
        holder.bind(pharmacy);
        holder.binding.pharmacyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof ServiceActivity) {
                    ((ServiceActivity) context).setPharmacy(pharmacy);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return pharmacyList.size();
    }

    public void setPharmacyList(List<Pharmacy> list) {
        pharmacyList = list;
    }

    public void saveFullPharmacyList() {
        save = pharmacyList;
    }

    public void returnFullPharmacyList() {
        pharmacyList = save;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public PharmacyListCardBinding binding;

        public ViewHolder(PharmacyListCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Pharmacy pharmacy) {
            binding.setPharmacy(pharmacy);
        }
    }
}
