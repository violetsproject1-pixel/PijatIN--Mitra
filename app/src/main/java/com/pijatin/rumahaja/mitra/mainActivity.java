package com.pijatin.rumahaja.mitra;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("Pijat IN Mitra - Ready");
        tv.setTextSize(24);
        tv.setPadding(50, 200, 50, 50);
        setContentView(tv);
    }
}
