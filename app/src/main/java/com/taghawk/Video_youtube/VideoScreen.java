package com.taghawk.Video_youtube;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.taghawk.R;

public class VideoScreen extends AppCompatActivity {

    private ImageView ivback;
    private CardView cardheader;
    private WebView mWebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_screen);
        initView();
    }

    private void initView() {
        ivback = findViewById(R.id.ivback);
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        cardheader = findViewById(R.id.cardheader);
        mWebview = findViewById(R.id.webview);
        mWebview.setBackgroundColor(Color.TRANSPARENT);
      /*  theWebPage.getSettings().setJavaScriptEnabled(true);
        theWebPage.getSettings().setPluginState(WebSettings.PluginState.ON);
        String playVideo= "<html><body><iframe class=\"youtube-player\" type=\"text/html\" width=\"320\" height=\"385\" src=\"http://www.youtube.com/embed/m1pnwFSdOLU\" frameborder=\"0\"></body></html>";
        String htmlString = "<html> <body> <embed src=\"link of youtube video\"; type=application/x-shockwave-flash width="+DeviceWidth+" height="+DeviceHeight+"> </embed> </body> </html>";

        theWebPage.loadData(playVideo, "text/html", "utf-8");*/

        mWebview.setInitialScale(1);
        mWebview.getSettings().setPluginState(WebSettings.PluginState.ON);

        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });


        WebSettings webSettings = mWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setEnableSmoothTransition(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setSupportMultipleWindows(true);
        mWebview.loadUrl("http://www.youtube.com/embed/m1pnwFSdOLU");

    }
}
