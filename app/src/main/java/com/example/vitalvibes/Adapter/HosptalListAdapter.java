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
import com.example.vitalvibes.R;
import com.example.vitalvibes.databinding.ViewholderNearbyHospitalBinding;
import com.example.vitalvibes.model.Hospital;

import java.util.ArrayList;
import java.util.List;

public class HosptalListAdapter extends RecyclerView.Adapter<HosptalListAdapter.Viewholder> {
    private ArrayList<Hospital> hospitalsList;
    private ArrayList<Hospital> hospitalsListFiltered; // For filtered list
    private Context context;
    private ViewholderNearbyHospitalBinding binding;

    public HosptalListAdapter(ArrayList<Hospital> hospitalsList) {
        this.hospitalsList = hospitalsList;
        this.hospitalsListFiltered = new ArrayList<>(hospitalsList); // Make a copy for filtering
    }

    @NonNull
    @Override
    public HosptalListAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ViewholderNearbyHospitalBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        context = parent.getContext();
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        Hospital hospital = hospitalsListFiltered.get(position); // Get the filtered list of hospitals

        binding.nameHospital.setText(hospital.getSiteName());
        binding.address.setText(hospital.getAddress());
        binding.distance.setText(hospital.getAddress());  // Modify as per distance logic if needed

        // Check if hospital has images and load the first one using Glide
        if (hospital.getPic() != null && !hospital.getPic().isEmpty()) {
            Glide.with(context)
                    .load(hospital.getPic().get(0)) // Load the first picture if available
                    .into(binding.pic);
        } else {
            // Handle case where no picture is available (optional)
            Glide.with(context)
                    .load(R.drawable.ic_launcher_background) // Set a default image if no picture is available
                    .into(binding.pic);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, HospitalDetail.class);
            intent.putExtra("object", hospitalsListFiltered.get(position)); // Pass hospital object to detail screen
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return hospitalsListFiltered.size(); // Use filtered list size
    }

    // Filter method to filter hospitals based on the query
    public void filter(String query) {
        hospitalsListFiltered.clear();
        if (query.isEmpty()) {
            hospitalsListFiltered.addAll(hospitalsList); // If query is empty, show all hospitals
        } else {
            query = query.toLowerCase();
            for (Hospital hospital : hospitalsList) {
                // Match query with hospital name and address
                if (hospital.getSiteName().toLowerCase().contains(query) ||
                        hospital.getAddress().toLowerCase().contains(query)) {
                    hospitalsListFiltered.add(hospital); // Add matching hospitals
                }
            }
        }
        notifyDataSetChanged(); // Notify adapter about data change
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        public Viewholder(ViewholderNearbyHospitalBinding binding) {
            super(binding.getRoot());
        }
    }
}

