package com.naturenavi.app.adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.naturenavi.app.R;
import com.naturenavi.app.RegisterActivity;
import com.naturenavi.app.TripDescriptionActivity;
import com.naturenavi.app.model.Trip;

import java.util.ArrayList;

public class TripItemAdapter extends RecyclerView.Adapter<TripItemAdapter.ViewHolder> {

    private ArrayList<Trip> mTripItemData = new ArrayList<>();
    private ArrayList<Trip> mTripItemDataAll = new ArrayList<>();  // ez a szures miatt kell majd
    private Context mContext;


    public TripItemAdapter(Context context, ArrayList<Trip> itemsData) {
        this.mTripItemData = itemsData;
        this.mTripItemDataAll = itemsData;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.trip_item,parent,false);
        return new ViewHolder(inflate);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(TripItemAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //Trip currentTrip = mTripItemData.get(position);
        //holder.nameTxt.setText(currentTrip.getName());
        holder.nameTxt.setText(mTripItemData.get(position).getName());
        holder.startDateTxt.setText(mTripItemData.get(position).getStartDate());
        holder.endDateTxt.setText(mTripItemData.get(position).getEndDate());
        holder.priceTxt.setText(mTripItemData.get(position).getPrice());
        holder.priceTxt.setText(mTripItemData.get(position).getPrice());
        Glide.with(mContext)
                .load(mTripItemData.get(position).getImageResource())
                .transform(new CenterCrop(),new RoundedCorners(30))
                .load(holder.itemImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTripClick(mTripItemData.get(position));
            }
        });


    }

    public void onTripClick(Trip trip) {
        Intent intent = new Intent(mContext, TripDescriptionActivity.class);
        intent.putExtra("trip_id", trip.getId());
        mContext.startActivity(intent);
    }



    @Override
    public int getItemCount() {
        return mTripItemData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTxt,startDateTxt,endDateTxt,priceTxt;
        private ImageView itemImage;
        public ViewHolder(View itemView){
            super(itemView);
            nameTxt = itemView.findViewById(R.id.tripNameText);
            startDateTxt = itemView.findViewById(R.id.startDateText);
            endDateTxt = itemView.findViewById(R.id.finishDateText);
            priceTxt = itemView.findViewById(R.id.tripPriceTextNumber);
            itemImage = itemView.findViewById(R.id.tripImage);

        }

        public void bindTo(Trip currentTrip) {

        }
    }




}
