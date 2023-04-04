package com.example.chatapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chatapp.R;
import com.example.chatapp.adapter.AdapterUser;
import com.example.chatapp.listenner.ListenerUser;
import com.example.chatapp.models.User;
import com.example.chatapp.unitilitise.Constants;
import com.example.chatapp.unitilitise.ReferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends BaseActivity implements ListenerUser {
    ImageView backimg;
    ListView userList;
    ProgressBar loading;
    TextView errortxt;
    AdapterUser adapter;
    ReferenceManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        anhXa();
        getUser();
        getBack();
    }
    private void isLoad(Boolean load){
        if(load) loading.setVisibility(View.VISIBLE);
        else loading.setVisibility(View.INVISIBLE);
    }
    private void getBack(){
        backimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
    }
    private void getUser(){
        isLoad(true);
        FirebaseFirestore user = FirebaseFirestore.getInstance();
        user.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        isLoad(false);
                        String currentUser = manager.getString(Constants.KEY_USERS_ID);
                        if(task.isSuccessful()&&task.getResult()!=null){
                            List<User> listUser = new ArrayList<>();
                            for(QueryDocumentSnapshot snapshot : task.getResult()){
                                if(snapshot.getId().equals(currentUser)) continue;
                                User user = new User();
                                user.email = snapshot.getString(Constants.KEY_EMAIL);
                                user.name = snapshot.getString(Constants.KEY_NAME);
                                user.image = snapshot.getString(Constants.KEY_IMAGE);
                                user.token = snapshot.getString(Constants.KEY_FCM_TOKEN);
                                user.id = snapshot.getId();
                                listUser.add(user);
                            }
                            if(userList!=null){
                                adapter = new AdapterUser(getApplicationContext(),R.layout.item_container_user,listUser,UsersActivity.this);
                                userList.setAdapter(adapter);
                            }
                            else{
                                errortxt.setText("No user available");
                            }
                        }else{
                            errortxt.setText("No user available");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    private void anhXa(){
        backimg = findViewById(R.id.imgback);
        userList = findViewById(R.id.userList);
        loading = findViewById(R.id.progressbar);
        errortxt = findViewById(R.id.textError);
        manager = new ReferenceManager(getApplicationContext());
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
        intent.putExtra(Constants.KEY_USER,user);
        startActivity(intent);
        finish();
    }
}