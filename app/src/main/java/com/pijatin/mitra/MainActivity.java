
package com.pijatin.mitra;
import android.os.Bundle; import android.webkit.WebView; import android.webkit.WebViewClient; import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle s){
        super.onCreate(s);
        WebView w=new WebView(this); w.getSettings().setJavaScriptEnabled(true); w.getSettings().setDomStorageEnabled(true);
        w.setWebViewClient(new WebViewClient()); w.loadUrl("file:///android_asset/index.html"); setContentView(w);
    }
}
