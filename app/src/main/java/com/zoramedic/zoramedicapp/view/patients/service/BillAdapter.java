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
import com.zoramedic.zoramedicapp.data.Bill;
import com.zoramedic.zoramedicapp.data.Pharmacy;
import com.zoramedic.zoramedicapp.databinding.BillCardBinding;

import java.util.List;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder> {

    private List<Bill> billList;
    private Context context;

    private List<Bill> save;

    public BillAdapter(Context context, List<Bill> billList) {
        this.context = context;
        this.billList = billList;
    }

    @NonNull
    @Override
    public BillAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BillCardBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bill_card, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BillAdapter.ViewHolder holder, int position) {
        Bill bill = billList.get(position);
        holder.bind(bill);
        holder.binding.billTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof ServiceActivity) {
                    ((ServiceActivity) context).setBill(bill);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return billList.size();
    }

    public void setBillList(List<Bill> list) {
        billList = list;
    }

    public void saveFullBillList() {
        save = billList;
    }

    public void returnFullBillList() {
        billList = save;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public BillCardBinding binding;

        public ViewHolder(BillCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Bill bill) {
            binding.setBill(bill);
            binding.executePendingBindings();
        }
    }
}
