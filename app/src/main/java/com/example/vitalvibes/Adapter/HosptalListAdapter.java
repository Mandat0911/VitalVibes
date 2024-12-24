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
        if (hospitalsList != null) {
            this.hospitalsList = hospitalsList;
            this.hospitalsListFiltered = new ArrayList<>(hospitalsList); // Make a copy for filtering
        } else {
            this.hospitalsList = new ArrayList<>();
            this.hospitalsListFiltered = new ArrayList<>();
        }
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
        if (hospitalsListFiltered != null && !hospitalsListFiltered.isEmpty()) {
            Hospital hospital = hospitalsListFiltered.get(position); // Get the filtered list of hospitals

            binding.nameHospital.setText(hospital.getSiteName());
            binding.address.setText(hospital.getAddress());
            binding.HospitalPhoneNumber.setText(hospital.getMobile());  // Modify as per distance logic if needed
            binding.detailDescription.setText(hospital.getHospitalBio());

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
                intent.putExtra("object", hospital); // Pass hospital object to detail screen
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return hospitalsListFiltered != null ? hospitalsListFiltered.size() : 0; // Safely return filtered list size
    }

    public void filter(String query) {
        if (hospitalsList != null && !hospitalsList.isEmpty()) {
            hospitalsListFiltered.clear();

            if (query.isEmpty()) {
                // If query is empty, show all hospitals
                hospitalsListFiltered.addAll(hospitalsList);
            } else {
                query = query.toLowerCase();

                for (Hospital hospital : hospitalsList) {
                    String siteName = hospital.getSiteName();
                    String address = hospital.getAddress();

                    // Add null checks before applying the filter
                    boolean matchesSiteName = siteName != null && siteName.toLowerCase().contains(query);
                    boolean matchesAddress = address != null && address.toLowerCase().contains(query);

                    if (matchesSiteName || matchesAddress) {
                        hospitalsListFiltered.add(hospital); // Add matching hospitals
                    }
                }
            }
            notifyDataSetChanged(); // Notify adapter about data change
        }
    }

    public void removeItem(int position) {
        // Remove the hospital from the list
        if (position >= 0 && position < hospitalsListFiltered.size()) {
            hospitalsListFiltered.remove(position);
            notifyItemRemoved(position); // Notify adapter about the removed item
        }
    }
    // Method to find the position of the hospital in the list (you can customize this based on your data)
    private int getHospitalPosition(String hospitalId) {
        for (int i = 0; i < hospitalsListFiltered.size(); i++) {
            if (hospitalsListFiltered.get(i).getHospitalId().equals(hospitalId)) {
                return i;
            }
        }
        return -1; // Return -1 if the hospital was not found
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        public Viewholder(ViewholderNearbyHospitalBinding binding) {
            super(binding.getRoot());
        }
    }
}
