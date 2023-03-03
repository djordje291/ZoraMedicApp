package com.zoramedic.zoramedicapp.view.pharmacy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.zoramedic.zoramedicapp.R;
import com.zoramedic.zoramedicapp.data.Pharmacy;
import com.zoramedic.zoramedicapp.databinding.PharmacyDialogBinding;
import com.zoramedic.zoramedicapp.viewmodel.DatabaseViewModel;

import java.util.Objects;

public class PharmacyDialog extends DialogFragment {

    private DatabaseViewModel databaseViewModel;
    private PharmacyDialogBinding binding;
    private Pharmacy pharmacy;

    private String updateOrAdd;

    private Context context;

    public PharmacyDialog(DatabaseViewModel databaseViewModel, Pharmacy pharmacy, Context context) {
        this.databaseViewModel = databaseViewModel;
        this.pharmacy = pharmacy;
        this.context = context;
        updateOrAdd = context.getString(R.string.update);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.pharmacy_dialog, null, false);
//        getActivity().setContentView(binding.getRoot());

        binding.name.requestFocus();

        if (pharmacy != null) {
            binding.setPharmacy(pharmacy);
            updateOrAdd = context.getString(R.string.update);
        } else {
            updateOrAdd = context.getString(R.string.add);
            binding.minimalLimit.setText(null);
        }

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(binding.getRoot())
                    .setPositiveButton(updateOrAdd, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!TextUtils.isEmpty(binding.name.getText().toString().trim())
                                    && !TextUtils.isEmpty(binding.minimalLimit.getText().toString().trim())
                                    && !TextUtils.isEmpty(binding.quantity.getText().toString().trim())
                                    && !TextUtils.isEmpty(binding.price.getText().toString().trim())) {

                                Pharmacy p = new Pharmacy();
                                p.setName(binding.name.getText().toString().trim());
                                p.setMinimalLimit(Integer.parseInt(binding.minimalLimit.getText().toString().trim()));
                                p.setQuantity(Integer.parseInt(binding.quantity.getText().toString().trim()));
                                p.setPrice(Integer.parseInt(binding.price.getText().toString().trim()));
                                p.setHasLow(p.getQuantity() - p.getMinimalLimit() <= 0);
                                p.setPerOs(binding.perOs.isChecked());

                                if (updateOrAdd.equals(context.getString(R.string.update))) {
                                    p.setDocRef(pharmacy.getDocRef());
                                    databaseViewModel.updatePharmacy(p);
                                } else {
                                    databaseViewModel.setPharmacy(p);
                                }
                            } else {
                                Toast.makeText(getActivity(), R.string.fill_in_all_the_fields, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Objects.requireNonNull(PharmacyDialog.this.getDialog()).cancel();
                        }
                    });
            
            return builder.create();
    }
}
