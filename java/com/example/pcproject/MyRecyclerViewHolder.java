package com.example.pcproject;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyRecyclerViewHolder extends RecyclerView.ViewHolder {
    TextView txt_nume, txt_puls;
    public MyRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);

        txt_puls = itemView.findViewById(R.id.txt_content);
        txt_nume = itemView.findViewById(R.id.txt_title);
    }

}
