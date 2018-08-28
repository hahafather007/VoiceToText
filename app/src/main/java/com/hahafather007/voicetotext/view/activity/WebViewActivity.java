package com.hahafather007.voicetotext.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hahafather007.voicetotext.R;
import com.hahafather007.voicetotext.databinding.ActivityWebviewBinding;

import static android.content.Intent.EXTRA_TITLE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class WebViewActivity extends AppCompatActivity {
    private ActivityWebviewBinding binding;

    public static Intent intentOfUrl(Context context, String url, String title) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("extra_url", url);
        intent.putExtra(EXTRA_TITLE, title);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_webview);
        setTitle(getIntent().getStringExtra(EXTRA_TITLE));

        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                binding.progressBar.setVisibility(GONE);
            }
        });

        loadWebUrl();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //点击刷新按钮重新刷新该界面
            case R.id.nav_refresh:
                loadWebUrl();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadWebUrl() {
        //防止重复加载
        if (binding.progressBar.getVisibility() == VISIBLE) return;

        binding.progressBar.setVisibility(VISIBLE);

        binding.webView.loadUrl(getIntent().getStringExtra("extra_url"));
    }
}
