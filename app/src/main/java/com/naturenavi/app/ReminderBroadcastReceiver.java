package com.naturenavi.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.naturenavi.app.model.Trip;
import com.naturenavi.app.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class ReminderBroadcastReceiver extends android.content.BroadcastReceiver {
// Müködése : Alarm Manager-hez
    // elvileg 1 percenkét küld ki értesítést hogy a legkozelebbi lefoglalt kirandulásig mennyi idő van még.SS

    FirebaseFirestore db;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("ReminderBroadcast","onReceive triggered");
         db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Log.d("ReminderBroadcast", "User data successfully fetched");
                User user = documentSnapshot.toObject(User.class);
                List<String> bookedTripIds = user.getBookedTripIds();
                findNextTrip(context, bookedTripIds);
            }else {
                Log.d("ReminderBroadcast", "No user data found");
            }
        });

    }

    private void findNextTrip(Context context, List<String> tripIds) {
        Log.d("ReminderBroadcast", "Finding next trip");
        db = FirebaseFirestore.getInstance();
        long now = System.currentTimeMillis();
        AtomicReference<Trip> nextTrip = new AtomicReference<>(null);
        AtomicLong minTimeDiff = new AtomicLong(Long.MAX_VALUE);
        AtomicInteger count = new AtomicInteger(tripIds.size()); // Számláló az aszinkron válaszok nyomon követésére

        for (String tripId : tripIds) {
            db.collection("trips").document(tripId).get().addOnSuccessListener(documentSnapshot -> {
                Trip trip = documentSnapshot.toObject(Trip.class);
                long tripStartMillis = parseDate(trip.getStartDate());
                if (trip != null && tripStartMillis > now && (tripStartMillis - now) < minTimeDiff.get()) {
                    nextTrip.set(trip);
                    minTimeDiff.set(tripStartMillis - now);
                }

                // Ellenőrizzük, hogy minden hívás befejeződött-e
                if (count.decrementAndGet() == 0 && nextTrip.get() != null) {
                    sendNotification(context, nextTrip.get(), minTimeDiff.get());
                }
            }).addOnFailureListener(e -> {
                // Csökkentjük a számlálót hiba esetén is
                if (count.decrementAndGet() == 0 && nextTrip.get() != null) {
                    sendNotification(context, nextTrip.get(), minTimeDiff.get());
                }
            });
        }
    }

    private void sendNotification(Context context, Trip trip, long timeDiff) {
        Log.d("ReminderBroadcast", "Sending notification for trip: " + trip.getName());
        long hours = TimeUnit.MILLISECONDS.toDays(timeDiff);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "reminder_channel")
                .setSmallIcon(R.drawable.kirandulo)
                .setContentTitle("Közelgő kirándulás: " + trip.getName())
                .setContentText("Várható indulás " + hours + " nap múla. Készülj fel addig !:)")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{0, 300, 300, 300});

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("reminder_channel", "Kirándulás Emlékeztető", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        Log.d("ReminderBroadcast", "Notifying with notification for trip: " + trip.getName());

        notificationManager.notify(200, builder.build());
    }

    private long parseDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd.", Locale.getDefault());
        try {
            Date date = dateFormat.parse(dateString);
            Log.d("ReminderBroadcast", "Parsed date: " + date.toString());
            return date.getTime(); // Visszaadja az időt milliszekundumban
        } catch (ParseException e) {
            Log.e("ProfileActivity", "Date parsing error: " + e.getMessage());
            Log.e("ReminderBroadcast", "Date parsing error", e);
            return -1;
        }
    }




}
