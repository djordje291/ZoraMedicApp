package com.zoramedic.zoramedicapp.view.patients;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.zoramedic.zoramedicapp.R;
import com.zoramedic.zoramedicapp.data.Patient;
import com.zoramedic.zoramedicapp.data.Pharmacy;
import com.zoramedic.zoramedicapp.data.User;
import com.zoramedic.zoramedicapp.data.UserMe;
import com.zoramedic.zoramedicapp.databinding.ActivityPatientsBinding;
import com.zoramedic.zoramedicapp.ui.ItemTouchHelperEdit;
import com.zoramedic.zoramedicapp.ui.SwipeToDeleteAndEditCallback;
import com.zoramedic.zoramedicapp.view.InternetActivity;
import com.zoramedic.zoramedicapp.view.notes.NotesActivity;
import com.zoramedic.zoramedicapp.view.pharmacy.PharmacyDialog;
import com.zoramedic.zoramedicapp.view.util.Constants;
import com.zoramedic.zoramedicapp.view.util.Sleeper;
import com.zoramedic.zoramedicapp.viewmodel.DatabaseViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PatientsActivity extends InternetActivity implements SearchView.OnQueryTextListener, ItemTouchHelperEdit {

    private ActivityPatientsBinding binding;
    private DatabaseViewModel databaseViewModel;

    private List<Patient> patientList = new ArrayList<>();

    private PatientsHomeAdapter patientsHomeAdapter;

    private MenuItem search;
    private SearchView searchView;

    private List<Patient> tempList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPatientsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.setUser(UserMe.getInstance());
        loading(true);

        databaseViewModel = new ViewModelProvider(this).get(DatabaseViewModel.class);

        databaseViewModel.getPatientList().observe(this, new Observer<List<Patient>>() {
            @Override
            public void onChanged(List<Patient> patients) {
                if (!patients.isEmpty()) {
                    if (!patientList.isEmpty()) {
                        patientList.clear();
                    }
                    binding.emptyPatients.setVisibility(View.GONE);

                    patientList.addAll(patients);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    binding.recyclerViewPatients.setLayoutManager(layoutManager);

                    patientsHomeAdapter = new PatientsHomeAdapter(PatientsActivity.this, patientList, databaseViewModel, binding.conView);
                    patientsHomeAdapter.saveFullPatientList();
                    binding.recyclerViewPatients.setAdapter(patientsHomeAdapter);
                    loading(false);
                    patientsHomeAdapter.notifyDataSetChanged();

                    if (UserMe.getInstance().getClearanceLvl() > 0) {
                        ItemTouchHelper.Callback callback = new SwipeToDeleteAndEditCallback(getApplicationContext(),
                                patientsHomeAdapter, PatientsActivity.this, null);
                        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
                        itemTouchHelper.attachToRecyclerView(binding.recyclerViewPatients);
                    }
                } else {
                    patientList.clear();
                    new Sleeper(binding.emptyPatients, binding.loading, patients, patientList).start();
                    if (binding.recyclerViewPatients.getAdapter() != null) {
                        binding.recyclerViewPatients.getAdapter().notifyDataSetChanged();
                    }
                }
            }
        });

        binding.fabPatients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientsActivity.this, NewPatientActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loading (boolean b) {
        if (b) {
            binding.loading.setVisibility(View.VISIBLE);
        } else {
            binding.loading.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pharmacy_menu, menu);
        search = menu.findItem(R.id.pharmacy_search);
        searchView = (SearchView) search.getActionView();
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query != null) {
            searchPharmacy(query);
        } else {
            patientsHomeAdapter.returnFullPatientList();
            refreshAdapter(null);
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (query != null) {
            searchPharmacy(query);
        } else {
            patientsHomeAdapter.returnFullPatientList();
            refreshAdapter(null);
        }
        return true;
    }

    private void searchPharmacy(String typedIn) {
        tempList.clear();
        for (Patient patient : patientList) {
            if  (patient.getName().toLowerCase(Locale.ROOT).contains(typedIn.toLowerCase(Locale.ROOT))) {
                tempList.add(patient);
            }
        }
        patientsHomeAdapter.setPatientList(tempList);
        refreshAdapter(null);
    }



    @Override
    public void onItemEdit(int position) {
        refreshAdapter(position);
        Intent intent = new Intent(PatientsActivity.this, NewPatientActivity.class);
        intent.putExtra(Constants.KEY_PATIENT, patientList.get(position));
        startActivity(intent);
    }

    public void refreshAdapter(Integer position) {
        if (binding.recyclerViewPatients.getAdapter() != null) {
            if (position == null) {
                binding.recyclerViewPatients.getAdapter().notifyDataSetChanged();
            } else {
                binding.recyclerViewPatients.getAdapter().notifyItemChanged(position);
            }
        }
    }
}