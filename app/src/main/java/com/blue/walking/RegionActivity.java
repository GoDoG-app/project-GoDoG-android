package com.blue.walking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class RegionActivity extends AppCompatActivity {

    WebView webView;

    class MyJavaScriptInterface {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processAddressData(String sido, String sigungu, String hname, String fullAddr) {
            Intent intent = new Intent(RegionActivity.this, RegisterActivity.class);
            intent.putExtra("sido", sido);
            intent.putExtra("sigungu", sigungu);
            intent.putExtra("hname", hname);
            intent.putExtra("fullAddr", fullAddr);

            setResult(RESULT_OK,intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region);
        webView = findViewById(R.id.webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyJavaScriptInterface(), "Android");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i("test", "Web page finished loading. Calling JavaScript function.");
                webView.loadUrl("javascript:sample2_execDaumPostcode();");
            }
        });

        webView.loadUrl("https://project4-walking-app.s3.amazonaws.com/kakao_search.html");
    }
}