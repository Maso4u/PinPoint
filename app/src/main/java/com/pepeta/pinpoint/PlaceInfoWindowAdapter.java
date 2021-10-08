package com.pepeta.pinpoint;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.pepeta.pinpoint.databinding.PlaceInfoWindowBinding;

public class PlaceInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View mWindow;
    private Context mContext;
    PlaceInfoWindowBinding binding;
    public PlaceInfoWindowAdapter(Context mContext) {
        binding = PlaceInfoWindowBinding.inflate(LayoutInflater.from(mContext));
        this.mContext = mContext;
        mWindow = binding.getRoot();
//        mWindow = LayoutInflater.from(mContext).inflate(R.layout.place_info_window_,null);

    }

    private void rendorWindowText(Marker marker){
        binding.tvPlaceName.setText(marker.getTitle());
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        return null;
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        return null;
    }
}
