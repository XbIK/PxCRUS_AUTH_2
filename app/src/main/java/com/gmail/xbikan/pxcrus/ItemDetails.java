package com.gmail.xbikan.pxcrus;


import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ShareActionProvider;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;

import android.webkit.WebChromeClient;

import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.gmail.xbikan.pxcrus.retrofit.ItemWebRequest;
import com.gmail.xbikan.pxcrus.retrofit.ItemWebRequestSingleton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ItemDetails extends AppCompatActivity {
    private static final String TAG_LOD = "Log";
    WebView mWebView;
    CardView cardView;
    TextView art, name, packageMin, warehouseText;
    private ShareActionProvider mShareActionProvider;
    String articul_name;
    String name_text;
    String articul_web;
    ImageView statusImage, updateImage;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        art = (TextView) findViewById(R.id.art_detail);
        name = (TextView) findViewById(R.id.name_detail);
        packageMin = (TextView) findViewById(R.id.package_detail);
        cardView = (CardView) findViewById(R.id.cardView_details);
        warehouseText = (TextView) findViewById(R.id.warehouse_textview);
        statusImage = (ImageView) findViewById(R.id.status_image);
        updateImage = (ImageView) findViewById(R.id.update_image);


        articul_name = getIntent().getExtras().getString("key_art");
        name_text = getIntent().getExtras().getString("key_name");
        articul_web = getIntent().getExtras().getString("key_art_web");
        Integer packageInt = getIntent().getExtras().getInt("key_package");
        String package_min = Integer.toString(packageInt);

        art.setText(articul_name);
        name.setText(name_text);
        packageMin.setText("упаковка, шт: " + package_min);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean webEnabled = sp.getBoolean("connection_web", true);

        webView(sp);

        ItemWebRequest webRequest = new ItemWebRequest();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser==null){
            warehouseText.setVisibility(View.GONE);
            statusImage.setVisibility(View.GONE);
            updateImage.setVisibility(View.GONE);

        }

        if (currentUser != null && webEnabled) {
            webRequest.itemDetailWeb(articul_web, warehouseText, statusImage, updateImage);
            updateImage.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.anim_rotate_twice));
        }


        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null && webEnabled) {
                    updateImage.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.anim_rotate_twice));
                    warehouseText.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.anim_fade));
                    webRequest.itemDetailWeb(articul_web, warehouseText, statusImage, updateImage);
                }
            }
        });
    }

    private void webView(SharedPreferences sp) {
        progressBar = (ProgressBar) findViewById(R.id.web_view_progress);
        progressBar.setMax(100);
        progressBar.setProgress(1);
        boolean web_connect = sp.getBoolean("connection_web", true);

        mWebView = (WebView) findViewById((R.id.webViewDetails));
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setBuiltInZoomControls(true);

        if (web_connect) {
            mWebView.loadUrl(ConstantsPxC.WEB_STRING_PXC + articul_web);
        } else {
            progressBar.setVisibility(View.GONE);
        }

        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
            }
        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_menu, menu);
        MenuItem shareItem = menu.findItem(R.id.share);
        return true;
    }

    protected void onResume() {
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean webEnabled = sp.getBoolean("connection_web", true);

    }


    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            case R.id.share:
                String statusToAppend = warehouseText.getText().toString();
                ;
                if (!statusToAppend.contains("наличие")) {
                    statusToAppend = "";
                }
                StringBuilder shareBody = new StringBuilder(articul_name);
                shareBody.append("\n").append(name_text);
                shareBody.append("\n").append(ConstantsPxC.WEB_STRING_PXC);
                shareBody.append(articul_web);
                shareBody.append("\n").append(statusToAppend);
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody.toString());
                startActivity(Intent.createChooser(sharingIntent, "Поделиться"));

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            warehouseText.setVisibility(View.VISIBLE);
            statusImage.setVisibility(View.VISIBLE);
            updateImage.setVisibility(View.VISIBLE);
        } else {
            warehouseText.setVisibility(View.GONE);
            statusImage.setVisibility(View.GONE);
            updateImage.setVisibility(View.GONE);
        }
    }

}
