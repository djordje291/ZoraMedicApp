package com.zoramedic.zoramedicapp.model;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.app.Activity;
import android.app.Application;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zoramedic.zoramedicapp.R;
import com.zoramedic.zoramedicapp.data.Bill;
import com.zoramedic.zoramedicapp.data.Message;
import com.zoramedic.zoramedicapp.data.Note;
import com.zoramedic.zoramedicapp.data.Patient;
import com.zoramedic.zoramedicapp.data.Pharmacy;
import com.zoramedic.zoramedicapp.data.User;
import com.zoramedic.zoramedicapp.data.UserMe;
import com.zoramedic.zoramedicapp.network.ApiClient;
import com.zoramedic.zoramedicapp.network.ApiService;
import com.zoramedic.zoramedicapp.view.HomeActivity;
import com.zoramedic.zoramedicapp.view.LoginActivity;
import com.zoramedic.zoramedicapp.view.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirebaseRepository {

    private Application application;
    private FirebaseAuth firebaseAuth;
    private MutableLiveData<FirebaseUser> userLiveData;
    private MutableLiveData<Boolean> loggedOutLiveData;

    private FirebaseFirestore db;
    private CollectionReference usersReference;
    List<User> users;
    MutableLiveData<List<User>> usersLiveList;

    private CollectionReference pharmacyReference;
    List<Pharmacy> pharmacies;
    MutableLiveData<List<Pharmacy>> pharmaciesLiveList;

    private CollectionReference patientReference;
    List<Patient> patients;
    MutableLiveData<List<Patient>> patientsLiveList;

    private CollectionReference noteReference;
    List<Note> notes;
    MutableLiveData<List<Note>> notesLiveList;

    private List<Pharmacy> notificationLowPharmacies;

    private CollectionReference messageReference;
    List<Message> messages;
    MutableLiveData<List<Message>> messagesLiveList;

    private CollectionReference billReference;
    List<Bill> bills;
    MutableLiveData<List<Bill>> billsLiveList;

    private FirebaseMessaging firebaseMessaging;
    private StorageReference storageReference;

    private MutableLiveData<Patient> selectedPatientLive;
    private Patient selectedPatient;

    public FirebaseRepository(Application application) {
        this.application = application;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.userLiveData = new MutableLiveData<>();
        this.loggedOutLiveData = new MutableLiveData<>();

        if (firebaseAuth.getCurrentUser() != null) {
            userLiveData.postValue(firebaseAuth.getCurrentUser());
            loggedOutLiveData.postValue(false);
        }

        this.db = FirebaseFirestore.getInstance();
        usersReference = db.collection(Constants.KEY_USERS_COLLECTION);

        pharmacyReference = db.collection(Constants.KEY_PHARMACY_COLLECTION);
        this.pharmacies = new ArrayList<>();
        this.pharmaciesLiveList = new MutableLiveData<>();

        patientReference = db.collection(Constants.KEY_PATIENTS_COLLECTION);
        this.patients = new ArrayList<>();
        this.patientsLiveList = new MutableLiveData<>();

        noteReference = db.collection(Constants.KEY_NOTES_COLLECTION);
        this.notes = new ArrayList<>();
        this.notesLiveList = new MutableLiveData<>();

        this.users = new ArrayList<>();
        this.usersLiveList = new MutableLiveData<>();

        this.notificationLowPharmacies = new ArrayList<>();

        this.messageReference = db.collection(Constants.KEY_MESSAGES_COLLECTION);
        this.messages = new ArrayList<>();
        this.messagesLiveList = new MutableLiveData<>();

        this.billReference = db.collection(Constants.KEY_BILLS_COLLECTION);
        this.bills = new ArrayList<>();
        this.billsLiveList = new MutableLiveData<>();

        this.firebaseMessaging = FirebaseMessaging.getInstance();
        this.storageReference = FirebaseStorage.getInstance().getReference();

        this.selectedPatientLive = new MutableLiveData<>();
        this.selectedPatient = new Patient();
    }

    public void login(String email, String password, LoginActivity a) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(application.getMainExecutor(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                userLiveData.postValue(firebaseAuth.getCurrentUser());
                            } else {
                                Toast.makeText(application.getApplicationContext(), application.getString(R.string.login_failed) + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                a.loading(false);
                            }
                        }
                    });
        }
    }

    public void logOut() {
        deleteToken();
    }

    private void deleteToken() {
        usersReference.document(UserMe.getInstance().getDocRef()).update(Constants.KEY_FCM_TOKEN, null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                firebaseAuth.signOut();
                loggedOutLiveData.postValue(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(application, R.string.failed_to_logout, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        usersReference.document(UserMe.getInstance().getDocRef()).update(Constants.KEY_FCM_TOKEN, token).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                UserMe.getInstance().setFcmToken(token);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    public void setUserClassAndIntent(String id, Activity activity, boolean b) {
        usersReference.whereEqualTo(Constants.KEY_USER_FIREBASE_ID, id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        UserMe u = UserMe.getInstance();
                        u.setFirstName(snapshot.getString(Constants.KEY_FIRST_NAME));
                        u.setLastName(snapshot.getString(Constants.KEY_LAST_NAME));
                        u.setClearanceLvl(snapshot.getLong(Constants.KEY_CLEARANCE_LVL).intValue());
                        u.setDocRef(snapshot.getId());
                        u.setFcmToken(snapshot.getString(Constants.KEY_FCM_TOKEN));
                    }
                    if (b) {
                        getToken();
                    }
                }
                Intent intent = new Intent(activity, HomeActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        });
    }

    public MutableLiveData<List<Pharmacy>> getPharmacyList() {

        if (pharmacies.size() == 0) {
            loadPharmacyList();
        }

        pharmaciesLiveList.setValue(pharmacies);

        return pharmaciesLiveList;
    }

    public MutableLiveData<List<Patient>> getPatientList() {

        if (patients.size() == 0) {
            loadPatientList();
        }

        patientsLiveList.setValue(patients);

        return patientsLiveList;
    }

    public MutableLiveData<Patient> getPatient(String docRef) {
        if (selectedPatient.getName() == null) {
            loadPatient(docRef);
        }

        selectedPatientLive.setValue(selectedPatient);

        return selectedPatientLive;
    }

    public MutableLiveData<List<Note>> getNoteList() {

        if (notes.size() == 0) {
            loadNoteList();
        }

        notesLiveList.setValue(notes);

        return notesLiveList;
    }

    public MutableLiveData<List<Message>> getMessageList() {

        if (messages.size() == 0) {
            loadMessageList();
        }

        messagesLiveList.setValue(messages);

        return messagesLiveList;
    }

    public void getMoreMessages(Timestamp ts) {
        loadMoreMessages(ts);
    }

    public MutableLiveData<List<Bill>> getBillList() {

        if (bills.size() == 0) {
            loadBillList();
        }

        billsLiveList.setValue(bills);

        return billsLiveList;
    }

    public MutableLiveData<List<User>> getUserList() {

        if (users.size() == 0) {
            loadUserList();
        }

        usersLiveList.setValue(users);

        return usersLiveList;
    }

    private void loadUserList() {
        usersReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

                if (value != null && !value.isEmpty()) {
                    if (!users.isEmpty()) {
                        users.clear();
                    }
                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                        User user = documentSnapshot.toObject(User.class);
                        users.add(user);
                    }
                    usersLiveList.postValue(users);
                }
            }
        });
    }

    private void loadPharmacyList() {
        //TODO: order by has low than alphabeticaly wont work
        String s;
        Query.Direction o;
        if (UserMe.getInstance().getClearanceLvl() < 1) {
            s = Constants.KEY_NAME;
            o = Query.Direction.ASCENDING;
        } else {
            s = Constants.KEY_HAS_LOW;
            o = Query.Direction.DESCENDING;
        }
        pharmacyReference.orderBy(Constants.KEY_NAME, Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

                if (value != null && !value.isEmpty()) {
                    if (!pharmacies.isEmpty()) {
                        pharmacies.clear();
                    }
                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                        Pharmacy pharmacy = documentSnapshot.toObject(Pharmacy.class);
                        if (pharmacy != null) {
                            pharmacy.setDocRef(documentSnapshot.getId());
                        }

                        pharmacies.add(pharmacy);
                    }
                    pharmaciesLiveList.postValue(pharmacies);
                } else {
                    if (!pharmacies.isEmpty()) {
                        pharmacies.clear();
                    }
                    pharmaciesLiveList.postValue(pharmacies);
                }
            }
        });
    }

    private void loadPatientList() {

        patientReference.orderBy(Constants.KEY_NAME).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

                if (value != null && !value.isEmpty()) {
                    if (!patients.isEmpty()) {
                        patients.clear();
                    }
                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                        Patient patient = documentSnapshot.toObject(Patient.class);
                        patient.setDocRef(documentSnapshot.getId());
                        patients.add(patient);
                    }
                    patientsLiveList.postValue(patients);
                } else {
                    if (!patients.isEmpty()) {
                        patients.clear();
                    }
                    patientsLiveList.postValue(patients);
                }
            }
        });
    }

    private void loadPatient(String docRef) {
        patientReference.document(docRef).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

                if (value != null && value.exists()) {
                    selectedPatient = new Patient();
                    selectedPatient = value.toObject(Patient.class);
                    selectedPatient.setDocRef(value.getId());
                    selectedPatientLive.postValue(selectedPatient);
                }
            }
        });
    }

    private void loadNoteList() {
        noteReference.orderBy(Constants.KEY_DATE_AND_TIME, Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

                if (value != null && !value.isEmpty()) {
                    if (!notes.isEmpty()) {
                        notes.clear();
                    }
                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                        Note note = documentSnapshot.toObject(Note.class);
                        note.setDocRef(documentSnapshot.getId());
                        if (note.getCreatorID().equals(UserMe.getInstance().getUserFirebaseID())) {
                            notes.add(note);
                        } else {
                            for (User u : note.getReceiverList()) {
                                if (u.getUserFirebaseID().equals(UserMe.getInstance().getUserFirebaseID())) {
                                    notes.add(note);
                                }
                            }
                        }
                    }
                    notesLiveList.postValue(notes);
                } else {
                    if (!notes.isEmpty()) {
                        notes.clear();
                    }
                    notesLiveList.postValue(notes);
                }
            }
        });
    }

    private void loadMessageList() {
        messageReference.limit(Constants.MESSAGES_COUNT).orderBy(Constants.KEY_DATE_AND_TIME, Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

                if (value != null && !value.isEmpty()) {
                    if (!messages.isEmpty()) {
                        messages.clear();
                    }
                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                        Message message = documentSnapshot.toObject(Message.class);
                        message.setDocRef(documentSnapshot.getId());
                        messages.add(message);
                    }
                    reverseList(messages);
                    messagesLiveList.postValue(messages);
                } else {
                    if (!messages.isEmpty()) {
                        messages.clear();
                    }
                    messagesLiveList.postValue(messages);
                }

            }
        });
    }

    private void loadMoreMessages(Timestamp ts) {

        List<Message> tempList = new ArrayList<>();
        messageReference.limit(Constants.MORE_MESSAGES_COUNT).orderBy(Constants.KEY_DATE_AND_TIME, Query.Direction.DESCENDING).startAfter(ts).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    Message message = documentSnapshot.toObject(Message.class);
                    message.setDocRef(documentSnapshot.getId());
                    tempList.add(message);
                }
                reverseList(messages);
                messages.addAll(tempList);
                reverseList(messages);
                messagesLiveList.postValue(messages);
            }
        });
    }

    private void reverseList(List<Message> list) {
        if (list == null || list.size() <= 1)
            return;
        Message value = list.remove(0);
        reverseList(list);
        list.add(value);
    }

    private void loadBillList() {
        billReference.orderBy(Constants.KEY_TITLE).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

                if (value != null && !value.isEmpty()) {
                    if (!bills.isEmpty()) {
                        bills.clear();
                    }
                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                        Bill bill = documentSnapshot.toObject(Bill.class);
                        bill.setDocRef(documentSnapshot.getId());
                        bills.add(bill);
                    }
                    billsLiveList.postValue(bills);
                }
            }
        });
    }


    public MutableLiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    public MutableLiveData<Boolean> getLoggedOutLiveData() {
        return loggedOutLiveData;
    }

    public void setNote(Note note) {
        noteReference.add(note).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                note.setDocRef(documentReference.getId());
                try {
                    JSONArray tokens = new JSONArray();
                    for (User u : note.getReceiverList()) {
                        if (u.getFcmToken() != null && !u.getFcmToken().equals(UserMe.getInstance().getFcmToken())) {
                            tokens.put(u.getFcmToken());
                        }
                    }
                    if (tokens.length() > 0) {
                        JSONObject data = new JSONObject();
                        data.put(Constants.KEY_TYPE, Constants.KEY_NOTES);
                        data.put(Constants.KEY_TITLE, note.getTitle());
                        data.put(Constants.KEY_CREATOR_NAME, note.getCreatorName());
                        data.put(Constants.KEY_ID, getId(note.getDocRef()));


                        JSONObject body = new JSONObject();
                        body.put(Constants.REMOTE_MSG_DATA, data);
                        body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

                        sendNoteNotification(body.toString());
                    }
                } catch (JSONException exception) {

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(application, application.getString(R.string.not_added), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void setPharmacy(Pharmacy pharmacy) {
        pharmacy.setDocRef(null);
        pharmacyReference.document().set(pharmacy).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(application, application.getString(R.string.not_added), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setPatient(Patient patient, Uri fileUri) {
        patientReference.add(patient).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                patient.setDocRef(documentReference.getId());
                if (fileUri != null) {
                    uploadWordFile(fileUri, patient);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private void uploadWordFile(Uri fileUri, Patient patient) {
        String wordPath = patient.getName() + " " + application.getString(R.string.pocetni_word) + " " + Timestamp.now().getSeconds();

        storageReference.child(wordPath).putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Map<String, Object> updates = new HashMap<>();
                updates.put(Constants.KEY_WORD_PATH, wordPath);
                if (patient.getDocRef() != null) {
                    patientReference.document(patient.getDocRef()).update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(application, application.getString(R.string.not_added), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(application, application.getString(R.string.not_added_word_document), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getWordFile(Patient patient, Activity activity) {
        storageReference.child(patient.getWordPath()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String uri1 = uri.toString();
                downloadFile(activity, patient.getName(), application.getString(R.string.doc), DIRECTORY_DOWNLOADS, uri1);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String uri) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri1 = Uri.parse(uri);
        DownloadManager.Request request = new DownloadManager.Request(uri1);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);
        downloadManager.enqueue(request);
    }

    public void updatePharmacy(Pharmacy p) {
        Map<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_QUANTITY, p.getQuantity());
        updates.put(Constants.KEY_HAS_LOW, p.isHasLow());
        updates.put(Constants.KEY_MINIMAL_LIMIT, p.getMinimalLimit());
        updates.put(Constants.KEY_NAME, p.getName());
        updates.put(Constants.KEY_PRICE, p.getPrice());
        updates.put(Constants.KEY_PER_OS, p.isPerOs());

        getUserList();

        pharmacyReference.document(p.getDocRef()).update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if (p.isHasLow()) {
                    try {
                        JSONArray tokens = new JSONArray();
                        for (User u : users) {
                            if (u.getClearanceLvl() > 0) {
                                if (u.getFcmToken() != null && !u.getFcmToken().equals(UserMe.getInstance().getFcmToken())) {
                                    tokens.put(u.getFcmToken());
                                }
                            }
                        }
                        if (tokens.length() > 0) {
                            JSONObject data = new JSONObject();
                            data.put(Constants.KEY_TYPE, Constants.KEY_PHARMACY);
                            data.put(Constants.KEY_NAME, p.getName());
                            data.put(Constants.KEY_MINIMAL_LIMIT, p.getMinimalLimit());
                            data.put(Constants.KEY_QUANTITY, p.getQuantity());
                            data.put(Constants.KEY_ID, getId(p.getDocRef()));

                            JSONObject body = new JSONObject();
                            body.put(Constants.REMOTE_MSG_DATA, data);
                            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

                            sendPharmacyLowNotification(body.toString());
                        }
                    } catch (JSONException exception) {

                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(application, application.getString(R.string.not_updated), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateNote(Note n) {
        Map<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_SEEN_BY_LIST, n.getSeenByList());
        noteReference.document(n.getDocRef()).update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(application, application.getString(R.string.not_updated), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updatePatientWithUri(Patient patient, Uri fileUri, List<Pharmacy> pL) {
        Map<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_NAME, patient.getName());
        updates.put(Constants.KEY_BLOOD_TYPE, patient.getBloodType());
        updates.put(Constants.KEY_ROOM_NUMBER, patient.getRoomNumber());
        updates.put(Constants.KEY_WEIGHT, patient.getWeight());
        updates.put(Constants.KEY_HEIGHT, patient.getHeight());
        updates.put(Constants.KEY_ADDRESS, patient.getAddress());
        updates.put(Constants.KEY_PHONE_NUMBER, patient.getPhoneNumber());
        updates.put(Constants.KEY_BIRTHDAY, patient.getBirthday());
        updates.put(Constants.KEY_IS_MALE, patient.isMale());
        if (patient.getSaturation() != null) {
            updates.put(Constants.KEY_SATURATION, patient.getSaturation());
        }
        if (patient.getServices() != null) {
            updates.put(Constants.KEY_SERVICES, patient.getServices());
        }
        if (patient.getBloodPressureHistory() != null) {
            updates.put(Constants.KEY_BLOOD_PRESSURE_HISTORY, patient.getBloodPressureHistory());
        }
        patientReference.document(patient.getDocRef()).update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if (pL != null) {
                    for (Pharmacy p : pL) {
                        updatePharmacy(p);
                    }
                }
                if (fileUri != null) {
                    uploadWordFile(fileUri, patient);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(application, application.getString(R.string.not_added), Toast.LENGTH_SHORT).show();
            }
        });

    }

//    public void deleteNote(Note note) {
//        noteReference.whereEqualTo("dateAndTime", note.getDateAndTime()).get().continueWith(new Continuation<QuerySnapshot, Void>() {
//            @Override
//            public Void then(@NonNull Task<QuerySnapshot> task) throws Exception {
//                for (DocumentSnapshot documentSnapshot : task.getResult()) {
//                    noteReference.document(documentSnapshot.getId()).delete();
//                }
//                return null;
//            }
//        });
//    }

    public void deleteNote(Note note) {
        noteReference.document(note.getDocRef()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(application, application.getString(R.string.delete_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deletePharmacy(Pharmacy pharmacy) {
        pharmacyReference.document(pharmacy.getDocRef()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(application, application.getString(R.string.delete_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deletePatient(Patient patient) {
        patientReference.document(patient.getDocRef()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if (patient.getWordPath() != null) {
                    deleteWordFile(patient);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(application, application.getString(R.string.delete_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteWordFile(Patient patient) {
        storageReference.child(patient.getWordPath()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    public void updateUserAvailability(boolean b) {
        usersReference.document(UserMe.getInstance().getDocRef()).update(Constants.KEY_AVAILABILITY, b).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    public void setMessage(Message message) {
        getUserList();
        messageReference.document().set(message).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                try {
                    JSONArray tokens = new JSONArray();
                    for (User u : users) {
                        if (!u.getAvailability()) {
                            if (u.getFcmToken() != null && !u.getFcmToken().equals(UserMe.getInstance().getFcmToken())) {
                                tokens.put(u.getFcmToken());
                            }
                        }
                    }
                    if (tokens.length() > 0) {
                        JSONObject data = new JSONObject();
                        data.put(Constants.KEY_TYPE, Constants.KEY_MESSAGE);
                        data.put(Constants.KEY_NAME, UserMe.getInstance().getFirstName() + " " + UserMe.getInstance().getLastName());
                        data.put(Constants.KEY_MESSAGE, message.getMessage());

                        JSONObject body = new JSONObject();
                        body.put(Constants.REMOTE_MSG_DATA, data);
                        body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

                        sendChatNotification(body.toString());
                    }
                } catch (JSONException exception) {

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(application, R.string.message_failed_to_send, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendChatNotification(String messageBody) {
        ApiClient.getClient().create(ApiService.class).sendMessage(
                Constants.getRemoteMsgHeaders(),
                messageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    try {
                        if (response.body() != null) {
                            JSONObject responseJson = new JSONObject(response.body());
                            JSONArray results = responseJson.getJSONArray(Constants.KEY_RESULTS);
                            if (responseJson.getInt(Constants.KEY_FAILURE) == 1) {
                                JSONObject error = (JSONObject) results.get(0);
                                return;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(application, application.getString(R.string.failed_to_send_notification), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(application, application.getString(R.string.failed_to_send_notification), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendPharmacyLowNotification(String messageBody) {
        ApiClient.getClient().create(ApiService.class).sendMessage(
                        Constants.getRemoteMsgHeaders(),
                        messageBody)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            try {
                                if (response.body() != null) {
                                    JSONObject responseJson = new JSONObject(response.body());
                                    JSONArray results = responseJson.getJSONArray(Constants.KEY_RESULTS);
                                    if (responseJson.getInt(Constants.KEY_FAILURE) == 1) {
                                        JSONObject error = (JSONObject) results.get(0);
                                        return;
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(application, application.getString(R.string.failed_to_send_notification), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(application, application.getString(R.string.failed_to_send_notification), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendNoteNotification(String messageBody) {
        ApiClient.getClient().create(ApiService.class).sendMessage(
                        Constants.getRemoteMsgHeaders(),
                        messageBody)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            try {
                                if (response.body() != null) {
                                    JSONObject responseJson = new JSONObject(response.body());
                                    JSONArray results = responseJson.getJSONArray(Constants.KEY_RESULTS);
                                    if (responseJson.getInt(Constants.KEY_FAILURE) == 1) {
                                        JSONObject error = (JSONObject) results.get(0);
                                        return;
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(application, application.getString(R.string.failed_to_send_notification), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(application, application.getString(R.string.failed_to_send_notification), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private int getId(String s) {
        return Integer.parseInt(s.replaceAll("[^0-9]", ""));
    }
}
