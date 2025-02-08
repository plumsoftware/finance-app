package ru.plumsoftware.costcontrol;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Window;

public class ProgressDialog {
    private Dialog dialog;

    @SuppressLint("InflateParams")
    public void showDialog(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(LayoutInflater.from(context).inflate(R.layout.progress_dialog_layout, null, false));
        dialog.show();
    }

    public void dismissDialog() {
        try {
            if (dialog != null)
                dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
