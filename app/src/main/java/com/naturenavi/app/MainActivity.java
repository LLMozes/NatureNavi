package com.naturenavi.app;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.naturenavi.app.adapter.TripItemAdapter;
import com.naturenavi.app.model.Trip;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


public class MainActivity extends AppCompatActivity {

    private FirebaseUser mUser;
    private FirebaseAuth mMirebaseAuth;
    private RecyclerView mRecyclerView;
    private RecyclerView mLegkorabbiRecyclerView;
    private ArrayList<Trip> mTripList;
    private ArrayList<Trip> mLegkorabbiTripList;
    private TripItemAdapter mItemAdapter;
    private TripItemAdapter mLegkorabbiItemAdapter;
    FirebaseFirestore mFirestore;
    private CollectionReference mTrips;

    private TextView legnepszerubbText,legkorabbanText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        legnepszerubbText = findViewById(R.id.textView5);
        legnepszerubbText.setText(Html.fromHtml("<u>Legnépszerűbb kirándulásaink:</u>"));


        legkorabbanText = findViewById(R.id.textView6);
        legkorabbanText.setText(Html.fromHtml("<u>Legkorábban induló kirándulásaink:</u>"));

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mMirebaseAuth = FirebaseAuth.getInstance();

        if(mUser == null){
            System.out.println("Nincs hitelesítve a user");
            finish();
        }else {
            System.out.println("Sikeres bejelentkezés");
        }

        mRecyclerView = findViewById(R.id.tripProfileRecyclerView);
        mLegkorabbiRecyclerView = findViewById(R.id.legkorabbiRecycleView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mLegkorabbiRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mTripList = new ArrayList<>();
        mLegkorabbiTripList = new ArrayList<>();

        mItemAdapter = new TripItemAdapter(this,mTripList);
        mLegkorabbiItemAdapter = new TripItemAdapter(this, mLegkorabbiTripList);

        mRecyclerView.setAdapter(mItemAdapter);
        mLegkorabbiRecyclerView.setAdapter(mLegkorabbiItemAdapter);


        mFirestore= FirebaseFirestore.getInstance();
        mTrips = mFirestore.collection("trips");

        queryTrips();
        queryEarliestTrips();
    }

    private void queryTrips() {
        mTripList.clear();

        mTrips.orderBy("participant", Query.Direction.DESCENDING).limit(8).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Trip trip = document.toObject(Trip.class);
                trip.setId(document.getId());
                mTripList.add(trip);
            }

            if (mTripList.size() == 0) {
                initializeTripData();
                queryTrips();
            }
            mItemAdapter.notifyDataSetChanged();
        });
    }

    private void queryEarliestTrips() {
        mLegkorabbiTripList.clear();

        mTrips.orderBy("startDate", Query.Direction.ASCENDING).limit(8).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Trip trip = document.toObject(Trip.class);
                trip.setId(document.getId());
                mLegkorabbiTripList.add(trip);
            }
            if (mLegkorabbiTripList.size() == 0) {
                initializeTripData();
                queryEarliestTrips();
            }

            mLegkorabbiItemAdapter.notifyDataSetChanged();
        });
    }


    private void initializeTripData() {
        String[] tripName = getResources().getStringArray(R.array.trip_names);
        String[] tripPrice = getResources().getStringArray(R.array.trip_price);
        String[] tripStartDate = getResources().getStringArray(R.array.trip_start_date);
        String[] tripEndDate = getResources().getStringArray(R.array.trip_end_date);
        String[] tripDescriptionText = getResources().getStringArray(R.array.trip_description_text);
        TypedArray tripsImageResources = getResources().obtainTypedArray(R.array.trip_images);

        AtomicInteger transactionCount = new AtomicInteger(tripName.length);

        for (int i = 0; i < tripName.length; i++) {
            final int index = i;
            DocumentReference newTripRef = mTrips.document(tripName[i]);
            FirebaseFirestore.getInstance().runTransaction(transaction -> {
                DocumentSnapshot snapshot = transaction.get(newTripRef);
                if (!snapshot.exists()) {
                    Trip newTrip = new Trip(tripPrice[index], tripStartDate[index], tripEndDate[index], tripDescriptionText[index],
                            tripName[index], tripsImageResources.getResourceId(index, 0) , 0);
                    newTrip.setId(newTripRef.getId());
                    transaction.set(newTripRef, newTrip);
                    return null;
                }
                return null;
            }).addOnSuccessListener(aVoid -> {
                if (transactionCount.decrementAndGet() == 0) {
                    tripsImageResources.recycle();
                }
            }).addOnFailureListener(e -> {
                Log.e("Transaction", "Transaction failure.", e);
                if (transactionCount.decrementAndGet() == 0) {
                    tripsImageResources.recycle();
                }
            });
        }
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }

    public void goProfilePage(View view) {
        startActivity(new Intent(this,ProfileActivity.class));
    }

    public void deleteTrip(View view) {
        mMirebaseAuth = FirebaseAuth.getInstance();
        mUser = mMirebaseAuth.getCurrentUser();
        if (mUser != null) {

            String tripId = getIntent().getStringExtra("trip_id");

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference tripRef = db.collection("trips").document(tripId);

            tripRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("TripDescriptionActivity", "Trip successfully deleted!");
                        Toast.makeText(this, "Trip deleted successfully!", Toast.LENGTH_SHORT).show();
                        showSuccessDialog();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("TripDescriptionActivity", "Error deleting trip", e);
                        Toast.makeText(this, "Failed to delete trip", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.e("TripDescriptionActivity", "User not logged in");
            Toast.makeText(this, "You need to be logged in to delete trips", Toast.LENGTH_SHORT).show();
        }


    }

    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Törlés megerősítése");
        builder.setMessage("Gratulálok, sikeres törlést hajtottál végre!");
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();  // Dialog bezárása az OK gombra kattintva
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}