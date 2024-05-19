package com.naturenavi.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
        EditText emailEditText;
        EditText passwordEditText;
        ImageView switchPasswordVisibilty;
        ImageView emailIcon;
        FirebaseAuth mAuth;
        TextView forgetPassword;

        private GoogleSignInAccount mGoogleSignInAccount;


    private static final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.[a-z]{2,}";
    private boolean isValidEmail(String email) {
        return email.matches(emailPattern);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        switchPasswordVisibilty = findViewById(R.id.switchPasswordVisibility);
        switchPasswordVisibilty.setVisibility(View.GONE); // alapjáraton ne latszodjon a szem csak ha van mar benne jelszo
        emailIcon = findViewById(R.id.correctEmail);


        //email helyességére reagáló pipa
        emailEditText.addTextChangedListener(new TextWatcher() {
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
        passwordEditText.addTextChangedListener(new TextWatcher() {
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
                if (s.length() > 0) {
                    switchPasswordVisibilty.setVisibility(View.VISIBLE);
                } else {
                    switchPasswordVisibilty.setVisibility(View.GONE); // Elrejti az ikont, ha nincs szöveg.
                }
            }
        });

        //kapcsoló a jelszo láthatóságához
        switchPasswordVisibilty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordEditText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {

                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    switchPasswordVisibilty.setVisibility(View.VISIBLE);
                    switchPasswordVisibilty.setImageResource(R.drawable.eye_open_password);
                } else {
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    switchPasswordVisibilty.setImageResource(R.drawable.password_icon);
                }
            }
        });

    }

    public void goMainPage(View view) {
        Intent mainIntent = new Intent(this, EnterActivity.class);
        startActivity(mainIntent);
    }

    public void loginWithGoogle(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Figyelem");
        builder.setMessage("Jelenleg ez a bejelentkezési módszer nem működik. Az applikáció karbantartás alatt áll.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void login(View view) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if(TextUtils.isEmpty(email)  ){
            emailIcon.setVisibility(View.GONE);
            emailEditText.setError("Email is required.");
            return;
        }else if(!isValidEmail(email)) {
            emailIcon.setVisibility(View.GONE);
            emailEditText.setError("The email address is not valid");
            return;
        }

        if(TextUtils.isEmpty(password)){
            switchPasswordVisibilty.setVisibility(View.GONE);
            passwordEditText.setError("Password is required.");
            return;
        }
        if(password.length() <6 ){
            switchPasswordVisibilty.setVisibility(View.GONE);
            passwordEditText.setError("Password msut be >= 6 characters");
            return;
        }

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Sikeres művelet!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                }else {
                    Toast.makeText(LoginActivity.this, "Hiba! Valami bibi van a hattérben:" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }








}