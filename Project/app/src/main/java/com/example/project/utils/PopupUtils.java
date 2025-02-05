package com.example.project.utils;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

public class PopupUtils {

    public interface OnClickListener {
        void OnPositiveClicked();
        void OnNegativeClicked();
    }

    public static void showPopup(Context context, String title, String message, Constants.TYPE_ALERT typeAlert, OnClickListener onPositive, OnClickListener onNegative) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message);

        switch (typeAlert) {
            case OK:
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (onPositive != null) onPositive.OnPositiveClicked();
                    }
                });
                break;
            case OK_CANCEL:
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (onPositive != null) onPositive.OnPositiveClicked();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(onNegative != null) onNegative.OnNegativeClicked();
                            }
                        });
                break;
            case TRYAGAIN_CANCEL:
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (onPositive != null) onPositive.OnPositiveClicked();
                            }
                        })
                        .setNegativeButton("Try Again", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(onNegative != null) onNegative.OnNegativeClicked();
                            }
                        });
                break;
            // Thêm các trường hợp khác nếu cần
        }

        builder.show();
    }
}
