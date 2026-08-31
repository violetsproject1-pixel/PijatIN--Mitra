package com.pijatin.mitra;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle s){
        super.onCreate(s);
        try {
            WebView w=new WebView(this);
            w.getSettings().setJavaScriptEnabled(true);
            w.getSettings().setDomStorageEnabled(true);
            w.setWebViewClient(new WebViewClient());
            w.loadUrl("file:///android_asset/index.html");
            setContentView(w);
        } catch (Exception e) {
            // Kalau index.html ga ada, tampil text biar ga crash
            android.widget.TextView tv = new android.widget.TextView(this);
            tv.setText("PijatIN MITRA\n\nLoading...\nError: " + e.getMessage() + "\n\nCek app/src/main/assets/index.html ada ga?");
            tv.setPadding(50,50,50,50);
            setContentView(tv);
        }
    }
}
