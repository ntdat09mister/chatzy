package com.example.chatzy.utilities;

import java.util.HashMap;

public class Constants {
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PREFERENCE_NAME = "chatzyPreference";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_FCM_TOKEN = "fcmToken";
    public static final String KEY_USER = "user";
    public static final String KEY_COLLECTION_CHAT = "chat";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_RECEIVER_ID = "receiverId";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_COLLECTION_CONVERSATIONS = "conversations";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_RECEIVER_NAME = "receiverName";
    public static final String KEY_SENDER_IMAGE = "senderImage";
    public static final String KEY_RECEIVER_IMAGE = "receiverImage";
    public static final String KEY_LAST_MESSAGE = "lastMessage";
    public static final String TYPE_MESSAGE = "type";
    public static final String KEY_COLLECTION_FRIENDS = "friends";
    public static final String KEY_COLLECTION_WAIT_FRIENDS = "wait_friends";
    public static final String KEY_COLLECTION_REQUEST_FRIENDS = "request_friends";
    public static final String KEY_LAST_USER = "lastUser";
    public static final String KEY_LAST_READ = "lastRead";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";
    public static final String KEY_AVAILABLILITY = "availability";
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static HashMap<String, String> remoteMegHeaders = null;
    public static HashMap<String, String> getRemoteMsgHeaders() {
        if (remoteMegHeaders == null) {
            remoteMegHeaders = new HashMap<>();
            remoteMegHeaders.put(REMOTE_MSG_AUTHORIZATION, "key=AAAAP4n8JBM:APA91bG21wIt0nsMcvZ0okc85qENTmRcleRxy8SyNUUm1fjEdFTSnFkHIANXU27eaYWL70ImiMCSJ5Po0h1kNzsj96_MoGpjZ7iGOwdsHIU0Gq9E_Gq3d5DslKvUUukdssp8pziWfQSJ");
            remoteMegHeaders.put(REMOTE_MSG_CONTENT_TYPE, "application/json");
        }
        return remoteMegHeaders;
    }

}
