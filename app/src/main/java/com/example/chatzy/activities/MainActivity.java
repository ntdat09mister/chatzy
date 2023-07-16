package com.example.chatzy.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.chatzy.adapters.RecentConversationAdapter;
import com.example.chatzy.databinding.ActivityMainBinding;
import com.example.chatzy.listeners.ConversionListener;
import com.example.chatzy.models.ChatMessage;
import com.example.chatzy.models.User;
import com.example.chatzy.utilities.Constants;
import com.example.chatzy.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ConversionListener {
    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    private List<ChatMessage> conversations;
    private RecentConversationAdapter conversationAdapter;
    private FirebaseFirestore database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        init();
        loadUserDetails();
        getToken();
        setListener();
        listenConversations();
    }

    private void init() {
        conversations = new ArrayList<>();
        conversationAdapter = new RecentConversationAdapter(conversations, this);
        binding.conversionRecyclerView.setAdapter(conversationAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void setListener() {
        binding.imageSignOut.setOnClickListener(v -> signOut());
        binding.fabNewChat.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(),UsersActivity.class)));
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void listenConversations() {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = ((value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = senderId;
                    chatMessage.receivedId = receiverId;
                    if (preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)) {
                        chatMessage.conversionImage = documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE);
                        chatMessage.conversionName = documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME);
                        chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    } else {
                        chatMessage.conversionImage = documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE);
                        chatMessage.conversionName = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                        chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    }
                    chatMessage.messafe = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    conversations.add(chatMessage);
                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    for (int i = 0; i<conversations.size(); i++) {
                        String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        if (conversations.get(i).senderId.equals(senderId) && conversations.get(i).receivedId.equals(receiverId)) {
                            conversations.get(i).messafe = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                            conversations.get(i).dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                            break;
                        }
                    }
                }
            }
            Collections.sort(conversations, (obj1, obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
            conversationAdapter.notifyDataSetChanged();
            binding.conversionRecyclerView.smoothScrollToPosition(0);
            binding.conversionRecyclerView.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    });

//    private final EventListener<QuerySnapshot> eventListener = ((value, error) -> {
//        if (error != null) {
//            return;
//        }
//
//        if (value != null && !value.isEmpty()) {
//            // Loop through all the documents in the query result
//            for (DocumentSnapshot document : value.getDocuments()) {
//                String senderId = document.getString(Constants.KEY_SENDER_ID);
//                String receiverId = document.getString(Constants.KEY_RECEIVER_ID);
//                String conversationId = getConversationId(senderId, receiverId);
//                String message = document.getString(Constants.KEY_LAST_MESSAGE);
//                Date timestamp = document.getDate(Constants.KEY_TIMESTAMP);
//
//                // Check if conversation already exists in the list
//                int index = getConversationIndex(conversationId);
//                if (index != -1) {
//                    // Conversation already exists, update the latest message
//                    ChatMessage existingChatMessage = conversations.get(index);
//                    existingChatMessage.message = message;
//                    existingChatMessage.dateObject = timestamp;
//                } else {
//                    // Conversation doesn't exist, create a new conversation
//                    ChatMessage chatMessage = new ChatMessage();
//                    chatMessage.senderId = senderId;
//                    chatMessage.receiverId = receiverId;
//                    if (preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)) {
//                        chatMessage.conversionImage = document.getString(Constants.KEY_RECEIVER_IMAGE);
//                        chatMessage.conversionName = document.getString(Constants.KEY_RECEIVER_NAME);
//                        chatMessage.conversionId = receiverId;
//                    } else {
//                        chatMessage.conversionImage = document.getString(Constants.KEY_SENDER_IMAGE);
//                        chatMessage.conversionName = document.getString(Constants.KEY_SENDER_NAME);
//                        chatMessage.conversionId = senderId;
//                    }
//                    chatMessage.message = message;
//                    chatMessage.dateObject = timestamp;
//                    conversations.add(chatMessage);
//                }
//            }
//
//            // Sort conversations list in reverse chronological order based on the latest message timestamp
//            Collections.sort(conversations, (obj1, obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
//
//            // Update the conversation adapter and UI
//            conversationAdapter.notifyDataSetChanged();
//            binding.conversionRecyclerView.smoothScrollToPosition(0);
//            binding.conversionRecyclerView.setVisibility(View.VISIBLE);
//            binding.progressBar.setVisibility(View.GONE);
//        }
//    });

    // Helper method to get the conversation ID for a given pair of sender and receiver IDs
//    private String getConversationId(String senderId, String receiverId) {
//        if (preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)) {
//            return senderId + "-" + receiverId;
//        } else {
//            return receiverId + "-" + senderId;
//        }
//    }
//
//    // Helper method to get the index of a conversation in the conversations list based on its conversation ID
//    private int getConversationIndex(String conversationId) {
//        for (int i = 0; i < conversations.size(); i++) {
//            ChatMessage chatMessage = conversations.get(i);
//            String existingConversationId = getConversationId(chatMessage.senderId, chatMessage.receiverId);
//            if (existingConversationId.equals(conversationId)) {
//                return i;
//            }
//        }
//        return -1;
//    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e -> showToast("Unable to update Token!"));
    }

    private void loadUserDetails() {
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }

    private void signOut() {
        showToast("Signing out....");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                });
//                .addOnFailureListener(e -> showToast("Unable to sign out!"));

    }

    @Override
    public void onConversionClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }
}