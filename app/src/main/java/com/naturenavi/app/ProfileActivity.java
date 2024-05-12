package com.naturenavi.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.naturenavi.app.adapter.TripItemAdapter;
import com.naturenavi.app.model.Trip;
import com.naturenavi.app.model.User;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    private RecyclerView mRecyclerView;
    private ArrayList<Trip> mUserTripList;
    private TripItemAdapter mItemAdapter;
    TextView fullName,email,phone;

    FirebaseFirestore mFirestoreDb;
    String userId;
    private DocumentReference userRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mFirestoreDb = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        userRef = mFirestoreDb.collection("users").document(userId);

        fullName = findViewById(R.id.profileNameTextView);
        phone = findViewById(R.id.profilePhone);
        email = findViewById(R.id.profileEmail);
        mRecyclerView = findViewById(R.id.tripProfileRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mUserTripList = new ArrayList<>();
        mItemAdapter = new TripItemAdapter(this, mUserTripList);
        mRecyclerView.setAdapter(mItemAdapter);

        queryUserProfile();
        loadUserTrips();
    }
    private void loadUserTrips() {
        // Kézzel kiválasztott tripId
        String manualTripId = "9CC1KFAgBR0wWYKI2Hrs";  // Példaként ezt a tripId-t használtuk

        // Lekérdezzük a Firestore-ból az adott tripId-hoz tartozó utazást
        mFirestoreDb.collection("trips").document(manualTripId).get().addOnSuccessListener(tripSnapshot -> {
            if (tripSnapshot.exists()) {
                Trip trip = tripSnapshot.toObject(Trip.class);
                mUserTripList.add(trip);
                mItemAdapter.notifyDataSetChanged();
            } else {
                Log.e("ProfileActivity", "A keresett trip nem található az adatbázisban.");
            }
        }).addOnFailureListener(e -> {
            Log.e("ProfileActivity", "Hiba történt az adatok lekérdezése során: " + e.toString());
        });
    }


    /*
    private void loadUserTrips() {
        mUserTripList.clear();


        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User user = documentSnapshot.toObject(User.class);
                List<String> bookedTripIds = user.getBookedTripIds();
                for (String tripId : bookedTripIds) {
                    mFirestoreDb.collection("trips").document(tripId).get().addOnSuccessListener(tripSnapshot -> {
                        if (tripSnapshot.exists()) {
                            Trip trip = tripSnapshot.toObject(Trip.class);
                            mUserTripList.add(trip);
                            mItemAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

     */

    private void queryUserProfile() {
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User user = documentSnapshot.toObject(User.class);
                fullName.setText(user.getFullName());
                phone.setText(user.getPhoneNumber());
                email.setText(user.getEmail());
            }
        });
    }


    public void goMainPage(View view) {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }
}