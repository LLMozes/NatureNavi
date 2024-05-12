package com.naturenavi.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    EditText mFullname;EditText mEmail;EditText mPassword;EditText mPasswordAgain;EditText mPhoneNumber;

    private FirebaseAuth mAuth;
    FirebaseFirestore mFirestoreDb;
    String userID;

    //ProgressBar ez jo cucc ha jelezni kaarom hogy backend oldalon valami folyamat zajlik.

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullname = findViewById(R.id.fullName_EditText);
        mEmail = findViewById(R.id.email_EditText);
        mPassword = findViewById(R.id.password_EditText);
        mPasswordAgain = findViewById(R.id.passwordAgain_EditText);
        mPhoneNumber = findViewById(R.id.phone_EditText);

        mAuth = FirebaseAuth.getInstance();
        mFirestoreDb = FirebaseFirestore.getInstance();

        //itt lehetne azt is hogy nem a loginra hanem az alkalmazás fő oldalára irányítson vissza
        if(mAuth.getCurrentUser() != null){
            //TODO
            goLoginPageFromRegister();
            finish();
        }

    }


    public void register(View view) {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        String passwordAgain = mPasswordAgain.getText().toString();
        String userName = mFullname.getText().toString().trim();
        String phoneNumber = mPhoneNumber.getText().toString().trim();


        if(TextUtils.isEmpty(email)){
            mEmail.setError("Email is required.");
            return;
        }
        if(TextUtils.isEmpty(password)){
            mPassword.setError("Password is required.");
            return;
        }
        if(password.length() <6 ){
            mPassword.setError("Password msut be >= 6 characters");
            return;
        }
        if(!password.equals(passwordAgain)){
            mPasswordAgain.setError("Password doesnt match");
            return;
        }



        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "Sikeres művelet!", Toast.LENGTH_SHORT).show();
                    userID = mAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = mFirestoreDb.collection("users").document(userID);
                    Map<String,Object> user = new HashMap<>();
                    user.put("fullName",userName);
                    user.put("email",email);
                    user.put("phoneNumber",phoneNumber);
                    List<String> tripIds = new ArrayList<>();
                    user.put("bookedTripIds", tripIds);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {

                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG,"Sikerult az eltarolas az adatbazisba az eltarolt user id: "+userID);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG,"Nem sikerült az adatbásiba beletenni ezert: "+e.toString());
                        }
                    });
                    startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                }else {
                    Toast.makeText(RegisterActivity.this, "Hiba! Valami biba van a hattérben:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


private void goLoginPageFromRegister(){
    Intent loginIntent = new Intent(this,LoginActivity.class);
    startActivity(loginIntent);
}


}