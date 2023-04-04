package com.example.chatapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.R;
import com.example.chatapp.adapter.ChatAdapter;
import com.example.chatapp.listenner.ApiServer;
import com.example.chatapp.models.ChatMessage;
import com.example.chatapp.models.User;
import com.example.chatapp.network.ApiClient;
import com.example.chatapp.unitilitise.Constants;
import com.example.chatapp.unitilitise.ReferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends BaseActivity {
    ImageView backimg,infoimg;
    TextView nametxt,onlinetxt;
    RecyclerView listChat;
    ProgressBar loadpgb;
    EditText inputMessageedt;
    FrameLayout sendfrl;
    private User received;
    List<ChatMessage> chatMessages;
    ChatAdapter adapter;
    FirebaseFirestore store;
    ReferenceManager manager;
    private String conversionId = null;
    private Boolean isReceiverAvailable = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        anhXa();
        getUserReceived();
        backimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
        infoimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        init();
        sendfrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        listenMessage();
    }

    private void anhXa(){
        backimg = findViewById(R.id.back);
        infoimg = findViewById(R.id.info);
        nametxt = findViewById(R.id.textName);
        listChat = findViewById(R.id.listMessage);
        loadpgb = findViewById(R.id.load);
        inputMessageedt = findViewById(R.id.inputMessage);
        sendfrl = findViewById(R.id.layoutSend);
    }

    private void getUserReceived(){
        Intent intent = getIntent();
        received = (User) intent.getSerializableExtra(Constants.KEY_USER);
        nametxt.setText(received.name);
    }

    private void isLoad(boolean load){
        if(load) loadpgb.setVisibility(View.VISIBLE);
        else  loadpgb.setVisibility(View.INVISIBLE);
    }

    private void init(){
        manager = new ReferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        adapter = new ChatAdapter(chatMessages,getBitmap(received.image),manager.getString(Constants.KEY_USERS_ID));
        listChat.setAdapter(adapter);
        store = FirebaseFirestore.getInstance();
        onlinetxt = findViewById(R.id.online);
    }

    private void sendMessage() {
        if(!inputMessageedt.getText().toString().trim().isEmpty()){
            Map<String, Object> message = new HashMap<>();
            message.put(Constants.KEY_SENDER_ID,manager.getString(Constants.KEY_USERS_ID));
            message.put(Constants.KEY_RECEIVER_ID,received.id);
            message.put(Constants.KEY_MESSAGE,inputMessageedt.getText().toString());
            message.put(Constants.KEY_TIME,new Date());
            store.collection(Constants.KEY_COLLECTION_CHAT).add(message);
            if(conversionId == null){
                HashMap<String,Object> conversion = new HashMap<>();
                conversion.put(Constants.KEY_SENDER_ID,manager.getString(Constants.KEY_USERS_ID));
                conversion.put(Constants.KEY_SENDER_NAME,manager.getString(Constants.KEY_NAME));
                conversion.put(Constants.KEY_SENDER_IMAGE,manager.getString(Constants.KEY_IMAGE));
                conversion.put(Constants.KEY_RECEIVER_ID,received.id);
                conversion.put(Constants.KEY_RECEIVER_NAME,received.name);
                conversion.put(Constants.KEY_RECEIVER_IMAGE,received.image);
                conversion.put(Constants.KEY_LAST_MESSAGE,inputMessageedt.getText().toString().trim());
                conversion.put(Constants.KEY_TIME,new Date());
                addConversion(conversion);
            }
            else{
                updateConversion(inputMessageedt.getText().toString().trim());
            }
            if(!isReceiverAvailable){
                try {
                    JSONArray tokens = new JSONArray();
                    tokens.put(received.token);

                    JSONObject data = new JSONObject();
                    data.put(Constants.KEY_USERS_ID, manager.getString(Constants.KEY_USERS_ID));
                    data.put(Constants.KEY_NAME, manager.getString(Constants.KEY_NAME));
                    data.put(Constants.KEY_FCM_TOKEN, manager.getString(Constants.KEY_FCM_TOKEN));
                    data.put(Constants.KEY_MESSAGE, inputMessageedt.getText().toString());

                    JSONObject body = new JSONObject();
                    body.put(Constants.REMOTE_MGS_DATA, data);
                    body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);
                    sendNotifycation(body.toString());
                }catch (JSONException e){
                    showToast(e.getMessage());
                }
            }
            inputMessageedt.setText(null);
        }else Toast.makeText(getApplicationContext(), "Please enter a message!", Toast.LENGTH_SHORT).show();

    }

    private void listenAvailabilityReceiver(){
        store.collection(Constants.KEY_COLLECTION_USERS).document(received.id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error!=null) return;
                        if(value!=null){
                            if(value.getLong(Constants.KEY_AVAIABLILITY)!=null){
                                int availability = Objects.requireNonNull(
                                        value.getLong(Constants.KEY_AVAIABLILITY)
                                ).intValue();
                                isReceiverAvailable = availability == 1;
                            }
                            received.token = value.getString(Constants.KEY_FCM_TOKEN);
                        }
                        if(isReceiverAvailable){
                            onlinetxt.setVisibility(View.VISIBLE);
                        }else onlinetxt.setVisibility(View.GONE);

                    }
                });
    }

    private void listenMessage(){
        store.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,manager.getString(Constants.KEY_USERS_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID,received.id)
                .addSnapshotListener(listener);
        store.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,received.id)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,manager.getString(Constants.KEY_USERS_ID))
                .addSnapshotListener(listener);
    }

    private final EventListener<QuerySnapshot> listener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
            if(error!=null) return;
            if(value!=null){
                int count = chatMessages.size();
                for(DocumentChange documentChange : value.getDocumentChanges()){
                    if(documentChange.getType()==DocumentChange.Type.ADDED){
                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        chatMessage.receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        chatMessage.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                        chatMessage.dateTime = getDateTime(documentChange.getDocument().getDate(Constants.KEY_TIME));
                        chatMessage.dataObject = documentChange.getDocument().getDate(Constants.KEY_TIME);
                        chatMessages.add(chatMessage);
                    }
                }
                Collections.sort(chatMessages, new Comparator<ChatMessage>() {
                    @Override
                    public int compare(ChatMessage o1, ChatMessage o2) {
                        return o1.dataObject.compareTo(o2.dataObject);
                    }
                });
                if(count==0){
                    adapter.notifyDataSetChanged();
                }else {
                    adapter.notifyItemRangeInserted(chatMessages.size(),chatMessages.size());
                    listChat.smoothScrollToPosition(chatMessages.size()-1);
                }
                isLoad(false);
                if(conversionId==null){
                    checkForConversion();
                }
            }
        }
    };

    private Bitmap getBitmap(String encodeImage){
        byte[] bytes = Base64.decode(encodeImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

    private String getDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void addConversion(Map<String,Object> conversion){
        store.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .add(conversion)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        conversionId = documentReference.getId();
                    }
                });
    }
    private void updateConversion(String message){
        DocumentReference document = store.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .document(conversionId);
        document.update(
                Constants.KEY_LAST_MESSAGE,message,
                Constants.KEY_TIME,new Date()
        );

    }

    //
    private void checkForConversion(){
        if(chatMessages.size()!=0){
            checkForConversionRemotely(manager.getString(Constants.KEY_USERS_ID),received.id);
            checkForConversionRemotely(received.id,manager.getString(Constants.KEY_USERS_ID));
        }
    }

    private void checkForConversionRemotely(String senderId,String receiverId){
        store.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,receiverId)
                .whereEqualTo(Constants.KEY_SENDER_ID,senderId)
                .get()
                .addOnCompleteListener(conversionOnComplete);
    }

    OnCompleteListener<QuerySnapshot> conversionOnComplete = new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if(task.isSuccessful() && task.getResult()!=null && task.getResult().getDocuments().size()>0){
                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                conversionId = documentSnapshot.getId();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        listenAvailabilityReceiver();
    }

    private void sendNotifycation(String messageBody){
        ApiClient.getClient().create(ApiServer.class).sendMessage(
                Constants.getRemoteMsgHeaders(),
                messageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if(response.isSuccessful()) {
                    try {
                        if (response.body() != null) {
                            JSONObject object = new JSONObject(response.body());
                            JSONArray resutls = object.getJSONArray("results");
                            if (object.getInt("failure") == 1) {
                                JSONObject error = (JSONObject) resutls.get(0);
                                showToast(error.getString("error"));
                                return;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    showToast("Notification send successfully");
                }
                else showToast("Error : "+response.code());
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                showToast(t.getMessage());
            }
        });
    }

    private void showToast(String data){
        Toast.makeText(getApplicationContext(), data+"", Toast.LENGTH_SHORT).show();
    }
}
