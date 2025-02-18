package com.example.estanciasapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

class LoadingDialog {
Activity activity;
AlertDialog alertDialog;

    LoadingDialog(Activity myActivity){
            activity=myActivity;
    }
    void startLoadingDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        LayoutInflater inflater=activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_progress,null));
        builder.setCancelable(false);
        alertDialog=builder.create();
        alertDialog.show();
    }
    void dismissDialog(){
        alertDialog.dismiss();
    }
}