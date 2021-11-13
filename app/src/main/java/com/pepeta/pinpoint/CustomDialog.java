package com.pepeta.pinpoint;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.pepeta.pinpoint.databinding.CustomDialogBinding;

public class CustomDialog {
    CustomDialogBinding binding;
    private Context context;
    private Dialog dialog;

    public CustomDialog(Context context) {
        this.context = context;
        binding = CustomDialogBinding.inflate(LayoutInflater.from(context),null,false);
    }

    public void  showDialog(String message){
        dialog = new Dialog(context);
        dialog.setContentView(binding.getRoot());
        binding.tvDialogMessage.setText(message);
        dialog.setCancelable(false);
        dialog.show();
    }
    public void dismissDialog(){
        dialog.dismiss();
    }
}
