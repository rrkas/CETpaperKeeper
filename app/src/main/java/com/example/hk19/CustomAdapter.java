package com.example.hk19;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private List<PersonDetails> mPersons;
    private Context context;
    private OnRecordClickListener onRecordClickListener;

    public CustomAdapter(List<PersonDetails> mPersons, Context context, OnRecordClickListener onRecordClickListener) {
        this.mPersons = mPersons;
        this.context = context;
        this.onRecordClickListener=onRecordClickListener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout,parent,false);
        return new ViewHolder(view,onRecordClickListener);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PersonDetails personDetails=mPersons.get(position);
        holder.Number.setText(personDetails.getPersonNumber());
        holder.Name.setText(personDetails.getPersonName());
    }
    @Override
    public int getItemCount() {
        return mPersons.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView Name,Number;

        OnRecordClickListener onRecordClickListener;

        public ViewHolder(@NonNull View itemView, OnRecordClickListener onRecordClickListener) {
            super(itemView);
            Name=itemView.findViewById(R.id.myName);
            Number=itemView.findViewById(R.id.myNo);

            this.onRecordClickListener=onRecordClickListener;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            onRecordClickListener.onRecordClick(getAdapterPosition());
        }
    }

    public interface OnRecordClickListener{
        void onRecordClick(int position);
    }
}
