package com.pepeta.pinpoint;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.pepeta.pinpoint.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_PinPoint);
        binding =ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}