package com.zoramedic.zoramedicapp.view.patients;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.zoramedic.zoramedicapp.R;
import com.zoramedic.zoramedicapp.data.Bill;
import com.zoramedic.zoramedicapp.data.BloodPressure;
import com.zoramedic.zoramedicapp.data.Patient;
import com.zoramedic.zoramedicapp.data.Pharmacy;
import com.zoramedic.zoramedicapp.data.Service;
import com.zoramedic.zoramedicapp.data.User;
import com.zoramedic.zoramedicapp.data.UserMe;
import com.zoramedic.zoramedicapp.data.WordCreator;
import com.zoramedic.zoramedicapp.databinding.ActivitySelectedPatientBinding;
import com.zoramedic.zoramedicapp.ui.RangeDateValidator;
import com.zoramedic.zoramedicapp.view.InternetActivity;
import com.zoramedic.zoramedicapp.view.patients.service.ServiceActivity;
import com.zoramedic.zoramedicapp.view.util.Constants;
import com.zoramedic.zoramedicapp.viewmodel.DatabaseViewModel;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class SelectedPatientActivity extends InternetActivity implements View.OnClickListener {

    ActivitySelectedPatientBinding binding;
    Patient selectedPatient;

    MenuItem wordDoc;
    MenuItem bill;

    DatabaseViewModel databaseViewModel;

    Context context;

    private BloodPressureAdapter bloodPressureAdapter;
    private MedicalHistoryAdapter medicalHistoryAdapter;

    List<User> users = new ArrayList<>();
    private List<Bill> billList = new ArrayList<>();
    private List<Pharmacy> pharmacyList = new ArrayList<>();

    Animation dropdown;
    Animation slideUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectedPatientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = getApplicationContext();

        databaseViewModel = new ViewModelProvider(this).get(DatabaseViewModel.class);

        databaseViewModel.getUserList().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> userList) {
                if (!userList.isEmpty()) {
                    if (!users.isEmpty()) {
                        users.clear();
                    }
                    users.addAll(userList);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(context);

                    binding.bloodPressureRV.setLayoutManager(layoutManager);
                    bloodPressureAdapter = new BloodPressureAdapter(SelectedPatientActivity.this, selectedPatient, users);
                    binding.bloodPressureRV.setAdapter(bloodPressureAdapter);
                    bloodPressureAdapter.notifyDataSetChanged();

                    LinearLayoutManager layoutManager1 = new LinearLayoutManager(context);

                    binding.medicalHistoryRV.setLayoutManager(layoutManager1);
                    medicalHistoryAdapter = new MedicalHistoryAdapter(SelectedPatientActivity.this, selectedPatient.getServices(), users);
                    binding.medicalHistoryRV.setAdapter(medicalHistoryAdapter);
                    medicalHistoryAdapter.notifyDataSetChanged();

                } else {
                    users.clear();
                }
            }
        });

        selectedPatient = (Patient) getIntent().getSerializableExtra(Constants.KEY_PATIENT);

        databaseViewModel.getPatient(selectedPatient.getDocRef()).observe(this, new Observer<Patient>() {
            @Override
            public void onChanged(Patient patient) {
                if (patient != null && patient.getName() != null) {
                    selectedPatient = patient;
                    binding.setPatient(selectedPatient);
                    getUserSaturationForBinding();
                    if (bloodPressureAdapter != null) {
                        bloodPressureAdapter.getNewBloodPressureHistory(selectedPatient);
                        bloodPressureAdapter.notifyDataSetChanged();
                    }
                    if (medicalHistoryAdapter != null) {
                        medicalHistoryAdapter.getNewMedicalHistory(selectedPatient);
                        medicalHistoryAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        binding.setPatient(selectedPatient);

        binding.additionalInformationBtn.setOnClickListener(this);
        binding.bloodPressureBtn.setOnClickListener(this);
        binding.medicalHistoryBtn.setOnClickListener(this);
        binding.addBloodPressure.setOnClickListener(this);
        binding.addMedicalHistory.setOnClickListener(this);
        binding.percentage.setOnClickListener(this);
        binding.saturationBtn.setOnClickListener(this);


        databaseViewModel.getBillList().observe(this, new Observer<List<Bill>>() {
            @Override
            public void onChanged(List<Bill> bills) {
                if (!bills.isEmpty()) {
                    if (!billList.isEmpty()) {
                        billList.clear();
                    }
                    billList.addAll(bills);
                } else {
                    billList.clear();
                }
            }
        });
        databaseViewModel.getPharmacyList().observe(this, new Observer<List<Pharmacy>>() {
            @Override
            public void onChanged(List<Pharmacy> pharmacies) {
                if (!pharmacies.isEmpty()) {
                    if (!pharmacyList.isEmpty()) {
                        pharmacyList.clear();
                    }
                    pharmacyList.addAll(pharmacies);
                } else {
                    pharmacyList.clear();
                }
            }
        });

        setGenderColor();

        dropdown = AnimationUtils.loadAnimation(SelectedPatientActivity.this, R.anim.dropdown);
        slideUp = AnimationUtils.loadAnimation(SelectedPatientActivity.this, R.anim.slide_up);
    }

    public void toggleView(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            view.startAnimation(slideUp);
            view.setVisibility(View.GONE);

        } else {
            view.startAnimation(dropdown);
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.selected_patient_menu, menu);
        wordDoc = menu.findItem(R.id.wordDoc);
        bill = menu.findItem(R.id.bill);
        if (selectedPatient.getWordPath() == null || selectedPatient.getWordPath().isEmpty()) {
            wordDoc.setIcon(R.drawable.ic_cloud_off);
        }
        if (UserMe.getInstance().getClearanceLvl() < 1) {
            bill.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.wordDoc) {
            if (selectedPatient.getWordPath() != null && !selectedPatient.getWordPath().isEmpty()) {
                databaseViewModel.getWordFile(selectedPatient, this);
            } else {
                Toast.makeText(this, R.string.no_file, Toast.LENGTH_SHORT).show();
            }
        }
        if (item.getItemId() == R.id.bill) {
            showRangeDatePicker();
        }
        return super.onOptionsItemSelected(item);
    }
    private void showRangeDatePicker() {
        Pair<Long, Long> days = getFirstAndLastDay();
        CalendarConstraints.Builder constraintsBuilderRange = new CalendarConstraints.Builder();
        constraintsBuilderRange.setValidator(new RangeDateValidator(days));


        MaterialDatePicker materialDatePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setSelection(days)
                .setCalendarConstraints(constraintsBuilderRange.build()).build();
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                Pair<Long,Long> d = (Pair<Long, Long>) selection;
                filterServices(new Date(d.first), new Date(d.second));
            }
        });
        materialDatePicker.show(getSupportFragmentManager(),Constants.KEY_DATE_RANGE_PICKER);
    }

    private Pair<Long, Long> getFirstAndLastDay() {
        Date start = selectedPatient.getServices().get(0).getTimestamp();
        Date end = selectedPatient.getServices().get(selectedPatient.getServices().size() - 1).getTimestamp();
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.setTime(start);
        calendarStart.add(Calendar.HOUR_OF_DAY, +1);
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(end);
        calendarEnd.add(Calendar.HOUR_OF_DAY, +1);
        Pair<Long, Long> p = new Pair<Long, Long>(calendarStart.getTime().getTime(), calendarEnd.getTime().getTime());
        return p;
    }

    private void createWordBill(Patient patient, Date start, Date end) {
        loading(true);
        WordCreator wordCreator = new WordCreator(patient, SelectedPatientActivity.this, billList, pharmacyList, start, end);
        wordCreator.create();
        wordCreator.openFile();
        loading(false);
    }

    private void filterServices(Date start, Date end) {
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.setTime(start);
        calendarStart.add(Calendar.HOUR_OF_DAY, -1);
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(end);
        calendarEnd.add(Calendar.HOUR_OF_DAY, 23);

        Patient p = new Patient();
        p.setName(selectedPatient.getName());
        p.setDocRef(selectedPatient.getDocRef());
        p.setJMBG(selectedPatient.getJMBG());
        p.setAddress(selectedPatient.getAddress());
        p.setMale(selectedPatient.isMale());
        p.setPhoneNumber(selectedPatient.getPhoneNumber());
        p.setHeight(selectedPatient.getHeight());
        p.setWeight(selectedPatient.getWeight());
        p.setRoomNumber(selectedPatient.getRoomNumber());
        p.setBloodType(selectedPatient.getBloodType());
        p.setBloodPressureHistory(selectedPatient.getBloodPressureHistory());
        p.setWordPath(selectedPatient.getWordPath());
        p.setServices(selectedPatient.getServices());
        p.getServices().removeIf(service -> !service.getTimestamp().after(calendarStart.getTime()) || !service.getTimestamp().before(calendarEnd.getTime()));
        createWordBill(p, start, end);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.additionalInformationBtn:
                toggleView(binding.additionalInformationCL);
                break;
            case R.id.bloodPressureBtn:
                if (selectedPatient.getBloodPressureHistory().size() > 0 && selectedPatient.getBloodPressureHistory() != null) {
                    toggleView(binding.bloodPressureRV);
                } else {
                    Toast.makeText(context, R.string.no_blood_pressure_history, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.medicalHistoryBtn:
                if (selectedPatient.getServices().size() > 0 && selectedPatient.getServices() != null) {
                    toggleView(binding.medicalHistoryRV);
                } else {
                    Toast.makeText(context,  R.string.no_medical_history, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.addBloodPressure:
                BloodPressureDialog dialog = new BloodPressureDialog(databaseViewModel, selectedPatient);
                dialog.show(getSupportFragmentManager(), dialog.getTag());
                break;
            case R.id.addMedicalHistory:
                Intent intent = new Intent();
                intent.setClass(SelectedPatientActivity.this, ServiceActivity.class);
                intent.putExtra(Constants.KEY_PATIENT, selectedPatient);
                startActivity(intent);
                break;
            case R.id.percentage:
                SaturationDialog saturationDialog = new SaturationDialog(selectedPatient, databaseViewModel, context);
                saturationDialog.show(getSupportFragmentManager(), saturationDialog.getTag());
                break;
            case R.id.saturationBtn:
                toggleView(binding.saturationCL);
                break;
        }
    }

    public void deleteBloodPressure(BloodPressure bp) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SelectedPatientActivity.this);
        builder
                .setMessage(context.getString(R.string.are_you_sure_you_want_to_delete))
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (selectedPatient.getBloodPressureHistory().removeIf(bloodPressure -> (bloodPressure.getTimestamp().equals(bp.getTimestamp())))) {
                            databaseViewModel.updatePatientWithUri(selectedPatient, null, null);
                        }
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).show();
    }

    public void deleteMedicalHistory(Service s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SelectedPatientActivity.this);
        builder
                .setMessage(context.getString(R.string.are_you_sure_you_want_to_delete))
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (selectedPatient.getServices().removeIf(service -> (service.getTimestamp().equals(s.getTimestamp())))) {
                            databaseViewModel.updatePatientWithUri(selectedPatient, null, null);
                        }
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).show();
    }

    public void editMedicalHistory(Service s) {
        Intent intent = new Intent();
        intent.setClass(SelectedPatientActivity.this, ServiceActivity.class);
        intent.putExtra(Constants.KEY_PATIENT, selectedPatient);
        intent.putExtra(Constants.KEY_SERVICE, s);
        startActivity(intent);
    }

    public void loading(boolean b) {
        if (b) {
            binding.loading.setVisibility(View.VISIBLE);
        } else {
            binding.loading.setVisibility(View.INVISIBLE);
        }
    }

    private void setGenderColor() {
        if (selectedPatient.isMale()) {
            binding.gender.setColorFilter(ContextCompat.getColor(context, R.color.blue), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            binding.gender.setColorFilter(ContextCompat.getColor(context, R.color.pink), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    private void getUserSaturationForBinding() {
        if (selectedPatient.getSaturation() != null) {
            if (users != null && users.size() > 0) {
                for (User u : users) {
                    if (selectedPatient.getSaturation().getCreatorID().equals(u.getUserFirebaseID())) {
                        binding.setUser(u);
                    }
                }
            }
        }
    }
}