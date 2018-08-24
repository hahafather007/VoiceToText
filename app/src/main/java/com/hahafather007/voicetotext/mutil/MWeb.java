package com.hahafather007.voicetotext.mutil;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.webkit.*;

import java.net.*;

import android.widget.*;


public class MWeb extends Activity {
    WebView webView;
    String urlurl;

    boolean b = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        webView = new WebView(this);
        setContentView(webView);
        WebSettings webSettings = webView.getSettings();

// 支持javascript
        webSettings.setJavaScriptEnabled(true);

// 支持使用localStorage(H5页面的支持)
        webSettings.setDomStorageEnabled(true);

// 支持数据库
        webSettings.setDatabaseEnabled(true);

// 支持缓存
        webSettings.setAppCacheEnabled(true);
        String appCaceDir = this.getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        webSettings.setAppCachePath(appCaceDir);

// 设置可以支持缩放
        webSettings.setUseWideViewPort(true);

// 扩大比例的缩放
        webSettings.setSupportZoom(true);

        webSettings.setBuiltInZoomControls(true);

// 隐藏缩放按钮
        webSettings.setDisplayZoomControls(false);

// 自适应屏幕
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setLoadWithOverviewMode(true);

// 隐藏滚动条
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);

// 进度显示及隐藏


        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String s, String s1, String s2, String s3, long l) {
                downloadByBrowser(s);
            }
        });

// 处理网页内的连接（自身打开）
        webView.setWebViewClient(new WebViewClient() {


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {


            }

            @Override
            public void onPageFinished(WebView view, String url) {

            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);

            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                // 如下方案可在非微信内部WebView的H5页面中调出微信支付


                if (url.startsWith("weixin://wap/pay?") | url.startsWith("mqqapi") | url.startsWith("alipay")) {

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));

                    WebBackForwardList backForwardList = webView.copyBackForwardList();
                    int currentIndex = backForwardList.getCurrentIndex();
                    int i = 1;
                    while (true) {
                        WebHistoryItem historyItem = backForwardList.getItemAtIndex(currentIndex - i);
                        if (historyItem != null) {
                            String backPageUrl = historyItem.getUrl();
                            //url拿到可以进行操作

                            if (!isyou(backPageUrl, urlurl)) {
                                webView.goBack();
                            } else {
                                webView.goBack();
                                break;
                            }

                        }
                        i++;
                    }
                    startActivity(intent);


                    return true;
                } else if (parseScheme(url)) {
                    try {
                        Intent intent;
                        intent = Intent.parseUri(url,
                                Intent.URI_INTENT_SCHEME);
                        intent.addCategory("android.intent.category.BROWSABLE");
                        intent.setComponent(null);
                        // intent.setSelector(null);
                        //   webView.goBack();
                        WebBackForwardList backForwardList = webView.copyBackForwardList();
                        int currentIndex = backForwardList.getCurrentIndex();
                        int i = 1;
                        while (true) {
                            WebHistoryItem historyItem = backForwardList.getItemAtIndex(currentIndex - i);
                            if (historyItem != null) {
                                String backPageUrl = historyItem.getUrl();
                                //url拿到可以进行操作
                                if (!isyou(backPageUrl, urlurl)) {
                                    webView.goBack();
                                } else {
                                    webView.goBack();
                                    break;
                                }
                            }
                            i++;
                        }
                        startActivity(intent);

                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                return false;


            }


        });

// 使用返回键的方式防止网页重定向
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                        webView.goBack();
                        return true;
                    }
                }
                return false;
            }
        });


//        Toast.makeText(this, getIntent().getStringExtra("url"), 3000).show();
        if (getIntent().getStringExtra("url").contains("http://") | getIntent().getStringExtra("url").contains("https://")) {
            webView.loadUrl(getIntent().getStringExtra("url"));
            URL url = null;
            try {
                url = new URL(getIntent().getStringExtra("url"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            urlurl = url.getHost();// 获取主机名
        } else {
            webView.loadUrl("http://" + getIntent().getStringExtra("url"));

            URL url = null;
            try {
                url = new URL("http://" + getIntent().getStringExtra("url"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            urlurl = url.getHost();// 获取主机名

        }


    }


    public boolean parseScheme(String url) {

        if (url.contains("platformapi/startapp")) {
            return true;
        } else if ((Build.VERSION.SDK_INT > 23)
                && (url.contains("platformapi") && url.contains("startapp"))) {
            return true;
        } else {
            return false;
        }
    }

    private void downloadByBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }


    public boolean isyou(String s1, String s2) {


        String str = s1;
        if (str.indexOf(s2) != -1) {

            return true;

        } else {

            return false;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        webView.reload();
    }

}

