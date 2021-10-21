package com.pepeta.pinpoint.Activities;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import com.pepeta.pinpoint.Fragments.FavouritesFragment;
import com.pepeta.pinpoint.Fragments.MapsFragment;
import com.pepeta.pinpoint.Fragments.SettingsFragment;
import com.pepeta.pinpoint.R;
import com.pepeta.pinpoint.User;
import com.pepeta.pinpoint.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity{

    private ActivityMainBinding binding;
    public User user;
//    NavArgument navArgument;
    Fragment selectedFragment = null;

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

            binding.bottomNavView.setOnItemSelectedListener(item ->  {

                if (item.getItemId()==R.id.favouritesFragment){
                    selectedFragment = FavouritesFragment.newInstance(user.getId());
                }else if (item.getItemId()==R.id.locationsFragment){
                    selectedFragment = MapsFragment.newInstance(user.getId());
                }

                if (binding.btnSettings.isChecked()) binding.btnSettings.setChecked(false);
                getSupportFragmentManager().beginTransaction().replace(binding.hostFragment.getId(),selectedFragment).commit();
                return true;
            });

            binding.btnSettings.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    getSupportFragmentManager().beginTransaction().
                            replace(binding.hostFragment.getId(), SettingsFragment.newInstance(user.getId())).
                            addToBackStack(null).commit();
                }else{
                    if (selectedFragment instanceof FavouritesFragment){
                        selectedFragment = FavouritesFragment.newInstance(user.getId());
                        getSupportFragmentManager().beginTransaction()
                                .replace(binding.hostFragment.getId(),selectedFragment).commit();
                    }else{
                        getSupportFragmentManager().popBackStack();
                    }
                }
            });
        }

    }
}