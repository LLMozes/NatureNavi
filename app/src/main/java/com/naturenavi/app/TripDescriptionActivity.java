
package com.naturenavi.app;

import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import com.naturenavi.app.model.Trip;
import com.naturenavi.app.model.User;


public class TripDescriptionActivity extends AppCompatActivity {

    private TextView nameTxt, descriptionTxt, priceTxt,indulasText,erkezesText;
    private Button cancelButton,bookButton;
    private ImageView tripImg;
    private FirebaseFirestore mFirestoreDb;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_description);

        initializeFirebase();
        initializeViews();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (currentUser == null) {
            currentUser = mAuth.getCurrentUser(); // Biztosítjuk, hogy a currentUser friss legyen
        }
        String tripId = getIntent().getStringExtra("trip_id");
        loadTripDetails(tripId);

    }



    private void initializeFirebase() {
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }
        mAuth = FirebaseAuth.getInstance();
        mFirestoreDb = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    private void initializeViews() {
        nameTxt = findViewById(R.id.TripName);
        descriptionTxt = findViewById(R.id.description);
        priceTxt = findViewById(R.id.TripePrice);
        tripImg = findViewById(R.id.descriptionImage);
        bookButton = findViewById(R.id.lefoglalButton);
        cancelButton = findViewById(R.id.lemondbutton);
        indulasText = findViewById(R.id.indulasText);
        erkezesText = findViewById(R.id.erkezesText);
    }

    private void loadTripDetails(String tripId) {
        Log.d("TripDescriptionActivity", "Betöltés megkezdése, trip ID: " + tripId);
        mFirestoreDb.collection("trips").document(tripId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Trip trip = documentSnapshot.toObject(Trip.class);
                if (trip != null) {
                    nameTxt.setText(trip.getName());
                    descriptionTxt.setText(trip.getDescription());
                    priceTxt.setText(trip.getPrice());
                    indulasText.setText(trip.getStartDate());
                    erkezesText.setText(trip.getEndDate());
                    if (trip.getImageResource() != 0) {
                        Glide.with(this)
                                .load(trip.getImageResource())
                                .into(tripImg);


                    } else {
                        Log.d("TripDescriptionActivity", "Nincs érvényes kép azonosító.");
                    }

                    Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
                    priceTxt.startAnimation(bounce);

                } else {
                    Log.e("TripDescriptionActivity", "Trip objektum null.");
                }
            } else {
                Log.e("TripDescriptionActivity", "Az utazás nem található.");
            }
        }).addOnFailureListener(e -> {
            Log.e("TripDescriptionActivity", "Hiba történt az utazás adatainak lekérésekor: " + e.getMessage(), e);
        });

        checkIfTripBooked(tripId);
    }


    public void toBook(View view) {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String tripId = getIntent().getStringExtra("trip_id");

            DocumentReference userRef = mFirestoreDb.collection("users").document(userId);
            userRef.update("bookedTripIds", FieldValue.arrayUnion(tripId))
                    .addOnSuccessListener(aVoid -> {
                        Log.d("TripDescriptionActivity", "Trip successfully booked!");
                        Toast.makeText(this, "Trip booked successfully!", Toast.LENGTH_SHORT).show();
                        disableBookingButton();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("TripDescriptionActivity", "Error booking trip", e);
                        Toast.makeText(this, "Failed to book trip", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.e("TripDescriptionActivity", "User not logged in");
            Toast.makeText(this, "You need to be logged in to book trips", Toast.LENGTH_SHORT).show();
        }
    }

    public void toCancel(View view) {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String tripId = getIntent().getStringExtra("trip_id");

            DocumentReference userRef = mFirestoreDb.collection("users").document(userId);
            userRef.update("bookedTripIds", FieldValue.arrayRemove(tripId))
                    .addOnSuccessListener(aVoid -> {
                        Log.d("TripDescriptionActivity", "Trip successfully canceled!");
                        Toast.makeText(this, "Trip canceled successfully!", Toast.LENGTH_SHORT).show();
                        enableBookingButton();  // Ha szükséges, újra engedélyezheted a foglalás gombot
                    })
                    .addOnFailureListener(e -> {
                        Log.e("TripDescriptionActivity", "Error canceling trip", e);
                        Toast.makeText(this, "Failed to cancel trip", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.e("TripDescriptionActivity", "User not logged in");
            Toast.makeText(this, "You need to be logged in to cancel trips", Toast.LENGTH_SHORT).show();
        }
    }


    private void disableBookingButton() {
        bookButton.setEnabled(false);
        bookButton.setBackgroundColor(Color.GRAY);
        cancelButton.setEnabled(true);
        cancelButton.setBackgroundColor(Color.RED);
    }
    private void enableBookingButton() {
        bookButton.setEnabled(true);
        bookButton.setBackgroundColor(getColor(R.color.my_primary));
        cancelButton.setEnabled(false);
        cancelButton.setBackgroundColor(Color.GRAY);
    }

    private void checkIfTripBooked(String tripId) {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userRef = mFirestoreDb.collection("users").document(userId);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null && user.getBookedTripIds() != null ) {
                        if(user.getBookedTripIds().contains(tripId)){
                            disableBookingButton();
                        }else {
                            enableBookingButton();
                        }

                    }
                }
            }).addOnFailureListener(e -> {
                Log.e("TripDescriptionActivity", "Error retrieving user info: " + e.getMessage(), e);
            });
        }
    }


    public void goBack(View view) {
        String origin = getIntent().getStringExtra("origin");
        Intent intent;
        if ("ProfileActivity".equals(origin)) {
            intent = new Intent(this, ProfileActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }

}








