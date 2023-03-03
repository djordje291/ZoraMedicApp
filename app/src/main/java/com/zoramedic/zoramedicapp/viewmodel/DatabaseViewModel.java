package com.zoramedic.zoramedicapp.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.Timestamp;
import com.zoramedic.zoramedicapp.data.Bill;
import com.zoramedic.zoramedicapp.data.Message;
import com.zoramedic.zoramedicapp.data.Note;
import com.zoramedic.zoramedicapp.data.Patient;
import com.zoramedic.zoramedicapp.data.Pharmacy;
import com.zoramedic.zoramedicapp.data.Service;
import com.zoramedic.zoramedicapp.data.User;
import com.zoramedic.zoramedicapp.model.FirebaseRepository;

import java.util.List;

public class DatabaseViewModel extends AndroidViewModel {

    private FirebaseRepository firebaseRepository;

    public DatabaseViewModel(@NonNull Application application) {
        super(application);

        firebaseRepository = new FirebaseRepository(application);
    }

    public MutableLiveData<List<Pharmacy>> getPharmacyList() {
        return firebaseRepository.getPharmacyList();
    }

    public MutableLiveData<List<Patient>> getPatientList() {
        return firebaseRepository.getPatientList();
    }

    public MutableLiveData<Patient> getPatient(String docRef) {
        return firebaseRepository.getPatient(docRef);
    }

    public MutableLiveData<List<Note>> getNoteList() {
        return firebaseRepository.getNoteList();
    }

    public MutableLiveData<List<Message>> getMessageList() {
        return firebaseRepository.getMessageList();
    }

    public MutableLiveData<List<Bill>> getBillList() {
        return firebaseRepository.getBillList();
    }

    public void getMoreMessages(Timestamp ts) {
        firebaseRepository.getMoreMessages(ts);
    }

    public MutableLiveData<List<User>> getUserList() {
        return firebaseRepository.getUserList();
    }

    public void setNote(Note note) {
        firebaseRepository.setNote(note);
    }

    public void setPharmacy(Pharmacy pharmacy) {
        firebaseRepository.setPharmacy(pharmacy);
    }

    public void setMessage(Message message) {
        firebaseRepository.setMessage(message);
    }

    public void setPatient(Patient patient, Uri fileUri) {
        firebaseRepository.setPatient(patient, fileUri);
    }

    public void updatePatientWithUri(Patient patient, Uri fileUri, List<Pharmacy> pL) {
        firebaseRepository.updatePatientWithUri(patient, fileUri, pL);
    }

    public void updatePharmacy(Pharmacy p) {
        firebaseRepository.updatePharmacy(p);
    }

    public void updateNote(Note n) {
        firebaseRepository.updateNote(n);
    }

    public void deleteNote(Note note) {
        firebaseRepository.deleteNote(note);
    }

    public void deletePharmacy(Pharmacy pharmacy) {
        firebaseRepository.deletePharmacy(pharmacy);
    }

    public void deletePatient(Patient patient) {
        firebaseRepository.deletePatient(patient);
    }

    public void updateUserAvailability(boolean b) {
        firebaseRepository.updateUserAvailability(b);
    }

    public void getWordFile(Patient patient, Activity activity) {
        firebaseRepository.getWordFile(patient, activity);
    }
}
