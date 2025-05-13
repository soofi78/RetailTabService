package com.lfsolutions.retail.util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.lfsolutions.retail.R;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by devandro on 11/7/17.
 */

public class Alert {

    private static final String TAG = "Alert";
    private static final HashMap<Integer, AlertDialog> listOfDialogs = new HashMap<>();
    private final int LIMIT = 3;
    private final Activity activity;
    private View dialogView;
    private AlertDialog.Builder alertBuilder;
    private AlertDialog dialog;
    private LinearLayout buttonsContainer;
    private TextView txt_description;
    private TextView txt_title;
    private ImageView logo;
    private LinearLayout logoWrapper;
    private String description;
    private int buttonCount = 0;

    public Alert(Activity activity) {
        this.activity = activity;
    }

    public static boolean dialogsVisible() {
        return listOfDialogs.size() > 0;
    }

    public static Alert make(Activity activity) {
        Alert alert = new Alert(activity);
        return alert.init();
    }

    public static boolean areVisible() {
        return listOfDialogs.size() > 0;
    }

    public static void clearAllDialogs() {
        try {
            Iterator it = listOfDialogs.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                try {
                    ((AlertDialog) pair.getValue()).dismiss();
                } catch (Exception e) {

                }
                it.remove();
            }
            listOfDialogs.clear();
        } catch (Exception e) {
            Logger.INSTANCE.log(TAG, e);
        }
    }

    public AlertDialog.Builder getAlertBuilder() {
        return alertBuilder;
    }

    public Activity getActivity() {
        return activity;
    }

    public AlertDialog getDialog() {
        return dialog;
    }

    private Alert init() {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dialogView = inflater.inflate(R.layout.dialog, null);
        dialogView.setBackgroundResource(R.drawable.alert);
        txt_title = dialogView.findViewById(R.id.title);
        txt_description = dialogView.findViewById(R.id.description);
        buttonsContainer = dialogView.findViewById(R.id.controls);
        logo = dialogView.findViewById(R.id.icon);
        logoWrapper = dialogView.findViewById(R.id.icon_wrapper);
        return this;
    }

    public Alert setTitle(String title) {
        this.txt_title.setText(title);
        return this;
    }

    public AlertDialog show(boolean addToBackStack) {
        try {
            if (buttonCount > LIMIT) {
                throw new RuntimeException("Button in a dialog should not be more than 3");
            }
            if (activity != null) {
                alertBuilder = new AlertDialog.Builder(activity);
                alertBuilder.setView(dialogView);
                alertBuilder.setCancelable(false);
                dialog = alertBuilder.create();
                dialog.getWindow().getAttributes().windowAnimations = R.style.CustomAnimations;
                dialog.setOnDismissListener(dialog -> {
                    if (dialog != null)
                        listOfDialogs.remove(dialog.hashCode());
                });
                dialog.show();
                if (addToBackStack) listOfDialogs.put(dialog.hashCode(), dialog);
                return dialog;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public Alert setDescription(String description) {
        this.txt_description.setText(description);
        this.description = description;
        return this;
    }

    public Alert addButton(String buttonText, final OnAction action) {
        if (buttonCount >= LIMIT) {
            return this;
        }
        View view = activity.getLayoutInflater().inflate(R.layout.button, null, false);
        Button button = view.findViewById(R.id.buttonId);
        button.setText(buttonText);
        button.setTag(action);
        button.setAllCaps(false);
        button.setOnFocusChangeListener((v, hasFocus) -> {
            button.setTextColor(activity.getColor(hasFocus ? R.color.black : R.color.white));
            button.setBackgroundResource(hasFocus ? R.drawable.button_focused : R.drawable.button_default);
        });
        button.setOnClickListener(v -> {
            if (v.getTag() instanceof OnAction) {
                ((OnAction) v.getTag()).action(dialog);
            }
        });
        buttonsContainer.addView(view);
        buttonCount++;
        return this;
    }

    public Alert setButtonOrientation(int buttonOrientation) {
        buttonsContainer.setOrientation(buttonOrientation);
        return this;
    }

    public Alert autoCloseAfter(int millis) {
        dialogView.postDelayed(() -> {
            if (dialog != null && dialog.isShowing()) dialog.dismiss();
        }, millis);
        return this;
    }

    public Alert autoCloseAfter(int millis, OnAction onClose) {
        dialogView.postDelayed(() -> {
            if (dialog != null && dialog.isShowing()) dialog.dismiss();
            if (onClose != null) {
                onClose.action(null);
            }
        }, millis);
        return this;
    }

    public Alert setLogo(int resId) {
        this.logo.setImageResource(resId);
        this.logoWrapper.setVisibility(View.VISIBLE);
        return this;
    }

    public interface OnAction {
        void action(AlertDialog alertDialog);
    }
}
