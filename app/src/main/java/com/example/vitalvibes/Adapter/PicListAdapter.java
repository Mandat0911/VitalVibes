package com.example.vitalvibes.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vitalvibes.databinding.ViewholderPiclistBinding;

import java.util.List;

public class PicListAdapter extends RecyclerView.Adapter<PicListAdapter.Viewholder> {
    private List<String> pics;
    private ImageView picMain;
    private Context context;

    public PicListAdapter(List<String> pics, ImageView picMain) {
        this.pics = pics;
        this.picMain = picMain;
    }

    @NonNull
    @Override
    public PicListAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderPiclistBinding binding = ViewholderPiclistBinding.inflate(LayoutInflater.from(context), parent, false);

        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PicListAdapter.Viewholder holder, int position) {
        Glide.with(context)
                .load(pics.get(position))
                .into(holder.binding.picList);

        holder.binding.getRoot().setOnClickListener(v -> {
            Glide.with(context)
                    .load(pics.get(position))
                    .into(picMain);
        });
    }

    @Override
    public int getItemCount() {
        return pics.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ViewholderPiclistBinding binding;
        public Viewholder(ViewholderPiclistBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
