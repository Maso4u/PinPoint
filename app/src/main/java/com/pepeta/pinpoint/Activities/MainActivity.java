package com.pepeta.pinpoint.Activities;

import static android.content.ContentValues.TAG;
import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavArgs;
import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.pepeta.pinpoint.Fragments.FavouritesFragment;
import com.pepeta.pinpoint.Fragments.FavouritesFragmentDirections;
import com.pepeta.pinpoint.Fragments.MapsFragment;
import com.pepeta.pinpoint.R;
import com.pepeta.pinpoint.User;
import com.pepeta.pinpoint.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity{

    private ActivityMainBinding binding;
    public User user;
    NavArgument navArgument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().hasExtra("user")){
            user = getIntent().getParcelableExtra("user");
            Bundle bundle = new Bundle();
            bundle.putParcelable("user", user);
            getSupportFragmentManager().beginTransaction().replace(binding.hostFragment.getId(),MapsFragment.newInstance(user.getId())).commit();

            binding.bottomNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()){
                        case R.id.favouritesFragment:
//                            getFragmentManager().beginTransaction().replace(binding.hostFragment.getId(),FavouritesFragment.newInstance(user.getId()));
                            selectedFragment = FavouritesFragment.newInstance(user.getId());
                            break;
                        case R.id.locationsFragment:
                            selectedFragment = MapsFragment.newInstance(user.getId());
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(binding.hostFragment.getId(),selectedFragment).commit();
                    return true;
                }
            });
        }

    }



}