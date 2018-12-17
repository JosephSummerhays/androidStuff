package com.example.joseph.famserver;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.joseph.famserver.Models.ItemModel;

import java.util.ArrayList;

public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.ItemHolder>{
    ArrayList<ItemModel> items;
    public ItemRecyclerAdapter(ArrayList<ItemModel> relatives) {
        this.items = relatives;
    }
    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item, viewGroup,false);
        return new ItemHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int i) {
        holder.topLine.setText(items.get(i).getTopline());
        holder.bottomLine.setText(items.get(i).getBottomline());
        holder.icon.setImageResource(items.get(i).getIconID());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView topLine;
        public TextView bottomLine;
        public ImageView icon;
        public ItemHolder(View v) {
            super(v);
            topLine = itemView.findViewById(R.id.topline);
            bottomLine = itemView.findViewById(R.id.bottomline);
            icon = itemView.findViewById(R.id.icon);
        }

    }

}
