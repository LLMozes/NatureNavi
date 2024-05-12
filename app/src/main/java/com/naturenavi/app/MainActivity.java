package com.naturenavi.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.naturenavi.app.adapter.TripItemAdapter;
import com.naturenavi.app.model.Trip;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private FirebaseUser mUser;
    private FirebaseAuth mMirebaseAuth;
    private RecyclerView mRecyclerView;
    private ArrayList<Trip> mTripList;
    private TripItemAdapter mItemAdapter;

    FirebaseFirestore mFirestore;
    private CollectionReference mTrips;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mMirebaseAuth = FirebaseAuth.getInstance();

        if(mUser == null){
            System.out.println("Nincs hitelesítve a user");
            finish();
        }else {
            System.out.println("Sikeres bejelentkezés");
        }

        mRecyclerView = findViewById(R.id.tripProfileRecyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mTripList = new ArrayList<>();

        mItemAdapter = new TripItemAdapter(this,mTripList);
        mRecyclerView.setAdapter(mItemAdapter);



        mFirestore= FirebaseFirestore.getInstance();
        mTrips = mFirestore.collection("trips");

        queryTrips();

    }

    private void queryTrips() {
        mTripList.clear();

        mTrips.orderBy("participant", Query.Direction.DESCENDING).limit(10).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Trip trip = document.toObject(Trip.class);
                trip.setId(document.getId());
                mTripList.add(trip);
            }

            if (mTripList.size() == 0) {
                initalizeTripData();
                queryTrips();
            }
            mItemAdapter.notifyDataSetChanged();
        });
    }

    private void initalizeTripData() {
        String[] tripName = getResources().getStringArray(R.array.trip_names);
        String[] tripPrice = getResources().getStringArray(R.array.trip_price);
        String[] tripStartDate = getResources().getStringArray(R.array.trip_start_date);
        String[] tripEndDate = getResources().getStringArray(R.array.trip_end_date);
        TypedArray tripsImageResources = getResources().obtainTypedArray(R.array.trip_images);

        for (int i = 0; i < tripName.length; i++) {
            Trip newTrip = new Trip(tripPrice[i], tripStartDate[i], tripEndDate[i], "nem kell még",
                    tripName[i], tripsImageResources.getResourceId(i,0), 0);
            DocumentReference newTripRef = mTrips.document(); // Létrehoz egy új dokumentum referenciát egyedi ID-val
            newTrip.setId(newTripRef.getId()); // Beállítja az új Trip ID-jét
            newTripRef.set(newTrip); // Hozzáadja az új Trip-et a Firestore-hoz
        }
        tripsImageResources.recycle();
    }







    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this,LoginActivity.class));
        finish();

    }

    public void goProfilePage(View view) {
        startActivity(new Intent(this,ProfileActivity.class));
    }
}