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
import com.zoramedic.zoramedicapp.databinding.PharmacyAddedCardBinding;

import java.util.List;

public class PharmacyAddedAdapter extends RecyclerView.Adapter<PharmacyAddedAdapter.ViewHolder> {

    private Context context;
    private List<Pharmacy> pharmacyList;

    public PharmacyAddedAdapter(Context context, List<Pharmacy> pharmacyList) {
        this.context = context;
        this.pharmacyList = pharmacyList;
    }

    @NonNull
    @Override
    public PharmacyAddedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PharmacyAddedCardBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.pharmacy_added_card, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PharmacyAddedAdapter.ViewHolder holder, int position) {
        Pharmacy pharmacy = pharmacyList.get(position);
        holder.bind(pharmacy);

        holder.binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if  (pharmacy.getUsed() < pharmacy.getQuantity()){
                    pharmacy.setUsed(pharmacy.getUsed() + 1);
                    holder.bind(pharmacy);
                } else {
                    Toast.makeText(context, R.string.no_more_in_stock, Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.binding.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pharmacy.getUsed() > 0) {
                    pharmacy.setUsed(pharmacy.getUsed() - 1);
                    holder.bind(pharmacy);
                } else {
                    Toast.makeText(context, R.string.cant_go_lower, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return pharmacyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public PharmacyAddedCardBinding binding;

        public ViewHolder(PharmacyAddedCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Pharmacy pharmacy) {
            binding.setPharmacy(pharmacy);
            binding.executePendingBindings();
        }
    }
}
