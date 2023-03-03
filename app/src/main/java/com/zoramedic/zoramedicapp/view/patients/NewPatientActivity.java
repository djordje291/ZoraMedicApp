package com.zoramedic.zoramedicapp.view.patients;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.zoramedic.zoramedicapp.R;
import com.zoramedic.zoramedicapp.data.Patient;
import com.zoramedic.zoramedicapp.data.Saturation;
import com.zoramedic.zoramedicapp.databinding.ActivityNewPatientBinding;
import com.zoramedic.zoramedicapp.view.InternetActivity;
import com.zoramedic.zoramedicapp.view.util.Constants;
import com.zoramedic.zoramedicapp.viewmodel.DatabaseViewModel;


public class NewPatientActivity extends InternetActivity {

    ActivityNewPatientBinding binding;
    DatabaseViewModel databaseViewModel;

    ActivityResultLauncher<Intent> someActivityResultLauncher;
    Uri fileUri;

    Patient patient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewPatientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseViewModel = new ViewModelProvider(this).get(DatabaseViewModel.class);

        checkForEdit();

        binding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.name.getText().toString().trim().isEmpty() &&
                        !binding.bloodType.getText().toString().trim().isEmpty() &&
                        !binding.room.getText().toString().trim().isEmpty() &&
                        !binding.weight.getText().toString().trim().isEmpty() &&
                        !binding.height.getText().toString().trim().isEmpty() &&
                        !binding.address.getText().toString().trim().isEmpty() &&
                        !binding.phoneNumber.getText().toString().trim().isEmpty()) {
                    if (binding.JMBG.getText().toString().trim().length() == 13) {
                        if (patient == null) {
                            patient = new Patient();
                        }
                        patient.setName(binding.name.getText().toString().trim());
                        patient.setBloodType(binding.bloodType.getText().toString().trim());
                        patient.setRoomNumber(binding.room.getText().toString().trim());
                        patient.setWeight(Integer.parseInt(binding.weight.getText().toString().trim()));
                        patient.setHeight(Integer.parseInt(binding.height.getText().toString().trim()));
                        patient.setAddress(binding.address.getText().toString().trim());
                        patient.setPhoneNumber(binding.phoneNumber.getText().toString().trim());
                        patient.setJMBG(binding.JMBG.getText().toString().trim());
                        patient.setMale(binding.gender.isChecked());
                        patient.setBirthday();
//                        patient.setSaturation(new Saturation());

                        if (patient.getDocRef() == null || patient.getDocRef().trim().isEmpty()) {
                            databaseViewModel.setPatient(patient, fileUri);
                        } else {
                            databaseViewModel.updatePatientWithUri(patient, fileUri, null);
                        }
                        finish();
                    } else {
                        Toast.makeText(NewPatientActivity.this, R.string.jmbg_has_to_have_13_digits, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(NewPatientActivity.this, R.string.fill_in_all_the_fields, Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDocument();
            }
        });

        binding.gender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                changeGender(b);
            }
        });

        someActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            fileUri = result.getData().getData();
                            binding.uploadButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_cloud_done));
                            binding.tvUpload.setText(R.string.word_document_uploaded);
                            binding.tvUpload.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_purple));
                            Toast.makeText(NewPatientActivity.this, R.string.file_selected, Toast.LENGTH_SHORT).show();
                        } else {
                            fileUri = null;
                            binding.uploadButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_cloud_upload));
                            binding.tvUpload.setText(R.string.upload_word_document);
                            binding.tvUpload.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                            Toast.makeText(NewPatientActivity.this, R.string.file_not_selected, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void checkForEdit() {
        if (getIntent().getSerializableExtra(Constants.KEY_PATIENT) != null) {
            patient = (Patient) getIntent().getSerializableExtra(Constants.KEY_PATIENT);
            fillInFields();
        }
    }

    private void fillInFields() {
        binding.setPatient(patient);
        changeGender(patient.isMale());
        if (patient.getWordPath() != null && !patient.getWordPath().trim().equals("")) {
            binding.uploadButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_cloud_done));
            binding.tvUpload.setText(R.string.word_document_uploaded);
            binding.tvUpload.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_purple));
        }
    }

    private void getDocument() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/msword");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        someActivityResultLauncher.launch(intent);
    }

    private void changeGender(boolean b) {
        if (b) {
            binding.gender.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
            binding.gender.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_male, 0, 0, 0);
        } else {
            binding.gender.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
            binding.gender.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_female, 0, 0, 0);
        }
        binding.gender.setChecked(b);
    }
}