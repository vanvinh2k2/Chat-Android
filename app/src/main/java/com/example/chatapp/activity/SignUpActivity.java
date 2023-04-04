package com.example.chatapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.R;
import com.example.chatapp.unitilitise.Constants;
import com.example.chatapp.unitilitise.ReferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    ImageView anhimg;
    TextView addimagetxt,signintxt;
    EditText nameedt,emailedt,passwordedt,conpasswordedt;
    Button signupbtn;
    ProgressBar loadsb;
    private ReferenceManager manager;
    FrameLayout frameLayoutanh;
    private String encodedImage;
    private final int CODE_REQUEST_PICK = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        manager = new ReferenceManager(getApplicationContext());
        anhXa();
        signintxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this,SignInActivity.class);
                startActivity(intent);
            }
        });
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidSignUpDetails()){
                    isLoad(true);
                    signUp();
                }
            }
        });
        frameLayoutanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(SignUpActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},CODE_REQUEST_PICK);
            }
        });
    }
    private void anhXa(){
        anhimg = findViewById(R.id.imageuser);
        addimagetxt = findViewById(R.id.context);
        nameedt = findViewById(R.id.inputname);
        emailedt = findViewById(R.id.inputemail);
        passwordedt = findViewById(R.id.inputpassword);
        conpasswordedt = findViewById(R.id.inputconformpassword);
        signupbtn = findViewById(R.id.buttonsignup);
        signintxt = findViewById(R.id.signin);
        loadsb = findViewById(R.id.progressbar);
        frameLayoutanh = findViewById(R.id.frameimage);
    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message+"", Toast.LENGTH_SHORT).show();
    }
    private void signUp(){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        HashMap<String,Object> data = new HashMap<>();
        data.put(Constants.KEY_NAME,nameedt.getText().toString().trim());
        data.put(Constants.KEY_EMAIL,emailedt.getText().toString().trim());
        data.put(Constants.KEY_PASSWORD,passwordedt.getText().toString().trim());
        data.put(Constants.KEY_IMAGE,encodedImage);
        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                       // Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                        isLoad(false);
                        manager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                        manager.putString(Constants.KEY_USERS_ID, documentReference.getId());
                        manager.putString(Constants.KEY_NAME,nameedt.getText().toString());
                        manager.putString(Constants.KEY_IMAGE,encodedImage);
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       // Log.w("TAG", "Error adding document", e);
                        isLoad(false);
                        showToast(e.getMessage());
                    }
                });
    }
    private String encodeImage(Bitmap bitmap){
        int wigth = 150;
        int height = bitmap.getHeight()*wigth/bitmap.getWidth();
        Bitmap prebitmap = Bitmap.createScaledBitmap(bitmap,wigth,height,false);
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        prebitmap.compress(Bitmap.CompressFormat.JPEG,50,arrayOutputStream);
        byte[] bytes = arrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_REQUEST_PICK && grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent,CODE_REQUEST_PICK);
        }else showToast("You are not allowed");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data!=null){
            Uri url = data.getData();
            InputStream inputStream = null;
            try {
                inputStream = getContentResolver().openInputStream(url);
                Bitmap anh = BitmapFactory.decodeStream(inputStream);
                anhimg.setImageBitmap(anh);
                encodedImage = encodeImage(anh);
                addimagetxt.setVisibility(View.INVISIBLE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private Boolean isValidSignUpDetails() {
        if (encodedImage == null) {
            showToast("Select profile image");
            return false;
        } else if (nameedt.getText().toString().trim().isEmpty()) {
            showToast("Enter name");
            return false;
        } else if (emailedt.getText().toString().trim().isEmpty()) {
            showToast("Enter email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailedt.getText().toString()).matches()) {
            showToast("Enter valid email");
            return false;
        } else if (passwordedt.getText().toString().trim().isEmpty()) {
            showToast("Enter password");
            return false;
        } else if (conpasswordedt.getText().toString().trim().isEmpty()) {
            showToast("Confirm your password");
            return false;
        } else if (!passwordedt.getText().toString().equals(conpasswordedt.getText().toString())) {
            showToast("Password and confirm password must be same");
            return false;
        } else return true;
    }

    private void isLoad(Boolean load){
        if(load){
            signupbtn.setVisibility(View.INVISIBLE);
            loadsb.setVisibility(View.VISIBLE);
        }
        else {
            loadsb.setVisibility(View.INVISIBLE);
            signupbtn.setVisibility(View.VISIBLE);
        }
    }
}