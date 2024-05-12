package com.naturenavi.app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.naturenavi.app.model.Trip;

public class TripDescriptionActivity extends AppCompatActivity {


    private TextView nameTxt,descriptionTxt,priceTxt;
    private ImageView TripImg;
    FirebaseFirestore mFirestoreDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_description);

        mFirestoreDb = FirebaseFirestore.getInstance();

        nameTxt = findViewById(R.id.TripName);
        descriptionTxt = findViewById(R.id.description);
        priceTxt = findViewById(R.id.TripePrice);


        String tripId = getIntent().getStringExtra("trip_id");

        loadTripDetails(tripId);

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
                    Log.d("TripDescriptionActivity", "Trip adatai betöltve: " + trip.getName());

                    if (trip.getImageResource() != 0) {
                        Log.d("TripDescriptionActivity", "Kép betöltése az erőforrás azonosítóból: " + trip.getImageResource());
                        //Glide.with(TripDescriptionActivity.this)
                                //.load(trip.getImageResource())
                                //.into(TripImg);
                    } else {
                        Log.d("TripDescriptionActivity", "Nincs érvényes kép azonosító.");
                    }
                } else {
                    Log.e("TripDescriptionActivity", "Trip objektum null.");
                }
            } else {
                Log.e("TripDescriptionActivity", "Az utazás nem található.");
            }
        }).addOnFailureListener(e -> {
            Log.e("TripDescriptionActivity", "Hiba történt az utazás adatainak lekérésekor: " + e.getMessage(), e);
        });
    }




    public void goBack(View view) {
        startActivity(new Intent(this,MainActivity.class));
    }
}



