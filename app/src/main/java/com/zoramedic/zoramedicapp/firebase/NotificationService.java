package com.zoramedic.zoramedicapp.firebase;


import static com.zoramedic.zoramedicapp.App.CHAT_CHANNEL_ID;
import static com.zoramedic.zoramedicapp.App.NOTES_CHANNEL_ID;
import static com.zoramedic.zoramedicapp.App.PHARMACY_CHANNEL_ID;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.zoramedic.zoramedicapp.App;
import com.zoramedic.zoramedicapp.R;
import com.zoramedic.zoramedicapp.data.Pharmacy;
import com.zoramedic.zoramedicapp.data.UserMe;
import com.zoramedic.zoramedicapp.view.LoginActivity;
import com.zoramedic.zoramedicapp.view.MainActivity;
import com.zoramedic.zoramedicapp.view.chat.ChatActivity;
import com.zoramedic.zoramedicapp.view.notes.NotesActivity;
import com.zoramedic.zoramedicapp.view.pharmacy.PharmacyActivity;
import com.zoramedic.zoramedicapp.view.util.Constants;
import com.zoramedic.zoramedicapp.viewmodel.LoginViewModel;

import java.util.Objects;

public class NotificationService extends FirebaseMessagingService {

    Intent intent;
    PendingIntent pendingIntent;
    Notification notification;
    NotificationManager notifyMgr;

    int i = 0;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        //ne postoji nacin da ovo testiram
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            FirebaseFirestore.getInstance().collection(Constants.KEY_USERS_COLLECTION).whereEqualTo(Constants.KEY_USER_FIREBASE_ID, currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    FirebaseFirestore.getInstance().collection(Constants.KEY_USERS_COLLECTION).document(queryDocumentSnapshots.getDocuments().get(0).getId()).update(Constants.KEY_FCM_TOKEN, token);
                }
            });
        }

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        String type = message.getData().get(Constants.KEY_TYPE);
        intent = new Intent(this, MainActivity.class);
//            int id = (Integer.parseInt(message.getData().get(Constants.KEY_ID)));
        if (type != null) {
            switch (type) {
                case Constants.KEY_MESSAGE:
//                    intent = new Intent(this, ChatActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

                    notification = new NotificationCompat.Builder(this, CHAT_CHANNEL_ID)
                            .setContentTitle(message.getData().get(Constants.KEY_NAME))
                            .setContentText(message.getData().get(Constants.KEY_MESSAGE))
                            .setSmallIcon(R.drawable.ic_chat)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .build();

                    notifyMgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                    notifyMgr.notify(i++, notification);
                    break;
                case Constants.KEY_PHARMACY:
//                    intent = new Intent(this, PharmacyActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

                    notification = new NotificationCompat.Builder(this, PHARMACY_CHANNEL_ID)
                            .setContentTitle(message.getData().get(Constants.KEY_NAME))
                            .setContentText(getString(R.string.minimal_is) + " " + message.getData().get(Constants.KEY_MINIMAL_LIMIT) + ",\n" + getString(R.string.in_stock) + " " + message.getData().get(Constants.KEY_QUANTITY))
                            .setSmallIcon(R.drawable.ic_pharmacy)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .build();

                    notifyMgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                    notifyMgr.notify(Integer.parseInt(message.getData().get(Constants.KEY_ID)), notification);
                    break;
                case Constants.KEY_NOTES:
//                    intent = new Intent(this, NotesActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

                    notification = new NotificationCompat.Builder(this, NOTES_CHANNEL_ID)
                            .setContentTitle(message.getData().get(Constants.KEY_CREATOR_NAME))
                            .setContentText(message.getData().get(Constants.KEY_TITLE))
                            .setSmallIcon(R.drawable.ic_board)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .build();

                    notifyMgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                    notifyMgr.notify(Integer.parseInt(message.getData().get(Constants.KEY_ID)), notification);
                    break;
            }
        }
    }
}
