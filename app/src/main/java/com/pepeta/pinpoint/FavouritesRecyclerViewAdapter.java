package com.pepeta.pinpoint;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.places.Place;
import com.pepeta.pinpoint.Model.PlaceDetails.DetailsModel;
import com.pepeta.pinpoint.Model.PlaceDetails.PeriodModel;
import com.pepeta.pinpoint.databinding.FavouritePlaceRowBinding;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FavouritesRecyclerViewAdapter extends RecyclerView.Adapter<FavouritesRecyclerViewAdapter.ViewHolder> {

    List<DetailsModel> favouritePlacesList = new ArrayList<>();

    /*public FavouritesRecyclerViewAdapter(Context context, String[] location) {

    }*/

    public void updateFavouritePlacesList(List<DetailsModel> favouritePlacesList){
        DiffUtilCallback diffUtilCallback = new DiffUtilCallback(this.favouritePlacesList,favouritePlacesList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtilCallback);
        this.favouritePlacesList.clear();
        this.favouritePlacesList.addAll(favouritePlacesList);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FavouritePlaceRowBinding favouritePlaceRowBinding = FavouritePlaceRowBinding
                .inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(favouritePlaceRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setBinding(favouritePlacesList.get(position));
    }

    @Override
    public int getItemCount() {return favouritePlacesList.size();}

    public class ViewHolder  extends RecyclerView.ViewHolder{
        FavouritePlaceRowBinding binding;

        public ViewHolder(@NonNull FavouritePlaceRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void setBinding(DetailsModel details){

            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK)-1;
            String closeStatus, closeTime = null, periodMsg;

            binding.tvPlaceName.setText(details.getName());
            binding.tvPlaceAddress.setText(details.getFormattedAddress());
            binding.tvContactNumber.setText(String.format(binding.getRoot()
                    .getContext()
                    .getString(R.string.contact_number_text), details.getFormattedPhoneNumber()));
            binding.tvRating.setText(String.format(binding.getRoot()
                    .getContext()
                    .getString(R.string.rating_text),details.getRating()));
            if (details.getOpeningHours()!=null){
                if (details.getOpeningHours().getOpenNow()){
                    closeStatus ="Open";
                    binding.tvHours.setTextColor(binding.getRoot().getContext().getColor(R.color.green));
                    periodMsg ="Closes";

                    for (PeriodModel period:details.getOpeningHours().getPeriods()) {
                        if (period.getClose().getDay()==dayOfWeek) {
                            closeTime=period.getClose().getTime();
                            break;
                        }
                    }
                }else {
                    closeStatus = "Closed";
                    periodMsg ="Opens";
                    binding.tvHours.setTextColor(binding.getRoot().getContext().getColor(R.color.error_red));
                    for (PeriodModel period:details.getOpeningHours().getPeriods()) {
                        if (period.getOpen().getDay()==dayOfWeek) {
                            closeTime=period.getOpen().getTime();
                            break;
                        }
                    }
                }

                binding.tvHours.setText(String.format(binding.getRoot()
                        .getContext()
                        .getString(R.string.hours),closeStatus,periodMsg,formattedTime(closeTime)));
            }else binding.tvHours.setText(R.string.hours_unavailable);
        }

        private String formattedTime(String unformattedTime){
            String hours = unformattedTime.substring(0,2);
            String minutes = unformattedTime.substring(2,4);
            return hours+":"+minutes;
        }

    }
}
