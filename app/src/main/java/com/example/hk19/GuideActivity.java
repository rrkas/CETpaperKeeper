package com.example.hk19;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class GuideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        TextView guide_version = findViewById(R.id.guide_version);
        guide_version.setText(LoginActivity.VERSION);

        TextView guide_version_two = findViewById(R.id.guide_version_two);
        guide_version_two.setText(LoginActivity.VERSION);
    }
}
