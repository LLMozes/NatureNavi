package com.naturenavi.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

    ImageView emailIcon,passwordVisibilityIcon,passwordMatchIcon,phoneNumberIcon;
    private FirebaseAuth mAuth;
    FirebaseFirestore mFirestoreDb;
    String userID;

    private static final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.[a-z]{2,}";

    private static final String phonePattern = "^0[0-9]{10}$";
    private boolean isValidEmail(String email) {
        return email.matches(emailPattern);
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone.matches(phonePattern);
    }

    //ProgressBar//

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullname = findViewById(R.id.fullName_EditText);
        mEmail = findViewById(R.id.email_EditText);
        mPassword = findViewById(R.id.password_EditText);
        mPasswordAgain = findViewById(R.id.passwordAgain_EditText);
        mPhoneNumber = findViewById(R.id.phone_EditText);

                emailIcon = findViewById(R.id.correctEmail);
        passwordVisibilityIcon =findViewById(R.id.passwordVisibilityEye);
                passwordMatchIcon = findViewById(R.id.matchPassword);
        phoneNumberIcon = findViewById(R.id.correctPhoneNumber);



        mAuth = FirebaseAuth.getInstance();
        mFirestoreDb = FirebaseFirestore.getInstance();

        //itt lehetne azt is hogy nem a loginra hanem az alkalmazás fő oldalára irányítson vissza
        if(mAuth.getCurrentUser() != null){
            //TODO
            goLoginPageFromRegister();
            finish();
        }


         //E-mail helyességét figyeli
        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //nope
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //nope
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(isValidEmail(s.toString())){
                    emailIcon.setVisibility(View.VISIBLE);
                    emailIcon.setImageResource(R.drawable.done_icon_green);
                }else {
                    emailIcon.setVisibility(View.VISIBLE);
                    emailIcon.setImageResource(R.drawable.done_icon_grey);
                }
            }

        });


        //ez ahhoz kell hogy a jelszo lathatsoagi kapcsolo megjelenjen akkor is ha mar hibaztunk egyzser a jelszoval bejelntkezeskor.
        //Ha mar vana  beviteli mezobe egy karakter is akkor megint lathatova teszi a "jelszo lathatosagi kapcsolot"
        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nope
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Nope
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkPasswordsMatch();
                if (s.length() > 0) {
                    passwordVisibilityIcon.setVisibility(View.VISIBLE);
                } else {
                    passwordVisibilityIcon.setVisibility(View.GONE); // Elrejti az ikont, ha nincs szöveg.
                }
            }
        });
        //ellenőri megegyezik e a 2 jelszo
        mPasswordAgain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                checkPasswordsMatch();
            }
        });


        //kapcsoló a jelszo láthatóságához
        mPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {

                    mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mPassword.setVisibility(View.VISIBLE);
                    passwordVisibilityIcon.setImageResource(R.drawable.eye_open_password);
                } else {
                    mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordVisibilityIcon.setImageResource(R.drawable.password_icon);
                }
            }
        });

        mPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isValidPhoneNumber(s.toString())) {
                    phoneNumberIcon.setImageResource(R.drawable.done_icon_green);
                    phoneNumberIcon.setVisibility(View.VISIBLE);
                } else {
                    phoneNumberIcon.setImageResource(R.drawable.done_icon_grey);
                    phoneNumberIcon.setVisibility(View.VISIBLE);
                }
            }
        });







    }

    private void checkPasswordsMatch() {
        if (mPassword.getText().toString().equals(mPasswordAgain.getText().toString())) {
            passwordMatchIcon.setImageResource(R.drawable.done_icon_green);
            passwordMatchIcon.setVisibility(View.VISIBLE);
        } else {
            passwordMatchIcon.setImageResource(R.drawable.done_icon_grey);
            passwordMatchIcon.setVisibility(View.VISIBLE);
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
            @SuppressLint("UseCompatLoadingForDrawables")
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

                    String defaultImageUrl = "gs://naturenavi-663e0.appspot.com/default_profile_picture.jpg";
                    user.put("profileImageUrl",defaultImageUrl);
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


    public void registerWithGoogle(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Figyelem");
        builder.setMessage("Jelenleg ez a regisztrációs módszer nem működik. Az applikáció karbantartás alatt áll.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    public void goMainPage(View view) {
        Intent mainIntent = new Intent(this, EnterActivity.class);
        startActivity(mainIntent);
    }
}