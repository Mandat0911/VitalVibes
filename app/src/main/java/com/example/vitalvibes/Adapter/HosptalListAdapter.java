package com.example.vitalvibes.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.vitalvibes.Activities.HospitalDetail;
import com.example.vitalvibes.R;
import com.example.vitalvibes.databinding.ViewholderNearbyHospitalBinding;
import com.example.vitalvibes.model.Hospital;
import java.util.ArrayList;
import java.util.HashSet;

public class HosptalListAdapter extends RecyclerView.Adapter<HosptalListAdapter.Viewholder> {
    private ArrayList<Hospital> hospitalsList; // Master list
    private ArrayList<Hospital> hospitalsListFiltered; // For filtered view
    private Context context;

    private ViewholderNearbyHospitalBinding binding;
    public HosptalListAdapter(ArrayList<Hospital> hospitalsList) {
        if (hospitalsList != null) {
            this.hospitalsList = new ArrayList<>(new HashSet<>(hospitalsList)); // Remove duplicates
            this.hospitalsListFiltered = new ArrayList<>(this.hospitalsList); // Copy for filtering
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
    public void onBindViewHolder(@NonNull HosptalListAdapter.Viewholder holder, int position) {
        Hospital hospital = hospitalsListFiltered.get(position);

        // Bind data to the UI
        holder.binding.nameHospital.setText(hospital.getSiteName());
        holder.binding.address.setText(hospital.getAddress());
        holder.binding.HospitalPhoneNumber.setText(hospital.getMobile());
        holder.binding.detailDescription.setText(hospital.getHospitalBio());

        // Load image using Glide
        if (hospital.getPic() != null && !hospital.getPic().isEmpty()) {
            Glide.with(context)
                    .load(hospital.getPic().get(0))
                    .into(holder.binding.pic);
        } else {
            Glide.with(context)
                    .load(R.drawable.ic_launcher_background)
                    .into(holder.binding.pic);
        }

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, HospitalDetail.class);
            intent.putExtra("object", hospital);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return hospitalsListFiltered.size();
    }


    // Filter hospitals based on query
    public void filter(String query) {
        HashSet<Hospital> filteredSet = new HashSet<>();
        if (query.isEmpty()) {
            filteredSet.addAll(hospitalsList);
        } else {
            for (Hospital hospital : hospitalsList) {
                if ((hospital.getSiteName() != null && hospital.getSiteName().toLowerCase().contains(query.toLowerCase()))
                        || (hospital.getAddress() != null && hospital.getAddress().toLowerCase().contains(query.toLowerCase()))) {
                    filteredSet.add(hospital);
                }
            }
        }
        hospitalsListFiltered.clear();
        hospitalsListFiltered.addAll(filteredSet);
        notifyDataSetChanged();
    }


    public class Viewholder extends RecyclerView.ViewHolder {
        ViewholderNearbyHospitalBinding binding;

        public Viewholder(ViewholderNearbyHospitalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
