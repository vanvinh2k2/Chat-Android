package com.example.chatapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.R;
import com.example.chatapp.adapter.AdapterRecentConversations;
import com.example.chatapp.listenner.ListenConversation;
import com.example.chatapp.models.ChatMessage;
import com.example.chatapp.models.User;
import com.example.chatapp.unitilitise.Constants;
import com.example.chatapp.unitilitise.ReferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity implements ListenConversation {
    ImageView anhimag,logoutimg;
    TextView nameuserstxt;
    private ReferenceManager manager;
    FloatingActionButton fabNewChat;
    private List<ChatMessage> conversation;
    RecyclerView listConversation;
    AdapterRecentConversations adapter;
    ProgressBar loading;
    FirebaseFirestore store;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        anhXa();
        init();
        loadUsersDetail();
        getToken();
        logoutimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        fabNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),UsersActivity.class);
                startActivity(intent);
            }
        });
        listenConversation();
    }

    private void anhXa(){
        anhimag = findViewById(R.id.imageuser);
        logoutimg = findViewById(R.id.logout);
        nameuserstxt = findViewById(R.id.textname);
        fabNewChat = findViewById(R.id.fabNewChat);
        loading = findViewById(R.id.loading);
        listConversation = findViewById(R.id.conversation);
    }

    private void listenConversation(){
        store.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID,manager.getString(Constants.KEY_USERS_ID))
                .addSnapshotListener(listener);
        store.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,manager.getString(Constants.KEY_USERS_ID))
                .addSnapshotListener(listener);
    }

    private final EventListener<QuerySnapshot> listener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
            if(error!=null) return;
            if(value!=null){
                for(DocumentChange documentChange: value.getDocumentChanges()){
                    if(documentChange.getType() == DocumentChange.Type.ADDED){
                        String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.senderId = senderId;
                        chatMessage.receiverId = receiverId;
                        chatMessage.dataObject = documentChange.getDocument().getDate(Constants.KEY_TIME);
                        chatMessage.message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                        if(senderId.equals(manager.getString(Constants.KEY_USERS_ID))){
                            chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                            chatMessage.conversionName = documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME);
                            chatMessage.conversionImage = documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE);
                        }else{
                            chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                            chatMessage.conversionName = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                            chatMessage.conversionImage = documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE);
                        }
                        conversation.add(chatMessage);
                    }else if (documentChange.getType() == DocumentChange.Type.MODIFIED){
                        for(int i=0;i<conversation.size();i++){
                            String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                            String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                            if(conversation.get(i).senderId.equals(senderId) && conversation.get(i).receiverId.equals(receiverId)){
                                conversation.get(i).message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                                conversation.get(i).dataObject = documentChange.getDocument().getDate(Constants.KEY_TIME);
                                break;
                            }
                        }
                    }
                }
                Collections.sort(conversation, new Comparator<ChatMessage>() {
                    @Override
                    public int compare(ChatMessage o1, ChatMessage o2) {
                        return o2.dataObject.compareTo(o1.dataObject);
                    }
                });
                adapter.notifyDataSetChanged();
                listConversation.setAdapter(adapter);
                loading.setVisibility(View.GONE);
            }
        }
    };

    private void init(){
        conversation = new ArrayList<>();
        adapter = new AdapterRecentConversations(conversation,MainActivity.this);
        listConversation.setAdapter(adapter);
        store = FirebaseFirestore.getInstance();
    }

    private void loadUsersDetail(){
        manager = new ReferenceManager(getApplicationContext());
        nameuserstxt.setText(manager.getString(Constants.KEY_NAME));
        byte[] bytes = Base64.decode(manager.getString(Constants.KEY_IMAGE),Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        anhimag.setImageBitmap(bitmap);
    }

    private void showToast(String message){
        Toast.makeText(MainActivity.this, message+"", Toast.LENGTH_SHORT).show();
    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                updateToken(s);
            }
        });
    }

    private void updateToken(String token){
        manager.putString(Constants.KEY_FCM_TOKEN,token);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference document = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(manager.getString(Constants.KEY_USERS_ID));
        document.update(Constants.KEY_FCM_TOKEN,token)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //showToast("Token updated successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Unable to update token");
                    }
                });
    }

    private void signOut(){
        showToast("Signing out ...");
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firestore.collection(Constants.KEY_COLLECTION_USERS)
                .document(manager.getString(Constants.KEY_USERS_ID));
        Map<String,Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        manager.clear();
                        Intent intent = new Intent(getApplicationContext(),SignInActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Unable to sign out");
                    }
                });
    }

    @Override
    public void onConversionClick(User user) {
        Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
        intent.putExtra(Constants.KEY_USER,user);
        startActivity(intent);
    }
}