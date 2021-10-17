package com.pepeta.pinpoint;

import androidx.recyclerview.widget.DiffUtil;

import com.pepeta.pinpoint.Model.PlaceDetails.DetailsModel;

import java.util.List;

public class DiffUtilCallback extends DiffUtil.Callback {
    private List<DetailsModel> oldList;
    private List<DetailsModel> newList;

    public DiffUtilCallback(List<DetailsModel> oldList, List<DetailsModel> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldItemPosition == newItemPosition;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition) == newList.get(newItemPosition);
    }
}
