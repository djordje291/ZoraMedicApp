package com.zoramedic.zoramedicapp.view.pharmacy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.zoramedic.zoramedicapp.R;
import com.zoramedic.zoramedicapp.databinding.ActivityPharmacyCardBinding;
import com.zoramedic.zoramedicapp.ui.ItemTouchHelperDelete;
import com.zoramedic.zoramedicapp.data.Pharmacy;
import com.zoramedic.zoramedicapp.data.UserMe;
import com.zoramedic.zoramedicapp.viewmodel.DatabaseViewModel;

import java.util.List;

public class PharmacyAdapter extends RecyclerView.Adapter<PharmacyAdapter.ViewHolder> implements ItemTouchHelperDelete {

    private Context context;
    private List<Pharmacy> pharmacyList;
    private DatabaseViewModel databaseViewModel;

    private List<Pharmacy> save;

    private View view;


    public PharmacyAdapter(Context context, List<Pharmacy> pharmacyList, DatabaseViewModel databaseViewModel, View view) {
        this.context = context;
        this.pharmacyList = pharmacyList;
        this.databaseViewModel = databaseViewModel;
        this.view = view;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityPharmacyCardBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.activity_pharmacy_card, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pharmacy pharmacy = pharmacyList.get(position);
        holder.bind(pharmacy);

        checkLvl(holder);

        holder.binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pharmacy.setQuantity(pharmacy.getQuantity() + 1);
                pharmacy.setHasLow(checkIsLow(pharmacy.getQuantity(), pharmacy.getMinimalLimit()));
                if (databaseViewModel != null) {
                    databaseViewModel.updatePharmacy(pharmacy);
                }
            }
        });
        Snackbar snackbar = Snackbar.make(view, R.string.pharmacy_is_removed, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pharmacy.setQuantity(pharmacy.getQuantity() + pharmacy.getUsed());
                        pharmacy.setHasLow(checkIsLow(pharmacy.getQuantity(), pharmacy.getMinimalLimit()));
                        pharmacy.setUsed(0);

                        if (databaseViewModel != null) {
                            databaseViewModel.updatePharmacy(pharmacy);
                        }

                        holder.bind(pharmacy);
                    }
                });
        snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT || event == DISMISS_EVENT_MANUAL || event == DISMISS_EVENT_CONSECUTIVE) {
                    pharmacy.setUsed(0);

                    if (databaseViewModel != null) {
                        databaseViewModel.updatePharmacy(pharmacy);
                    }
                }
            }
        });

        holder.binding.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pharmacy.getQuantity() >= 1) {
                    pharmacy.setQuantity(pharmacy.getQuantity() - 1);
                    pharmacy.setHasLow(checkIsLow(pharmacy.getQuantity(), pharmacy.getMinimalLimit()));
                    holder.bind(pharmacy);
                    pharmacy.setUsed(pharmacy.getUsed() + 1);

//                    if (databaseViewModel != null) {
//                        databaseViewModel.updatePharmacy(pharmacy);
//                    }

                    if (!snackbar.isShown()) {
                        snackbar.show();
                    }
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

    private void checkLvl(ViewHolder holder) {
        if (UserMe.getInstance().getClearanceLvl() < 1) {
            holder.binding.add.setVisibility(View.GONE);
        }
    }

    private boolean checkIsLow(int q, int m) {
        return q - m <= 0;
    }

    ;

    public void setPharmacyList(List<Pharmacy> list) {
        pharmacyList = list;
    }

    public void saveFullPharmacyList() {
        save = pharmacyList;
    }

    public void returnFullPharmacyList() {
        pharmacyList = save;
    }

    @Override
    public void onItemDelete(int position) {
        Pharmacy p = pharmacyList.get(position);
        pharmacyList.remove(p);
        notifyItemRemoved(position);
        Snackbar snackbar = Snackbar.make(view, R.string.pharmacy_is_removed, Snackbar.LENGTH_LONG)
                .setAction(context.getString(R.string.undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pharmacyList.add(position, p);
                        notifyItemInserted(position);
                        Toast.makeText(context, R.string.pharmacy_is_restored, Toast.LENGTH_SHORT).show();
                    }
                });
        snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT || event == DISMISS_EVENT_MANUAL || event == DISMISS_EVENT_CONSECUTIVE) {
                            databaseViewModel.deletePharmacy(p);
                }
            }
        });
        snackbar.show();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public ActivityPharmacyCardBinding binding;

        public ViewHolder(ActivityPharmacyCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Pharmacy pharmacy) {
            binding.setPharmacy(pharmacy);
            binding.setUserMe(UserMe.getInstance());
            binding.executePendingBindings();
        }
    }
}
