package com.zoramedic.zoramedicapp.view.patients;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.zoramedic.zoramedicapp.R;
import com.zoramedic.zoramedicapp.data.BloodPressure;
import com.zoramedic.zoramedicapp.data.Patient;
import com.zoramedic.zoramedicapp.data.Saturation;
import com.zoramedic.zoramedicapp.data.UserMe;
import com.zoramedic.zoramedicapp.databinding.SaturationDialogBinding;
import com.zoramedic.zoramedicapp.viewmodel.DatabaseViewModel;

import java.util.Date;
import java.util.Objects;

public class SaturationDialog extends DialogFragment {

    private Patient patient;
    private DatabaseViewModel databaseViewModel;
    private SaturationDialogBinding binding;
    private Context context;

    public SaturationDialog(Patient patient, DatabaseViewModel databaseViewModel, Context context) {
        this.patient = patient;
        this.databaseViewModel = databaseViewModel;
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.saturation_dialog, null, false);
        if (patient.getSaturation() == null) {
            patient.setSaturation(new Saturation());
        }
        binding.setSaturation(patient.getSaturation());

        binding.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(binding.percentage.getText().toString().trim()) < 100) {
                    patient.getSaturation().setPercentage(Integer.parseInt(binding.percentage.getText().toString().trim()) + 1);
                    binding.setSaturation(patient.getSaturation());
                } else {
                    Toast.makeText(context, R.string.its_already_100, Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(binding.percentage.getText().toString().trim()) > 0) {
                    patient.getSaturation().setPercentage(Integer.parseInt(binding.percentage.getText().toString().trim()) - 1);
                    binding.setSaturation(patient.getSaturation());
                } else {
                    Toast.makeText(context, R.string.its_already_0, Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(binding.getRoot())
                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!TextUtils.isEmpty(binding.percentage.getText().toString().trim())) {
                            Saturation saturation = new Saturation();
                            saturation.setPercentage(Integer.parseInt(binding.percentage.getText().toString().trim()));
                            saturation.setTimestamp(new Date());
                            saturation.setCreatorID(UserMe.getInstance().getUserFirebaseID());

                            patient.setSaturation(saturation);

                            databaseViewModel.updatePatientWithUri(patient, null, null);

                        }
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Objects.requireNonNull(SaturationDialog.this.getDialog()).cancel();
                    }
                });

        return builder.create();
    }
}
