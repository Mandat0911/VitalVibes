package com.example.vitalvibes.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vitalvibes.databinding.ViewholderUsersBinding;
import com.example.vitalvibes.model.Donor;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Donor> donorList;
    private DatabaseReference databaseReference;
    private OnDeleteUserListener onDeleteUserListener;

    // Constructor
    public UserListAdapter(Context context, ArrayList<Donor> donorList, OnDeleteUserListener onDeleteUserListener) {
        this.context = context;
        this.donorList = donorList != null ? donorList : new ArrayList<>();
        this.onDeleteUserListener = onDeleteUserListener;
    }

    @NonNull
    @Override
    public UserListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderUsersBinding binding = ViewholderUsersBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListAdapter.ViewHolder holder, int position) {
        Donor donor = donorList.get(position);

        holder.binding.UserFullName.setText(donor.getName() != null ? donor.getName() : "N/A");
        holder.binding.UserEmail.setText(donor.getEmail() != null ? donor.getEmail() : "N/A");
        holder.binding.UserBloodType.setText(donor.getBloodType() != null ? donor.getBloodType() : "N/A");
        holder.binding.UserPhoneNumber.setText(donor.getPhoneNumber() != null ? donor.getPhoneNumber() : "N/A");
        holder.binding.userDOB.setText(donor.getDob() != null ? donor.getDob() : "N/A");

        // Retrieve the following site information from Firebase
        DatabaseReference donorFollowsRef = FirebaseDatabase.getInstance().getReference("DonorFollows");
        donorFollowsRef.child(donor.getUserId()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                // Get the hospitalId the user is following
                Map<String, Object> followedHospitals = (Map<String, Object>) task.getResult().getValue();
                if (followedHospitals != null && !followedHospitals.isEmpty()) {
                    // Assuming you're following only one hospital, you can get its ID and fetch the hospital name
                    String followedHospitalId = followedHospitals.keySet().iterator().next();
                    fetchHospitalName(followedHospitalId, holder);
                } else {
                    holder.binding.SiteFollowing.setText("No site followed");
                }
            } else {
                holder.binding.SiteFollowing.setText("No site followed");
            }
        });

        holder.binding.deleteUserBtn.setOnClickListener(v -> {
            Toast.makeText(context, "Deleting user...", Toast.LENGTH_SHORT).show();
            if (onDeleteUserListener != null && donor.getUserId() != null) {
                onDeleteUserListener.onDeleteUser(donor.getUserId());
            }
        });
    }

    private void fetchHospitalName(String hospitalId, ViewHolder holder) {
        DatabaseReference hospitalRef = FirebaseDatabase.getInstance().getReference("Hospital").child(hospitalId);
        hospitalRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                String hospitalName = task.getResult().child("siteName").getValue(String.class);
                if (hospitalName != null) {
                    holder.binding.SiteFollowing.setText(hospitalName);
                } else {
                    holder.binding.SiteFollowing.setText("Unknown hospital");
                }
            } else {
                holder.binding.SiteFollowing.setText("No site found");
            }
        });
    }

    @Override
    public int getItemCount() {
        return donorList.size();
    }

    // Update dataset
    public void updateDonorList(ArrayList<Donor> newDonorList) {
        this.donorList.clear();
        this.donorList.addAll(newDonorList);
        notifyDataSetChanged();
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ViewholderUsersBinding binding;

        public ViewHolder(ViewholderUsersBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    // Interface for delete action
    public interface OnDeleteUserListener {
        void onDeleteUser(String userId);
    }
}
