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
    public void onBindViewHolder(@NonNull HosptalListAdapter.Viewholder holder, int position) {
        Hospital hospital = hospitalsListFiltered.get(position); // Get the filtered list of hospitals

        binding.nameHospital.setText(hospital.getName());
        binding.address.setText(hospital.getAddress());
        binding.rating.setText(String.valueOf(hospital.getRating()));
        binding.distance.setText(hospital.getNearest());

        Glide.with(context)
                .load(hospital.getPic().get(0)) // Load the first picture
                .into(binding.pic);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, HospitalDetail.class);
            intent.putExtra("object", hospitalsListFiltered.get(position)); // Single object
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
                if (hospital.getName().toLowerCase().contains(query) ||
                        hospital.getName().toLowerCase().contains(query)) {
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

