package com.example.hk19;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomAdapter extends ArrayAdapter<PersonDetails> {
    public CustomAdapter(@NonNull Context context, PersonDetails[] personDetails) {
        super(context,R.layout.row_layout,personDetails);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater myInflater = LayoutInflater.from(getContext());
        View CustomView = myInflater.inflate(R.layout.row_layout, parent,false);

        //get a reference
        PersonDetails singleItem = getItem(position);
        TextView myText = CustomView.findViewById(R.id.myName);
        TextView myNo = CustomView.findViewById(R.id.myNo);

        if(singleItem != null) {
            myText.setText(singleItem.getPersonName());
            myNo.setText(singleItem.getPersonNumber());
        }
        return CustomView;
    }
}
