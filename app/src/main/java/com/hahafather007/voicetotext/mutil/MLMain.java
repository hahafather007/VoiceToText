package com.hahafather007.voicetotext.mutil;


import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.view.*;

import java.io.*;
import java.net.*;

import android.widget.*;

public class MLMain extends Activity {
    String urls = "";
    String valur;
    String apackager;
    String aclass;
    String bclass;
    String cclass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: Implement this method
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View i = new View(this);
        i.setBackground(new BitmapDrawable(setB()));
        setContentView(i);
        mCreate();

        new Thread() {

            public void run() {
                valur = getPageSource();
                h.sendEmptyMessage(1);

            }

        }.start();


    }

    public void mCreate() {


    }


    public final void setL(String url, String p, String n, String bn, String cn) {
        this.urls = url;
        this.apackager = p;
        this.aclass = n;
        this.bclass = bn;
        this.cclass = cn;
    }


    public String getPageSource() {
        StringBuffer sb = new StringBuffer();
        try {
            //构建一URL对象
            URL url = new URL(urls);
            //使用openStream得到一输入流并由此构造一个BufferedReader对象
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            String line;
            //读取www资源
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            in.close();
        } catch (Exception ex) {
            System.err.println(ex);
            return null;
        }
        return sb.toString();
    }

    Handler h = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (valur == null) {
                        Toast.makeText(getApplication(), "网络异常", 3000).show();
                        h.sendEmptyMessageDelayed(2, 1500);
                        break;
                    }
                    String is = mGetValue("isshowwap");
                    if (is.equals("0")) {
                        h.sendEmptyMessageDelayed(2, 1500);
                    } else if (is.equals("1")) {
                        h.sendEmptyMessageDelayed(3, 1500);
                    } else if (is.equals("2")) {
                        h.sendEmptyMessageDelayed(4, 1500);
                    }

                    //Toast.makeText(getApplication(),is,3000).show();
                    break;
                case 2:
                    //主界面
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    ComponentName cn = new ComponentName(apackager, aclass);
                    intent.setComponent(cn);
                    startActivity(intent);
                    finish();
                    break;
                case 3:
                    //web界面
                    Intent intent1 = new Intent(Intent.ACTION_MAIN);
                    intent1.addCategory(Intent.CATEGORY_LAUNCHER);
                    ComponentName cn1 = new ComponentName(apackager, bclass);
                    intent1.setComponent(cn1);
                    intent1.putExtra("url", mGetValue("wapurl"));
                    startActivity(intent1);
                    finish();
                    break;
                //强制更新界面
                case 4:
                    Intent intent2 = new Intent(Intent.ACTION_MAIN);
                    intent2.addCategory(Intent.CATEGORY_LAUNCHER);
                    ComponentName cn2 = new ComponentName(apackager, cclass);
                    intent2.setComponent(cn2);
                    intent2.putExtra("url", mGetValue("desc"));
                    startActivity(intent2);
                    finish();

                    break;


            }
            super.handleMessage(msg);


        }


    };


    public String mGetValue(String s) {
        int ai = valur.indexOf(s);
        String as = valur.substring((ai + s.length() + 3), valur.length());
        return as.substring(0, as.indexOf("\""));

    }


    public Bitmap setB() {
        Bitmap p = null;
        try {
            p = BitmapFactory.decodeStream(getAssets().open("error.html"));
        } catch (IOException e) {
        }
        return p;
    }
}
