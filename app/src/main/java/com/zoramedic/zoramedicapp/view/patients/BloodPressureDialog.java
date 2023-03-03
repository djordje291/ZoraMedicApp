package com.zoramedic.zoramedicapp.view.patients;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.zoramedic.zoramedicapp.R;
import com.zoramedic.zoramedicapp.data.BloodPressure;
import com.zoramedic.zoramedicapp.data.Patient;
import com.zoramedic.zoramedicapp.data.UserMe;
import com.zoramedic.zoramedicapp.databinding.BloodPressureDialogBinding;
import com.zoramedic.zoramedicapp.viewmodel.DatabaseViewModel;

import java.util.Date;
import java.util.Objects;

public class BloodPressureDialog extends DialogFragment {

    private DatabaseViewModel databaseViewModel;
    private BloodPressureDialogBinding binding;
    private Patient patient;

    public BloodPressureDialog(DatabaseViewModel databaseViewModel, Patient patient) {
        this.databaseViewModel = databaseViewModel;
        this.patient = patient;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.blood_pressure_dialog, null, false);
//        getActivity().setContentView(binding.getRoot());

        binding.systolic.requestFocus();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(binding.getRoot())
                .setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!TextUtils.isEmpty(binding.systolic.getText().toString().trim()) &&
                                !TextUtils.isEmpty(binding.diastolic.getText().toString().trim()) &&
                                !TextUtils.isEmpty(binding.pulse.getText().toString().trim())) {
                            BloodPressure bloodPressure = new BloodPressure();
                            bloodPressure.setSystolic(Integer.parseInt(binding.systolic.getText().toString().trim()));
                            bloodPressure.setDiastolic(Integer.parseInt(binding.diastolic.getText().toString().trim()));
                            bloodPressure.setPulse(Integer.parseInt(binding.pulse.getText().toString().trim()));
                            bloodPressure.setTimestamp(new Date());
                            bloodPressure.setCreatorID(UserMe.getInstance().getUserFirebaseID());

                            patient.getBloodPressureHistory().add(bloodPressure);
                            databaseViewModel.updatePatientWithUri(patient, null, null);

                        }
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Objects.requireNonNull(BloodPressureDialog.this.getDialog()).cancel();
                    }
                });

        return builder.create();
    }
}

