package com.example.chatapp.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.unitilitise.Constants;
import com.example.chatapp.unitilitise.ReferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BaseActivity extends AppCompatActivity {
    private DocumentReference documentReference;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ReferenceManager manager = new ReferenceManager(getApplicationContext());
        FirebaseFirestore store = FirebaseFirestore.getInstance();
        documentReference = store.collection(Constants.KEY_COLLECTION_USERS)
                .document(manager.getString(Constants.KEY_USERS_ID));
    }

    @Override
    protected void onPause() {
        super.onPause();
        documentReference.update(Constants.KEY_AVAIABLILITY,0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        documentReference.update(Constants.KEY_AVAIABLILITY,1);
    }
}
