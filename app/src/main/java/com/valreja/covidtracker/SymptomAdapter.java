package com.valreja.covidtracker;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SymptomAdapter extends RecyclerView.Adapter<SymptomAdapter.SymptomViewHolder> {

    private final ArrayList<Symptom> symptomArrayList;
    OnRatingChangeListener ratingChangeListener;
    public SymptomAdapter(ArrayList<Symptom> symptomArrayList, OnRatingChangeListener ratingChangeListener) {
        this.symptomArrayList = symptomArrayList;
        this.ratingChangeListener = ratingChangeListener;
    }

    @NonNull
    @Override
    public SymptomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View currentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.symptom_list_item ,parent, false);
        return new SymptomViewHolder(currentView);
    }

    @Override
    public void onBindViewHolder(@NonNull SymptomViewHolder holder, int position) {
        Symptom currentSymptom = this.symptomArrayList.get(position);
        holder.SymptomNameTextView.setText(currentSymptom.getName());
        Log.d("AAAAAAAAAAAAAAA"," "+ position);
        holder.SymptomRatingBar.setRating(currentSymptom.getRating());
        holder.SymptomRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if (b) {
                    ratingChangeListener.onItemClick(position, v);
                }
            }
        });
        // holder.SymptomRatingBar.setOnRatingBarChangeListener((ratingBar, v, b) -> );
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        Log.d("AAAAAA",""+this.symptomArrayList.size());
        return this.symptomArrayList.size();
    }

    public static class SymptomViewHolder extends RecyclerView.ViewHolder {
        View view;
        public TextView SymptomNameTextView;
        public RatingBar SymptomRatingBar;

        public SymptomViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            SymptomNameTextView = (TextView) itemView.findViewById(R.id.symptom_name_text_view);
            SymptomRatingBar = (RatingBar) itemView.findViewById(R.id.symptom_rating_bar);
        }
    }
}
