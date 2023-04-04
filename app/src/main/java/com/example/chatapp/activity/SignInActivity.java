package com.example.chatapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.R;
import com.example.chatapp.unitilitise.Constants;
import com.example.chatapp.unitilitise.ReferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class SignInActivity extends AppCompatActivity {
    EditText emailedt,passwordedt;
    Button signinbtn;
    TextView createtxt;
    ProgressBar signinpgb;
    private ReferenceManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        anhXa();
        if(manager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
        createtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
        signinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidSignUpDetails()){
                    signIn();
                }
            }
        });
    }

    private void anhXa(){
        manager  = new ReferenceManager(getApplicationContext());
        emailedt = findViewById(R.id.inputemail);
        passwordedt = findViewById(R.id.inputpassword);
        signinbtn = findViewById(R.id.buttonsignin);
        createtxt = findViewById(R.id.createnewacount);
        signinpgb = findViewById(R.id.progressBarsignin);
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message+"", Toast.LENGTH_SHORT).show();
    }

    private void signIn(){
        isLoad(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL,emailedt.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD,passwordedt.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && task.getResult()!=null && task.getResult().getDocuments().size()>0){
                            DocumentSnapshot snapshot = task.getResult().getDocuments().get(0);
                            manager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                            manager.putString(Constants.KEY_USERS_ID,snapshot.getId());
                            manager.putString(Constants.KEY_NAME,snapshot.getString(Constants.KEY_NAME));
                            manager.putString(Constants.KEY_IMAGE,snapshot.getString(Constants.KEY_IMAGE));
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        else{
                            isLoad(false);
                            showToast("Unable to sign in");
                        }
                    }
                });
    }

    private Boolean isValidSignUpDetails() {
       if (emailedt.getText().toString().trim().isEmpty()) {
            showToast("Enter email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailedt.getText().toString()).matches()) {
            showToast("Enter valid email");
            return false;
        } else if (passwordedt.getText().toString().trim().isEmpty()) {
            showToast("Enter password");
            return false;
        } else return true;
    }
    private void isLoad(Boolean load){
        if(load){
            signinpgb.setVisibility(View.VISIBLE);
            signinbtn.setVisibility(View.INVISIBLE);
        }else{
            signinpgb.setVisibility(View.INVISIBLE);
            signinbtn.setVisibility(View.VISIBLE);
        }
    }
}