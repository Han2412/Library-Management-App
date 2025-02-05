package com.example.project.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.project.R;

public class LoadingDialog {

    private static LoadingDialog instance;
    private Dialog dialog;
    private Context context;

    private LoadingDialog(Context context) {
        this.context = context;
        createDialog();
    }

    public static synchronized LoadingDialog getInstance(Context context) {
        if (instance == null) {
            instance = new LoadingDialog(context);
        }
        return instance;
    }



    private void createDialog() {
        dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.loading_dialog);

        // Adjust dialog layout parameters if needed
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
    }

    public void show() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public void hide() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
