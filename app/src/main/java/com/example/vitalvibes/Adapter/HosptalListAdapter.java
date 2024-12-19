package com.example.vitalvibes.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vitalvibes.Activities.HospitalDetail;
import com.example.vitalvibes.databinding.ViewholderNearbyHospitalBinding;
import com.example.vitalvibes.model.Hospital;

import java.util.ArrayList;

public class HosptalListAdapter extends RecyclerView.Adapter<HosptalListAdapter.Viewholder> {
    ArrayList<Hospital> hospitalsList;
    Context context;
    ViewholderNearbyHospitalBinding binding;

    public HosptalListAdapter(ArrayList<Hospital> hospitalsList) {
        this.hospitalsList = hospitalsList;
    }

    @NonNull
    @Override
    public HosptalListAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding = ViewholderNearbyHospitalBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        context = parent.getContext();
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HosptalListAdapter.Viewholder holder, int position) {
        Hospital hospital = hospitalsList.get(position); // Get the current hospital

        binding.nameHospital.setText(hospital.getName());
        binding.address.setText(hospital.getAddress());
        binding.rating.setText(String.valueOf(hospital.getRating())); // Convert rating to String
        binding.distance.setText(hospital.getNearest());

        Glide.with(context)
                .load(hospital.getPic().get(0)) // Load the first picture
                .into(binding.pic);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, HospitalDetail.class);
            intent.putExtra("object", hospitalsList.get(position)); // Single object
            context.startActivity(intent);
        });

    }


    @Override
    public int getItemCount() {
        return hospitalsList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        public Viewholder(ViewholderNearbyHospitalBinding binding) {
            super(binding.getRoot());
        }
    }
}
