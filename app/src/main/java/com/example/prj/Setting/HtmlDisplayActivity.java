package com.example.prj.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prj.R;

public class HtmlDisplayActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html_display);

        WebView webView = findViewById(R.id.web_view);
        webView.setWebViewClient(new WebViewClient());

        // Get the data from the intent
        String htmlContent = getIntent().getStringExtra("htmlContent");

        // Load the HTML content into the WebView
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(HtmlDisplayActivity.this, SettingPage.class);
        startActivity(intent);
        finish();
    }
}