package com.zoramedic.zoramedicapp.view.chat;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.zoramedic.zoramedicapp.R;
import com.zoramedic.zoramedicapp.data.Message;
import com.zoramedic.zoramedicapp.data.UserMe;
import com.zoramedic.zoramedicapp.databinding.ActivityChatCardReceivedBinding;
import com.zoramedic.zoramedicapp.databinding.ActivityChatCardSentBinding;
import com.zoramedic.zoramedicapp.viewmodel.DatabaseViewModel;

import java.util.List;


public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


    private Context context;
    private List<Message> messageList;
    private DatabaseViewModel databaseViewModel;
    private UserMe user;

    public ChatAdapter(Context context, List<Message> messageList, DatabaseViewModel databaseViewModel, UserMe user) {
        this.context = context;
        this.messageList = messageList;
        this.databaseViewModel = databaseViewModel;
        this.user = user;
    }

    @Override
    public int getItemViewType(int position) {
        if (!messageList.get(position).getCreatorID().equals(user.getUserFirebaseID())) {
            return 0;
        } else {
            return 1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                ActivityChatCardReceivedBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.activity_chat_card_received, parent, false);
                return new ViewHolderReceived(binding);
            case 1:
                ActivityChatCardSentBinding binding2 = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.activity_chat_card_sent, parent, false);
                return new ViewHolderSent(binding2);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        switch (holder.getItemViewType()) {
            case 0:
                ViewHolderReceived viewHolderReceived = (ViewHolderReceived) holder;
                viewHolderReceived.bind(message, checkDate(position), checkUser(position));
                break;
            case 1:
                ViewHolderSent viewHolderSent = (ViewHolderSent) holder;
                viewHolderSent.bind(message, checkDate(position));
                break;
            default:
                break;
        }

    }

    private boolean checkDate(int position) {
        if (position > 0) {
            return messageList.get(position).convertDate().equals(messageList.get(position - 1).convertDate());
        } else {
            return false;
        }
    }

    private boolean checkUser(int position) {
        if (position > 0) {
            return messageList.get(position).getCreatorID().equals(messageList.get(position - 1).getCreatorID());
        } else {
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolderReceived extends RecyclerView.ViewHolder {

        public ActivityChatCardReceivedBinding binding;

        public ViewHolderReceived(ActivityChatCardReceivedBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Message message, boolean b, boolean user) {
            binding.setMessage(message);
            binding.setB(b);
            binding.setUser(user);
        }
    }

    public class ViewHolderSent extends RecyclerView.ViewHolder {

        public ActivityChatCardSentBinding binding;


        public ViewHolderSent(ActivityChatCardSentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Message message, boolean b) {
            binding.setMessage(message);
            binding.setB(b);
        }
    }

}
