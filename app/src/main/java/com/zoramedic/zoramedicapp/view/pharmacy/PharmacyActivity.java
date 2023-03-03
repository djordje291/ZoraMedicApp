package com.zoramedic.zoramedicapp.view.pharmacy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.zoramedic.zoramedicapp.R;
import com.zoramedic.zoramedicapp.ui.ItemTouchHelperEdit;
import com.zoramedic.zoramedicapp.data.Pharmacy;
import com.zoramedic.zoramedicapp.ui.SwipeToDeleteAndEditCallback;
import com.zoramedic.zoramedicapp.data.UserMe;
import com.zoramedic.zoramedicapp.databinding.ActivityPharmacyBinding;
import com.zoramedic.zoramedicapp.view.InternetActivity;
import com.zoramedic.zoramedicapp.view.util.Constants;
import com.zoramedic.zoramedicapp.view.util.Sleeper;
import com.zoramedic.zoramedicapp.viewmodel.DatabaseViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PharmacyActivity extends InternetActivity implements SearchView.OnQueryTextListener, ItemTouchHelperEdit {

    private ActivityPharmacyBinding binding;
    private DatabaseViewModel databaseViewModel;

    private List<Pharmacy> pharmacyList = new ArrayList<>();

    private PharmacyAdapter pharmacyAdapter;

    private MenuItem search;
    private SearchView searchView;

    private List<Pharmacy> tempList = new ArrayList<>();

    private Context context;

    ItemTouchHelper.Callback callback;
    ItemTouchHelper itemTouchHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPharmacyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = getApplicationContext();
        binding.setUser(UserMe.getInstance());

        loading(true);

        databaseViewModel = new ViewModelProvider(this).get(DatabaseViewModel.class);

        databaseViewModel.getPharmacyList().observe(this, new Observer<List<Pharmacy>>() {
            @Override
            public void onChanged(List<Pharmacy> pharmacies) {
                if (!pharmacies.isEmpty()) {
                    if (!pharmacyList.isEmpty()) {
                        pharmacyList.clear();
                    }
                    binding.emptyPharmacy.setVisibility(View.GONE);
                    pharmacyList.addAll(pharmacies);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                    binding.recyclerViewPharmacy.setLayoutManager(layoutManager);

                    pharmacyAdapter = new PharmacyAdapter(context, pharmacyList, databaseViewModel, binding.conView);
                    pharmacyAdapter.saveFullPharmacyList();

                    if (UserMe.getInstance().getClearanceLvl() >= 1) {
                        callback = new SwipeToDeleteAndEditCallback(context, pharmacyAdapter, PharmacyActivity.this, null);
                        itemTouchHelper = new ItemTouchHelper(callback);
                        itemTouchHelper.attachToRecyclerView(binding.recyclerViewPharmacy);
                    }

                    binding.recyclerViewPharmacy.setAdapter(pharmacyAdapter);
                    loading(false);
                    pharmacyAdapter.notifyDataSetChanged();
                } else {
                    pharmacyList.clear();
                    new Sleeper(binding.emptyPharmacy, binding.loading, pharmacies, pharmacyList).start();
                    refreshAdapter(null);
                }
            }
        });

        binding.fabPharmacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PharmacyDialog dialog = new PharmacyDialog(databaseViewModel, null, context);
                dialog.show(getSupportFragmentManager(), Constants.KEY_PHARMACY);
            }
        });
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
            pharmacyAdapter.returnFullPharmacyList();
            refreshAdapter(null);
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (query != null) {
            searchPharmacy(query);
        } else {
            pharmacyAdapter.returnFullPharmacyList();
            refreshAdapter(null);
        }
        return true;
    }

    private void searchPharmacy(String typedIn) {
        tempList.clear();
        for (Pharmacy pharmacy : pharmacyList) {
            if (pharmacy.getName().toLowerCase(Locale.ROOT).contains(typedIn.toLowerCase(Locale.ROOT))) {
                tempList.add(pharmacy);
            }
        }
        pharmacyAdapter.setPharmacyList(tempList);
        refreshAdapter(null);
    }

    @Override
    public void onItemEdit(int position) {
        refreshAdapter(position);
        PharmacyDialog dialog = new PharmacyDialog(databaseViewModel, pharmacyList.get(position), context);
        dialog.show(getSupportFragmentManager(), Constants.KEY_PHARMACY);
    }

    private void refreshAdapter(Integer position) {
        if (binding.recyclerViewPharmacy.getAdapter() != null) {
            if (position == null) {
                binding.recyclerViewPharmacy.getAdapter().notifyDataSetChanged();
            } else {
                binding.recyclerViewPharmacy.getAdapter().notifyItemChanged(position);
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
    protected void onResume() {
        super.onResume();
        refreshAdapter(null);
    }
}
