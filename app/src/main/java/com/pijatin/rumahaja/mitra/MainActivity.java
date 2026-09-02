package com.pijatin.rumahaja.mitra;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
public class MainActivity extends Activity{
@Override protected void onCreate(Bundle s){
super.onCreate(s);
WebView w=new WebView(this);
WebSettings ws=w.getSettings();
ws.setJavaScriptEnabled(true);
ws.setDomStorageEnabled(true);
ws.setAllowFileAccess(true);
ws.setAllowFileAccessFromFileURLs(true);
ws.setAllowUniversalAccessFromFileURLs(true);
ws.setAllowContentAccess(true);
w.setWebViewClient(new WebViewClient());
w.loadUrl("file:///android_asset/index.html");
setContentView(w);
}
}
