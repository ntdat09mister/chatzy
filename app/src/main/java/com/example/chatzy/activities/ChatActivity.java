package com.example.chatzy.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.example.chatzy.R;
import com.example.chatzy.adapters.ChatAdapter;
import com.example.chatzy.databinding.ActivityChatBinding;
import com.example.chatzy.models.ChatMessage;
import com.example.chatzy.models.User;
import com.example.chatzy.network.ApiClient;
import com.example.chatzy.network.ApiService;
import com.example.chatzy.utilities.Constants;
import com.example.chatzy.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import retrofit2.Callback;
import retrofit2.Call;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private User receivedUser;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private String conversationId = null;
    private Boolean isReceiverAvailable = false;
    private String conversionId = null;

    private String encodedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(binding.getRoot());

        setListeners();
        loadReceivedDetails();
        init();
        listenMessages();
//        getFriendStatus();
    }

//    private void getFriendStatus() {
//        User user = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
//        String myID = preferenceManager.getString(Constants.KEY_USER_ID);
//        String myName = preferenceManager.getString(Constants.KEY_NAME);
//        String myImage = preferenceManager.getString(Constants.KEY_IMAGE);
//        String myEmail = preferenceManager.getString(Constants.KEY_EMAIL);
//
//        database.collection(Constants.KEY_COLLECTION_USERS).document(myID).collection(Constants.KEY_COLLECTION_FRIENDS).whereEqualTo(Constants.KEY_USER_ID, user.id).get().addOnCompleteListener(task1 -> {
//            if (task1.isSuccessful() && !task1.getResult().isEmpty()) {
//                binding.imageBackgroundFriendStatus.setVisibility(View.GONE);
//                binding.friendAdd.setVisibility(View.GONE);
//                binding.friendRequest.setVisibility(View.GONE);
//                binding.friendWait.setVisibility(View.GONE);
//            } else {
//                database.collection(Constants.KEY_COLLECTION_USERS).document(myID).collection(Constants.KEY_COLLECTION_WAIT_FRIENDS).whereEqualTo(Constants.KEY_USER_ID, user.id).get().addOnCompleteListener(task2 -> {
//                    if (task2.isSuccessful() && !task2.getResult().isEmpty()) {
//                        binding.imageBackgroundFriendStatus.setVisibility(View.VISIBLE);
//                        binding.friendAdd.setVisibility(View.GONE);
//                        binding.friendRequest.setVisibility(View.GONE);
//                        binding.friendWait.setVisibility(View.VISIBLE);
//                    } else {
//                        database.collection(Constants.KEY_COLLECTION_USERS).document(myID).collection(Constants.KEY_COLLECTION_REQUEST_FRIENDS).whereEqualTo(Constants.KEY_USER_ID, user.id).get().addOnCompleteListener(task3 -> {
//                            if (task3.isSuccessful() && !task3.getResult().isEmpty()) {
//                                binding.imageBackgroundFriendStatus.setVisibility(View.VISIBLE);
//                                binding.friendRequest.setVisibility(View.VISIBLE);
//                                binding.friendAdd.setVisibility(View.GONE);
//                                binding.friendWait.setVisibility(View.GONE);
//                            } else {
//                                binding.imageBackgroundFriendStatus.setVisibility(View.VISIBLE);
//                                binding.friendAdd.setVisibility(View.VISIBLE);
//                                binding.friendWait.setVisibility(View.GONE);
//                                binding.friendRequest.setVisibility(View.GONE);
//
//                                binding.friendAdd.setOnClickListener(v -> {
//                                    HashMap<String, Object> userFriend = new HashMap<>();
//                                    userFriend.put(Constants.KEY_USER_ID, user.id);
//                                    userFriend.put(Constants.KEY_NAME, user.name);
//                                    userFriend.put(Constants.KEY_IMAGE, user.image);
//                                    userFriend.put(Constants.KEY_EMAIL, user.email);
//
//                                    database.collection(Constants.KEY_COLLECTION_USERS).document(myID).collection(Constants.KEY_COLLECTION_WAIT_FRIENDS).add(userFriend).addOnSuccessListener(documentReference -> {
//
//                                    }).addOnFailureListener(exception -> {
//                                        showToast(exception.getMessage());
//                                    });
//
//                                    HashMap<String, Object> myUser = new HashMap<>();
//                                    myUser.put(Constants.KEY_USER_ID, myID);
//                                    myUser.put(Constants.KEY_NAME, myName);
//                                    myUser.put(Constants.KEY_IMAGE, myImage);
//                                    myUser.put(Constants.KEY_EMAIL, myEmail);
//
//                                    database.collection(Constants.KEY_COLLECTION_USERS).document(user.id).collection(Constants.KEY_COLLECTION_REQUEST_FRIENDS).add(myUser).addOnSuccessListener(documentReference -> {
//                                        showToast("Đã gửi lời mời kết bạn đến " +user.name);
//                                    }).addOnFailureListener(exception -> {
//                                        showToast(exception.getMessage());
//                                    });
//
//                                    binding.imageBackgroundFriendStatus.setVisibility(View.VISIBLE);
//                                    binding.friendAdd.setVisibility(View.GONE);
//                                    binding.friendRequest.setVisibility(View.GONE);
//                                    binding.friendWait.setVisibility(View.VISIBLE);
//
//                                });
//                            }
//                        });
//                    }
//                });
//            }
//        });
//    }

    private void init() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages, getBitmapFromEncodedString(receivedUser.image), preferenceManager.getString(Constants.KEY_USER_ID));
        binding.chatRecyclerView.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();

    }

    private void sendMessage() {
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID, receivedUser.id);
        message.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());

        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        if (conversionId != null) {
            updateCOnversion(binding.inputMessage.getText().toString(), preferenceManager.getString(Constants.KEY_USER_ID));
        } else {
            HashMap<String, Object> conversion = new HashMap<>();
            conversion.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
            conversion.put(Constants.KEY_SENDER_NAME, preferenceManager.getString(Constants.KEY_NAME));
            conversion.put(Constants.KEY_SENDER_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE));
            conversion.put(Constants.KEY_RECEIVER_ID, receivedUser.id);
            conversion.put(Constants.KEY_RECEIVER_NAME, receivedUser.name);
            conversion.put(Constants.KEY_RECEIVER_IMAGE, receivedUser.image);
            conversion.put(Constants.KEY_LAST_MESSAGE, binding.inputMessage.getText().toString());
            conversion.put(Constants.KEY_LAST_USER, preferenceManager.getString(Constants.KEY_USER_ID));
            conversion.put(Constants.KEY_COLLECTION_CONVERSATIONS, "");
            conversion.put(Constants.KEY_LAST_READ, "0");
            conversion.put(Constants.KEY_TIMESTAMP, new Date());

            addConversion(conversion);

            Handler h = new Handler() ;
            h.postDelayed(() -> {
                updateConversationID();
            }, 1000);

            if(conversionId == null) {
                h.postDelayed(() -> {
                    updateConversationID();
                }, 2000);
            }

        }
        if (!isReceiverAvailable) {
            try {
                JSONArray tokens = new JSONArray();
                tokens.put(receivedUser.token);

                JSONObject data = new JSONObject();
                data.put(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
                data.put(Constants.KEY_NAME, preferenceManager.getString(Constants.KEY_NAME));
                data.put(Constants.KEY_FCM_TOKEN, preferenceManager.getString(Constants.KEY_FCM_TOKEN));
                data.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());

                JSONObject body = new JSONObject();
                body.put(Constants.REMOTE_MSG_DATA, data);
                body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

                sendNotification(body.toString());
            } catch (Exception exception) {
                showToast(exception.getMessage());
            }
        }
        binding.inputMessage.setText(null);
    }

    private void sendImageMessage() {
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID, receivedUser.id);
        message.put(Constants.KEY_MESSAGE, encodedImage);
        message.put(Constants.KEY_TIMESTAMP, new Date());

        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        if (conversionId != null) {
            updateCOnversion("Tin nhắn dạng hình ảnh...", preferenceManager.getString(Constants.KEY_USER_ID));
        } else {
            HashMap<String, Object> conversion = new HashMap<>();
            conversion.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
            conversion.put(Constants.KEY_SENDER_NAME, preferenceManager.getString(Constants.KEY_NAME));
            conversion.put(Constants.KEY_SENDER_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE));
            conversion.put(Constants.KEY_RECEIVER_ID, receivedUser.id);
            conversion.put(Constants.KEY_RECEIVER_NAME, receivedUser.name);
            conversion.put(Constants.KEY_RECEIVER_IMAGE, receivedUser.image);
            conversion.put(Constants.KEY_LAST_MESSAGE, "Đã gửi một hình ảnh...");
            conversion.put(Constants.KEY_LAST_USER, preferenceManager.getString(Constants.KEY_USER_ID));
            conversion.put(Constants.KEY_COLLECTION_CONVERSATIONS, "");
            conversion.put(Constants.KEY_LAST_READ, "0");
            conversion.put(Constants.KEY_TIMESTAMP, new Date());

            addConversion(conversion);

            Handler h = new Handler() ;
            h.postDelayed(() -> {
                updateConversationID();
            }, 1000);

            if(conversionId == null) {
                h.postDelayed(() -> {
                    updateConversationID();
                }, 2000);
            }
        }
        binding.inputMessage.setText(null);

    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

//                            binding.textAddImage.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);

                            sendImageMessage();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

    );

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 1000;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitMap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitMap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void sendNotification(String messageBody) {
        ApiClient.getClient().create(ApiService.class).sendMessage(
                Constants.getRemoteMsgHeaders(), messageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    try {
                        if (response.body() != null) {
                            JSONObject responseJson = new JSONObject(response.body());
                            JSONArray results = responseJson.getJSONArray("results");
                            if (responseJson.getInt("failure") == 1) {
                                JSONObject error = (JSONObject) results.get(0);
                                showToast(error.getString("lỗi"));
                                return;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    showToast(preferenceManager.getString(Constants.KEY_NAME));
//                    showToast(preferenceManager.getString(Constants.KEY_USER_ID));
                } else {
                    showToast("Lỗi: " +response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                showToast(t.getMessage());
            }
        });
    }

    private void listenAvailablilityOfReceiver() {
        database.collection(Constants.KEY_COLLECTION_USERS).document(
                receivedUser.id
        ).addSnapshotListener(ChatActivity.this, (value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null) {
                if (value.getLong(Constants.KEY_AVAILABLILITY) != null) {
                    int availability = Objects.requireNonNull(value.getLong(Constants.KEY_AVAILABLILITY)).intValue();
                    isReceiverAvailable = availability == 1;
                }
                receivedUser.token = value.getString(Constants.KEY_FCM_TOKEN);
                if (receivedUser.image == null) {
                    receivedUser.image = value.getString(Constants.KEY_IMAGE);
                    chatAdapter.setReceivierProfileImage(getBitmapFromEncodedString(receivedUser.image));
                    chatAdapter.notifyItemRangeChanged(0, chatMessages.size());
                }
            }
            if (isReceiverAvailable) {
                binding.textAvailablility.setVisibility(View.VISIBLE);
                binding.imageOnline.setVisibility(View.VISIBLE);
            } else {
                binding.textAvailablility.setVisibility(View.GONE);
                binding.imageOnline.setVisibility(View.GONE);
            }
        });
    }

    private void listenMessages() {
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receivedUser.id)
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, receivedUser.id)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        } if (value != null) {
            int count = chatMessages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatMessage.receivedId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    chatMessage.messafe = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                    chatMessage.dataTime = getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);

                    chatMessage.encodedImage = encodedImage;

                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            if (count == 0) {
                chatAdapter.notifyDataSetChanged();
            } else {
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
            }
            binding.chatRecyclerView.setVisibility(View.VISIBLE);
        }
        binding.progressBar.setVisibility(View.GONE);

        if (conversionId == null) {
            checkForConversion();
        }
    };

    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        if (encodedImage != null) {
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } else {
            return null;
        }

    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());

        binding.layoutSend.setOnClickListener(v -> {
            if (!binding.inputMessage.getText().toString().isEmpty()) {
                sendMessage();
            }
        });

        binding.imageView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });

//        binding.imageCallAudio.setOnClickListener(v -> {
//            Intent intent = new Intent(getApplicationContext(), CallingActivity.class);
//            intent.putExtra("type", "audio");
//            intent.putExtra("user", (User) getIntent().getSerializableExtra(Constants.KEY_USER));
//            startActivity(intent);
//        });
//        binding.imageCallVideo.setOnClickListener(v -> {
//            Intent intent = new Intent(getApplicationContext(), CallingActivity.class);
//            intent.putExtra("type", "video");
//            intent.putExtra("user", (User) getIntent().getSerializableExtra(Constants.KEY_USER));
//            startActivity(intent);
//        });

//        binding.imageInfo.setOnClickListener(v -> {
//            intentInfoChatActivity();
//        });

        binding.imageProfile.setOnClickListener(v -> {
            intentInfoChatActivity();
        });

        binding.textName.setOnClickListener(v -> {
            intentInfoChatActivity();
        });

        binding.textAvailablility.setOnClickListener(v -> {
            intentInfoChatActivity();
        });

        binding.imageVoice.setOnClickListener(v -> {
            speak(v);
        });

    }

    private void intentInfoChatActivity() {
        Intent intent = new Intent(getApplicationContext(), InfoChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("user", (User) getIntent().getSerializableExtra(Constants.KEY_USER));
        intent.putExtra("myID", preferenceManager.getString(Constants.KEY_USER_ID));
        intent.putExtra("myName", preferenceManager.getString(Constants.KEY_NAME));
        intent.putExtra("myImage", preferenceManager.getString(Constants.KEY_IMAGE));
        startActivity(intent);
    }

    private void speak(View v) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi_VN");
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Đọc đoạn tin nhắn bạn muốn gửi đi...");
        someActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        binding.inputMessage.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
                    }
                }
            });

    private void loadReceivedDetails() {
        receivedUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textName.setText(receivedUser.name);
        binding.imageProfile.setImageBitmap(getBitmapFromEncodedString(receivedUser.image));
    }

    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("HH:mm · dd/MM/yyyy", Locale.getDefault()).format(date);
    }

    private void addConversion(HashMap<String, Object> conversion) {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).add(conversion).addOnSuccessListener(documentReference -> conversionId = documentReference.getId());
    }

    private void updateCOnversion(String message, String user) {
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversionId);
        documentReference.update(Constants.KEY_LAST_MESSAGE, message, Constants.KEY_TIMESTAMP, new Date(), Constants.KEY_LAST_USER, user, Constants.KEY_LAST_READ, "0", Constants.KEY_COLLECTION_CONVERSATIONS, conversionId);
    }

    private void checkForConversion() {
        if (chatMessages.size() != 0) {
            checkForConversionRemotely(preferenceManager.getString(Constants.KEY_USER_ID), receivedUser.id);
            checkForConversionRemotely(receivedUser.id, preferenceManager.getString(Constants.KEY_USER_ID));
        }
    }

    private void checkForConversionRemotely(String senderId, String receiverId) {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverId)
                .get().addOnCompleteListener(conversionOnCompleteListener);
    }

    private final OnCompleteListener<QuerySnapshot> conversionOnCompleteListener = task -> {
        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversionId = documentSnapshot.getId();
        }
    };

    public void updateConversationID() {
        if (conversionId != null) {
            DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversionId);
            documentReference.update(Constants.KEY_COLLECTION_CONVERSATIONS, conversionId);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        listenAvailablilityOfReceiver();
    }
}