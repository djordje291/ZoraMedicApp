package com.zoramedic.zoramedicapp.view.chat;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.zoramedic.zoramedicapp.R;
import com.zoramedic.zoramedicapp.data.User;
import com.zoramedic.zoramedicapp.view.BaseActivity;
import com.zoramedic.zoramedicapp.data.Message;
import com.zoramedic.zoramedicapp.data.UserMe;
import com.zoramedic.zoramedicapp.databinding.ActivityChatBinding;
import com.zoramedic.zoramedicapp.view.util.Constants;
import com.zoramedic.zoramedicapp.view.util.Sleeper;
import com.zoramedic.zoramedicapp.viewmodel.DatabaseViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends BaseActivity {

    private ActivityChatBinding binding;
    private List<Message> messageList = new ArrayList<>();
    private DatabaseViewModel databaseViewModel;

    private Context context;
    private ChatAdapter chatAdapter;

    private UserMe userMe;

    private LinearLayoutManager layoutManager;

    private boolean isMoreMessages = false;
    private int previousListSize;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = getApplicationContext();
        userMe = UserMe.getInstance();

        layoutManager = new LinearLayoutManager(context);

        loading(true);

        databaseViewModel = new ViewModelProvider(this).get(DatabaseViewModel.class);

        getMessages();
        setListeners();
    }

    private void setListeners() {
        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.inputMessage.getText().toString().trim().isEmpty()) {
                    Message m = new Message();
                    m.setMessage(binding.inputMessage.getText().toString().trim());
                    m.setCreatorID(userMe.getUserFirebaseID());
                    m.setCreatorName(userMe.getFirstName() + " " + userMe.getLastName());
                    m.setDateAndTime(Timestamp.now());
                    databaseViewModel.setMessage(m);
                    binding.inputMessage.getText().clear();
                    chatAdapter.notifyDataSetChanged();
                }
            }
        });


        binding.recyclerViewChat.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                    if (messageList.size() >= Constants.MESSAGES_COUNT) {
                        isMoreMessages = true;
                        previousListSize = messageList.size();
                        databaseViewModel.getMoreMessages(messageList.get(0).getDateAndTime());
                    }
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void getMessages() {
        databaseViewModel.getMessageList().observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                if (!messages.isEmpty()) {
                    if (!messageList.isEmpty()) {
                        messageList.clear();
                    }
                    binding.emptyChat.setVisibility(View.GONE);
                    messageList.addAll(messages);

                    binding.recyclerViewChat.setLayoutManager(layoutManager);

                    chatAdapter = new ChatAdapter(context, messageList, databaseViewModel, userMe);

                    binding.recyclerViewChat.setAdapter(chatAdapter);
                    loading(false);
                    chatAdapter.notifyDataSetChanged();

                    if (!isMoreMessages) {
                        isMoreMessages = false;
                        if (binding.recyclerViewChat.getAdapter() != null) {
                            binding.recyclerViewChat.scrollToPosition(binding.recyclerViewChat.getAdapter().getItemCount() - 1);
                        }
                    } else {
                        if (binding.recyclerViewChat.getAdapter() != null) {
                            if (messageList.size() - previousListSize != 0) {
                                binding.recyclerViewChat.scrollToPosition(messageList.size() - previousListSize);
                            } else {
                                binding.recyclerViewChat.scrollToPosition(1);
                            }
                        }
                    }

                } else {
                    messageList.clear();
                    new Sleeper(binding.emptyChat, binding.loading, messages, messageList).start();
                    if (binding.recyclerViewChat.getAdapter() != null) {
                        binding.recyclerViewChat.getAdapter().notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        observeOnline();
    }

    private void observeOnline() {
        databaseViewModel.getUserList().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                if (!users.isEmpty()) {
                    StringBuilder builder = new StringBuilder(getString(R.string.in_chat));
                    int i = 0;
                    for (User u : users) {
                        if (u.getAvailability()) {
                            if (!u.getUserFirebaseID().equals(UserMe.getInstance().getUserFirebaseID())) {
                                i++;
                                builder.append(" ");
                                builder.append(u.getFirstName());
                                builder.append(" ");
                                builder.append(u.getLastName());
                                builder.append(",");
                            }

                        }
                    }
                    builder.deleteCharAt(builder.length() - 1);
                    if (i > 0) {
                        binding.online.setVisibility(View.VISIBLE);
                        binding.online.setText(builder);
                    } else {
                        binding.online.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void loading(boolean b) {
        if (b) {
            binding.loading.setVisibility(View.VISIBLE);
        } else {
            binding.loading.setVisibility(View.GONE);
        }
    }
}