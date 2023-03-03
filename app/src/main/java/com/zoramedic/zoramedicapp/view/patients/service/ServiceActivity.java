package com.zoramedic.zoramedicapp.view.patients.service;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.zoramedic.zoramedicapp.R;
import com.zoramedic.zoramedicapp.data.Bill;
import com.zoramedic.zoramedicapp.data.Patient;
import com.zoramedic.zoramedicapp.data.Pharmacy;
import com.zoramedic.zoramedicapp.data.Service;
import com.zoramedic.zoramedicapp.data.UserMe;
import com.zoramedic.zoramedicapp.databinding.ActivityServiceBinding;
import com.zoramedic.zoramedicapp.ui.ItemTouchHelperDelete;
import com.zoramedic.zoramedicapp.ui.SwipeToDeleteAndEditCallback;
import com.zoramedic.zoramedicapp.view.InternetActivity;
import com.zoramedic.zoramedicapp.view.patients.SelectedPatientActivity;
import com.zoramedic.zoramedicapp.view.util.Constants;
import com.zoramedic.zoramedicapp.viewmodel.DatabaseViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ServiceActivity extends InternetActivity implements View.OnClickListener, SearchView.OnQueryTextListener, ItemTouchHelperDelete {

    private DatabaseViewModel databaseViewModel;
    private ActivityServiceBinding binding;
    private List<Bill> billList = new ArrayList<>();
    private Context context;
    private BillAdapter billAdapter;
    private PharmacyAddedAdapter pharmacyAddedAdapter;
    private PharmacyListAdapter pharmacyListAdapter;
    private List<Pharmacy> pharmacyList = new ArrayList<>();
    private Bill selectedBill = new Bill();
    private Patient patient;
    private List<Pharmacy> addedPharmacyList = new ArrayList<>();

    private MenuItem search;
    private SearchView searchView;
    private List<Pharmacy> tempListPharmacy = new ArrayList<>();
    private List<Bill> tempListBill = new ArrayList<>();

    private Service service;
    private boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = ServiceActivity.this;

        patient = (Patient) getIntent().getSerializableExtra(Constants.KEY_PATIENT);

        databaseViewModel = new ViewModelProvider(this).get(DatabaseViewModel.class);

        pharmacyAddedAdapter = new PharmacyAddedAdapter(context, addedPharmacyList);


        loading(true);


        databaseViewModel.getBillList().observe(this, new Observer<List<Bill>>() {
            @Override
            public void onChanged(List<Bill> bills) {
                if (!bills.isEmpty()) {
                    if (!billList.isEmpty()) {
                        billList.clear();
                    }
                    billList.addAll(bills);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(context);

                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(ServiceActivity.this,
                            layoutManager.getOrientation());
                    dividerItemDecoration.setDrawable(getDrawable(R.drawable.divider));
                    binding.billRV.addItemDecoration(dividerItemDecoration);

                    binding.billRV.setLayoutManager(layoutManager);
                    billAdapter = new BillAdapter(context, billList);
                    billAdapter.saveFullBillList();
                    binding.billRV.setAdapter(billAdapter);
                    loading(false);
                    billAdapter.notifyDataSetChanged();
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
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context);

                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(ServiceActivity.this,
                            layoutManager.getOrientation());
                    dividerItemDecoration.setDrawable(getDrawable(R.drawable.divider));
                    binding.pharmacyRV.addItemDecoration(dividerItemDecoration);

                    binding.pharmacyRV.setLayoutManager(layoutManager);
                    pharmacyListAdapter = new PharmacyListAdapter(context, pharmacyList);
                    binding.pharmacyRV.setAdapter(pharmacyListAdapter);
                    pharmacyListAdapter.notifyDataSetChanged();

                    if (isEdit) {
                        clearPharmacyList();
                    }
                } else {
                    pharmacyList.clear();
                    if (binding.pharmacyRV.getAdapter() != null) {
                        binding.pharmacyRV.getAdapter().notifyDataSetChanged();
                    }
                }
            }
        });

        setView(false, true, false);

        binding.selectedBill.setOnClickListener(this);
        binding.addPharmacy.setOnClickListener(this);
        binding.addService.setOnClickListener(this);

        service = (Service) getIntent().getSerializableExtra(Constants.KEY_SERVICE);
        if (service != null) {
            isEdit = true;
            setBill(service.getBill());
            if (service.getPharmacyList() != null && service.getPharmacyList().size() > 0) {
                for (Pharmacy p : service.getPharmacyList()) {
                    setPharmacy(p);
                }
            }
        }
    }



    public void setBill(Bill bill) {
        binding.setBill(bill);
        selectedBill = bill;
        setView(true, false, false);
        binding.executePendingBindings();
    }

    public void setPharmacy(Pharmacy pharmacy) {
        binding.pharmacyList.setVisibility(View.VISIBLE);
        binding.pharmacyRV.setVisibility(View.GONE);
        setView(true, false, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        binding.pharmacyList.setLayoutManager(layoutManager);
        addedPharmacyList.add(pharmacy);
        if (isEdit) {
            pharmacyList.removeIf(p -> p.getDocRef().equals(pharmacy.getDocRef()));
        } else {
            pharmacyList.remove(pharmacy);
        }
        if (pharmacyListAdapter != null) {
            pharmacyListAdapter.notifyDataSetChanged();
        }

        ItemTouchHelper.Callback callback = new SwipeToDeleteAndEditCallback(context, ServiceActivity.this, null, null);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(binding.pharmacyList);

        binding.pharmacyList.setAdapter(pharmacyAddedAdapter);
        pharmacyAddedAdapter.notifyDataSetChanged();
    }

    private void setView(boolean a, boolean bill, boolean pharmacy) {
        if (a) {
            binding.selectedBill.setVisibility(View.VISIBLE);
            binding.description.setVisibility(View.VISIBLE);
            binding.addPharmacy.setVisibility(View.VISIBLE);
            binding.addService.setVisibility(View.VISIBLE);
            binding.pharmacyList.setVisibility(View.VISIBLE);
            if (search != null) {
                search.setVisible(false);
                searchView.clearFocus();
            }
        } else {
            binding.selectedBill.setVisibility(View.GONE);
            binding.description.setVisibility(View.GONE);
            binding.addPharmacy.setVisibility(View.GONE);
            binding.addService.setVisibility(View.GONE);
            binding.pharmacyList.setVisibility(View.GONE);
            if (search != null) {
                search.setVisible(true);
                searchView.clearFocus();
            }
        }
        if (bill) {
            binding.billRV.setVisibility(View.VISIBLE);
        } else {
            binding.billRV.setVisibility(View.GONE);
        }
        if (pharmacy) {
            binding.pharmacyRV.setVisibility(View.VISIBLE);
        } else {
            binding.pharmacyRV.setVisibility(View.GONE);
        }
    }

    private void addServiceAndIntent() {
        if (isEdit) {
            if (checkAddedPharmacyList()) {
                if (selectedBill != null) {
                    service.setBill(selectedBill);
                    if (!binding.description.getText().toString().trim().equals("")) {
                        service.setDescription(binding.description.getText().toString().trim());
                    }
                    if (addedPharmacyList != null && addedPharmacyList.size() != 0) {
                        setQuantityForPharmacies();
                        service.setPharmacyList(addedPharmacyList);
                    }
                    for (Service s : patient.getServices()) {
                        if (s.getTimestamp().equals(service.getTimestamp())) {
                            patient.getServices().set(patient.getServices().indexOf(s), service);
                        }
                    }
                    databaseViewModel.updatePatientWithUri(patient, null, null);

                    Intent intent = new Intent(ServiceActivity.this, SelectedPatientActivity.class);
                    intent.putExtra(Constants.KEY_PATIENT, patient);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(context, R.string.there_is_at_least_one_pharmacy_with_0, Toast.LENGTH_SHORT).show();
            }
        } else {
            if (checkAddedPharmacyList()) {
                if (selectedBill != null) {
                    Service service = new Service();
                    service.setBill(selectedBill);
                    service.setCreatorID(UserMe.getInstance().getUserFirebaseID());
                    if (!binding.description.getText().toString().trim().equals("")) {
                        service.setDescription(binding.description.getText().toString().trim());
                    }
                    if (addedPharmacyList != null && addedPharmacyList.size() != 0) {
                        setQuantityForPharmacies();
                        service.setPharmacyList(addedPharmacyList);
                    }
                    service.setTimestamp(new Date());
                    patient.getServices().add(service);
                    databaseViewModel.updatePatientWithUri(patient, null, addedPharmacyList);

                    Intent intent = new Intent(ServiceActivity.this, SelectedPatientActivity.class);
                    intent.putExtra(Constants.KEY_PATIENT, patient);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(context, R.string.there_is_at_least_one_pharmacy_with_0, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setQuantityForPharmacies() {
        for (Pharmacy p : addedPharmacyList) {
//            if (p.getUsedForService() == 0) {
//                addedPharmacyList.remove(p);
//                continue;
//            }
            p.setQuantity(p.getQuantity() - p.getUsed());
            if (p.getQuantity() <= p.getMinimalLimit()) {
                p.setHasLow(true);
            }
        }
    }

    private boolean checkAddedPharmacyList() {
        for (Pharmacy p : addedPharmacyList) {
            if (p.getUsed() == 0) {
                return false;
            }
        }
        return true;
    }

    private void clearPharmacyList() {
        for (Pharmacy p : service.getPharmacyList()) {
            pharmacyList.removeIf(pp -> pp.getDocRef().equals(p.getDocRef()));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.selectedBill:
                setView(false, true, false);
                break;
            case R.id.addPharmacy:
                setView(false, false, true);
                break;
            case R.id.addService:
                addServiceAndIntent();
                break;
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
            search(query);
        } else {
            if (binding.billRV.getVisibility() == View.VISIBLE) {
                billAdapter.returnFullBillList();
            }
            if (binding.pharmacyRV.getVisibility() == View.VISIBLE) {
                pharmacyListAdapter.returnFullPharmacyList();
            }
            refreshAdapter();
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (query != null) {
            search(query);
        } else {
            if (binding.billRV.getVisibility() == View.VISIBLE) {
                billAdapter.returnFullBillList();
            }
            if (binding.pharmacyRV.getVisibility() == View.VISIBLE) {
                pharmacyListAdapter.returnFullPharmacyList();
            }
            refreshAdapter();
        }
        return true;
    }

    private void search(String typedIn) {
        tempListBill.clear();
        tempListPharmacy.clear();
        if (binding.billRV.getVisibility() == View.VISIBLE) {
            for (Bill bill : billList) {
                if (bill.getTitle().toLowerCase(Locale.ROOT).contains(typedIn.toLowerCase(Locale.ROOT))) {
                    tempListBill.add(bill);
                }
            }
            billAdapter.setBillList(tempListBill);
            refreshAdapter();
        }
        if (binding.pharmacyRV.getVisibility() == View.VISIBLE) {
            for (Pharmacy pharmacy : pharmacyList) {
                if (pharmacy.getName().toLowerCase(Locale.ROOT).contains(typedIn.toLowerCase(Locale.ROOT))) {
                    tempListPharmacy.add(pharmacy);
                }
            }
            pharmacyListAdapter.setPharmacyList(tempListPharmacy);
            refreshAdapter();
        }
    }

    private void refreshAdapter() {
        if (binding.billRV.getVisibility() == View.VISIBLE) {
            if (binding.billRV.getAdapter() != null) {
                binding.billRV.getAdapter().notifyDataSetChanged();
            }
        }
        if (binding.pharmacyRV.getVisibility() == View.VISIBLE) {
            if (binding.pharmacyRV.getAdapter() != null) {
                binding.pharmacyRV.getAdapter().notifyDataSetChanged();
            }
        }
    }

    private void loading(boolean b) {
        if (b) {
            binding.loading.setVisibility(View.VISIBLE);
        } else {
            binding.loading.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemDelete(int position) {
        Pharmacy p = addedPharmacyList.get(position);
        p.setUsed(0);
        pharmacyList.add(p);
        addedPharmacyList.remove(position);
        if (binding.pharmacyList.getAdapter() != null) {
            binding.pharmacyList.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.pharmacyRV.getVisibility() == View.VISIBLE) {
            setView(true, false, false);
        } else if (binding.billRV.getVisibility() == View.VISIBLE && selectedBill.getTitle() != null) {
            setView(true, false, false);
        } else {
            super.onBackPressed();
        }
    }
}