package com.naturenavi.app.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.naturenavi.app.MainActivity;
import com.naturenavi.app.ProfileActivity;
import com.naturenavi.app.R;
import com.naturenavi.app.TripDescriptionActivity;
import com.naturenavi.app.model.Trip;

import java.util.ArrayList;

public class TripItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Trip> mTripItemData;
    // private ArrayList<Trip> mTripItemDataAll = new ArrayList<>();  // ez a szures miatt kell majd
    private Context mContext;
    private boolean showEmptyState = false;  // abban az esetben a profil oldlaon ha nincs még kirándulás lefoglalava a

    public TripItemAdapter(Context context, ArrayList<Trip> itemsData) {
        this.mTripItemData = itemsData;
       // this.mTripItemDataAll = itemsData;
        this.mContext = context;
    }

    public TripItemAdapter(Context context, ArrayList<Trip> itemsData, boolean showEmptyState) {
        this.mTripItemData = itemsData;
        this.mContext = context;
        this.showEmptyState = showEmptyState;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.trip_item, parent, false);
            return new ViewHolder(inflate);
        }else {
            View emptyView = LayoutInflater.from(mContext).inflate(R.layout.empty_view, parent, false);
            return new EmptyViewHolder(emptyView);
        }

    }
    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            Trip trip = mTripItemData.get(position);
            if (trip.getId() != null) {
                viewHolder.itemView.setOnClickListener(v -> onTripClick(trip));
            } else {
                Log.e("AdapterError", "Trip ID is null for position " + position);
            }
            viewHolder.nameTxt.setText(trip.getName());
            viewHolder.startDateTxt.setText(trip.getStartDate());
            viewHolder.endDateTxt.setText(trip.getEndDate());
            Glide.with(mContext)
                    .load(trip.getImageResource())
                    .transition(DrawableTransitionOptions.withCrossFade(1000))
                    .into(viewHolder.itemImage);

            viewHolder.itemView.setOnClickListener(v -> onTripClick(trip));
        } else if (holder instanceof EmptyViewHolder) {
            EmptyViewHolder emptyViewHolder = (EmptyViewHolder) holder;
            emptyViewHolder.itemView.setOnClickListener(v -> onEmptyViewClick());
        }
    }

    public void onTripClick(Trip trip) {
        if (trip != null && trip.getId() != null) {
            Intent intent = new Intent(mContext, TripDescriptionActivity.class);
            intent.putExtra("trip_id", trip.getId());
            if (mContext instanceof MainActivity) {
                intent.putExtra("origin", "MainActivity");
            } else if (mContext instanceof ProfileActivity) {
                intent.putExtra("origin", "ProfileActivity");
            }
            mContext.startActivity(intent);
        } else {
            // Itt kezeljük le, ha a trip vagy a trip ID null
            Toast.makeText(mContext, "Hiba történt a kirándulás betöltésekor", Toast.LENGTH_LONG).show();
        }
    }
    private void onEmptyViewClick() {
        Intent intent = new Intent(mContext, MainActivity.class);
        mContext.startActivity(intent);
    }



    @Override
    public int getItemCount() {
        return mTripItemData.size() > 0 ? mTripItemData.size() : (showEmptyState ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return mTripItemData.size() > 0 ? 0 : 1;  // Ha nincs adat, akkor az üres nézet
    }



    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTxt,startDateTxt,endDateTxt,priceTxt;
        private ImageView itemImage;

        public ViewHolder(View itemView){
            super(itemView);
            nameTxt = itemView.findViewById(R.id.tripNameText);
            startDateTxt = itemView.findViewById(R.id.startDateText);
            endDateTxt = itemView.findViewById(R.id.finishDateText);
           // priceTxt = itemView.findViewById(R.id.tripPriceTextNumber);
            itemImage = itemView.findViewById(R.id.tripImage);

        }

    }

    class EmptyViewHolder extends RecyclerView.ViewHolder {
        EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }




}
