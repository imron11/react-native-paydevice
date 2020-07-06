package com.reactlibrary.presentation;

import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.net.Uri;
import android.view.Display;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import com.reactlibrary.R;

public class LogoPresentation extends Presentation {
    private static final String TAG = "TextPresentation";

    private TextView mAmountText;
    private TextView mChangeText;
    private Context mContext;

    public LogoPresentation(Context context, Display display, int theme) {
        super(context, display, theme);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logoview);

        mAmountText = (TextView)findViewById(R.id.amount);
        mChangeText = (TextView)findViewById(R.id.change);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void onDisplayRemoved() {
        Log.d(TAG, "Secondary display has removed!");
        dismiss();
    }

    @Override
    public void onDisplayChanged() {
        Log.d(TAG, "Secondary display has changed!");
    }

    public void setData(String amount, String change) {
        String strAmount = "Amount : " + amount;
        String strChange = "Change : " + change;
        mAmountText.setText(strAmount);
        mChangeText.setText(strChange);
        Log.d(TAG, strAmount+" "+strChange);
    }
}
