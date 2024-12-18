package com.example.vitalvibes.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
        context = parent.getContext();
        binding = ViewholderNearbyHospitalBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HosptalListAdapter.Viewholder holder, int position) {
        binding.nameHospital.setText(hospitalsList.get(position).getName());
        binding.address.setText(hospitalsList.get(position).getAddress());
        binding.rating.setText("" + hospitalsList.get(position).getRating());
        binding.distance.setText(hospitalsList.get(position).getNearest());

        Glide.with(context)
                .load(hospitalsList.get(position).getPic().get(0))
                .into(binding.pic);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
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
