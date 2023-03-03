package com.zoramedic.zoramedicapp.data;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcelable;

import androidx.databinding.BindingAdapter;

import com.google.firebase.Timestamp;
import com.zoramedic.zoramedicapp.App;
import com.zoramedic.zoramedicapp.R;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class Note {

    private String title;
    private List<User> receiverList;
    private List<User> seenByList;
    private String creatorName;
    private String creatorID;
    private Timestamp dateAndTime;
    private String docRef;

    //for firestore
    public Note() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<User> getReceiverList() {
        return receiverList;
    }

    public void setReceiverList(List<User> receiverList) {
        this.receiverList = receiverList;
    }

    public List<User> getSeenByList() {
        return seenByList;
    }

    public void setSeenByList(List<User> seenByList) {
        this.seenByList = seenByList;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public Timestamp getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(Timestamp dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getDocRef() {
        return docRef;
    }

    public void setDocRef(String docRef) {
        this.docRef = docRef;
    }

    public String convertSeenBy() {
        StringBuilder sb = new StringBuilder();
        if (seenByList != null && !seenByList.isEmpty()) {
            sb = new StringBuilder(App.getContext().getString(R.string.seen_by));
            sb.append(" ");
            for (User u : seenByList) {
                sb.append(u.getFirstName());
                sb.append(" ");
                sb.append(u.getLastName());
                sb.append(", ");
            }
            sb.delete(sb.length() - 2, sb.length());
        } else {
        }
        return sb.toString();
    }

    public String convertTimeAndDate() {
        StringBuilder sb = new StringBuilder();
        Context context = App.getContext();
        if (dateAndTime != null) {
            long old = dateAndTime.getSeconds();
            long now = Timestamp.now().getSeconds();
            long time = now - old;
            if (!Locale.getDefault().getLanguage().equals("en")) {
                sb.append(context.getString(R.string.ago));
                sb.append(" ");
            }
            if (time < 60) {
                sb.append(time);
                sb.append(" ");
                sb.append(context.getString(R.string.seconds));
            } else if (time < 120) {
                sb.append(time / 60);
                sb.append(" ");
                sb.append(context.getString(R.string.minute));
            } else if (time < 3600) {
                sb.append(time / 60);
                sb.append(" ");
                sb.append(context.getString(R.string.minutes));
            } else if (time < 7200) {
                sb.append(time / 3600);
                sb.append(" ");
                sb.append(context.getString(R.string.hour));
            } else if (time < 216000) {
                sb.append(time / 3600);
                sb.append(" ");
                sb.append(context.getString(R.string.hours));
            } else if (time < 432000) {
                sb.append(time / 216000);
                sb.append(" ");
                sb.append(context.getString(R.string.day));
            } else {
                sb.append(time / 216000);
                sb.append(" ");
                sb.append(context.getString(R.string.days));
            }
            if (Locale.getDefault().getLanguage().equals("en")) {
                sb.append(" ");
                sb.append(context.getString(R.string.ago));
            }
        }
        return sb.toString();
    }


}
