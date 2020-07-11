package com.wodtian.webwallpaper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public abstract class WallpaperSetting extends Activity {
    static final String TAG = "Wallpaper Setting";
    SettingWebView webView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new SettingWebView(this);
        webView.loadUrl(WebWallpaper.getSettingURL());
        setContentView(webView);
    }
    public void alert(String title,String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.show();
    }
    static class SettingWebView extends WebView{
        WallpaperSetting wallpaperSetting;
        public SettingWebView(WallpaperSetting setting) {
            super(setting);
            wallpaperSetting = setting;
            getSettings().setJavaScriptEnabled(true);
            getSettings().setDomStorageEnabled(true);
            //getSettings().setAllowFileAccessFromFileURLs(true);
            getSettings().setAllowUniversalAccessFromFileURLs(true);
            setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    return false;
                }
            });
            setWebChromeClient(new WebChromeClient(){
                @Override
                public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                    Log.d("web console", "onConsoleMessage: "+consoleMessage.message());
                    return super.onConsoleMessage(consoleMessage);
                }
            });
            addJavascriptInterface(wallpaperSetting,"android");
        }
    }
    @JavascriptInterface
    public void toast(String s){
        Log.d(TAG, "toast: called");
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }
    @JavascriptInterface
    public String getProjectJSON(){
        Log.d(TAG, "getProjectJSON: called");
        try{
            return WebWallpaper.openAssets(this,WebWallpaper.getWallpaperFile("project.json"));
        }catch (IOException e){
            e.printStackTrace();
            return e.getMessage();
        }
    }
    @JavascriptInterface
    public String getWallpaperFile(String s){
        return WebWallpaper.getAssetsURL(WebWallpaper.getWallpaperFile(s));
    }
    @JavascriptInterface
    public String getCustomizeJS(){
        Log.d(TAG, "getCustomizeJS: called");
        try {
            return WebWallpaper.readFile(new File(getFilesDir(),"my.js"));
        } catch (IOException e) {
            e.printStackTrace();
            return "0";
        }
    }
    @JavascriptInterface
    public String getCustomizeJSURL(){
        try {
            return new File(getFilesDir(),"my.js").toURI().toURL().toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "";
        }
    }
    @JavascriptInterface
    public void saveCustomizeJS(String p){
        Log.d(TAG, "writeProperties: called");
        try {
            WebWallpaper.writeFile(new File(getFilesDir(),"my.js"),p);
        } catch (Exception e) {
            e.printStackTrace();
            toast(e.getMessage());
        }
    }
    @JavascriptInterface
    public String getSavedProperties(){
        Log.d(TAG, "getSavedProperties: called");
        try {
            return WebWallpaper.readFile(new File(getFilesDir(),"profile.json"));
        } catch (IOException e) {
            e.printStackTrace();
            return "0";
        }
    }
    @JavascriptInterface
    public void writeProperties(String p){
        Log.d(TAG, "writeProperties: called");
        try {
            WebWallpaper.writeFile(new File(getFilesDir(),"profile.json"),p);
        } catch (Exception e) {
            e.printStackTrace();
            toast(e.getMessage());
        }
    }
    @JavascriptInterface
    public void closeWallpaper(){
        toast("手动点击强行停止");
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+getPackageName()));
        startActivity(intent);
    }
    @JavascriptInterface
    public void wallpaperFile(String s){
        WebWallpaper.FILE_WALLPAPER=s;
    }
    @JavascriptInterface
    public void openBrowser(String url){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
    @JavascriptInterface
    public void setWallpaper(){
        Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,getService());
        startActivity(intent);
    }
    @Override
    protected void onDestroy() {
        if(webView!=null)
            webView.destroy();
        super.onDestroy();
    }
    public abstract ComponentName getService();
    public abstract String getPackageName();
}
