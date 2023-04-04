package com.example.chatapp.unitilitise;

import java.util.HashMap;

public class Constants {
        public static final String KEY_COLLECTION_USERS = "users";
        public static final String KEY_NAME = "name";
        public static final String KEY_EMAIL = "email";
        public static final String KEY_PASSWORD = "password";
        public static final String KEY_PREFERENCE_NAME = "chatAppPreference";
        public static final String KEY_IS_SIGNED_IN = "isSignIn";
        public static final String KEY_USERS_ID = "usersId";
        public static final String KEY_IMAGE = "image";
        public static final String KEY_FCM_TOKEN = "fcmToken";
        public static final String KEY_USER = "user";
        public static final String KEY_COLLECTION_CHAT = "chat";
        public static final String KEY_SENDER_ID = "senderId";
        public static final String KEY_RECEIVER_ID = "receiverId";
        public static final String KEY_MESSAGE = "message";
        public static final String KEY_TIME = "time";
        public static final String KEY_COLLECTION_CONVERSATIONS = "conversation";
        public static final String KEY_SENDER_NAME = "senderName";
        public static final String KEY_RECEIVER_NAME = "receiverName";
        public static final String KEY_SENDER_IMAGE = "senderImage";
        public static final String KEY_RECEIVER_IMAGE = "receiverImage";
        public static final String KEY_AVAIABLILITY = "availability";
        public static final String KEY_LAST_MESSAGE = "lastMessage";
        public static final String REMOTE_AUTHORIZATION = "Authorization";
        public static final String REMOTE_CONTENT_TYPE = "Content-Type";
        public static final String REMOTE_MGS_DATA = "data";
        public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

        public static HashMap<String,String> remoteMsgHeaders = null;
        public static HashMap<String,String> getRemoteMsgHeaders(){

                if(remoteMsgHeaders==null){
                        remoteMsgHeaders = new HashMap<>();
                        remoteMsgHeaders.put(
                            REMOTE_AUTHORIZATION,
                            "key=AAAASN7cnFE:APA91bHxOGlKa4OLMC5CzUQ_SW9vCEf-s3eUBErCUTgWq7CaKGxgN6dvJNmxkm1R8LVSrk5rptaZyLbNUVIfqL5JIV5pyLjoGMtkU5QjRaSGP02lbDW70M3My0q9MYePF0Kq7z1_ZzGA"
                        );
                        remoteMsgHeaders.put(
                            REMOTE_CONTENT_TYPE,
                            "application/json"
                        );
                }
                return remoteMsgHeaders;
        }
}
