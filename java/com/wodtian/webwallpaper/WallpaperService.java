package com.wodtian.webwallpaper;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

public class WallpaperService extends android.service.wallpaper.WallpaperService {
    class WebEngine extends Engine{
        WallpaperService service;
        WallpaperView wallpaperView;
        public WebEngine(WallpaperService wallpaperService){
            service = wallpaperService;
        }
        private Canvas canvas;
        private SurfaceHolder surfaceHolder;
        private boolean drawing = false;
        private boolean running = false;
        private int perFrame = 30;
        private Runnable frame = new Runnable() {
            @Override
            public void run() {
                drawing = true;
                for(int i=0;running&&i<perFrame;i++) {
                    if (wallpaperView == null) break;
                    try {
                        canvas = surfaceHolder.lockCanvas();
                        if (canvas != null) {
                            wallpaperView.draw(canvas);
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                drawing = false;
            }
        };
        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            log("on surface created");
            surfaceHolder = holder;
            if(wallpaperView!=null)
                wallpaperView.destroy();
            wallpaperView = new WallpaperView(this);
            wallpaperView.loadUrl(WebWallpaper.getWallpaperFileURL(WebWallpaper.FILE_WALLPAPER));
            running = true;
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            log("on surface destroy");
            if(wallpaperView!=null){
                wallpaperView.destroy();
                wallpaperView=null;
            }
            running = false;
            super.onSurfaceDestroyed(holder);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            log(String.format("on surface changed : format:%d,width:%d,height:%d",format,width,height));
            super.onSurfaceChanged(holder, format, width, height);
            if(wallpaperView!=null){
                int wSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
                int hSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
                wallpaperView.measure(wSpec,hSpec);
                wallpaperView.layout(0,0,width,height);
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            log("on visibility changed "+visible);
            super.onVisibilityChanged(visible);
            if(wallpaperView==null)return;
            if(visible){
                wallpaperView.execJS("resumeWallpaper()");
                running = true;
            }else{
                wallpaperView.execJS("pauseWallpaper");
                running = false;
            }
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            if(wallpaperView!=null)
                wallpaperView.onTouchEvent(event);
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            log("on create");
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            log("on destroy");
        }
        private void log(String msg){
            Log.d("WebWallpaperEngine",msg);
        }
        private void err(String msg){
            Log.e("WebWallpaperEngine",msg);
        }
        @JavascriptInterface
        public void drawFrame(){
            if(!drawing)
                new Thread(frame).start();
            /*if(wallpaperView==null)return;
            try{
                canvas = surfaceHolder.lockCanvas();
                if(canvas!=null){
                    wallpaperView.draw(canvas);
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }catch (Exception e){
                e.printStackTrace();
            }*/
        }
        @JavascriptInterface
        public String getProperties(){
            log("get properties called");
            try {
                return WebWallpaper.readFile(new File(getFilesDir(),"profile.json"));
            } catch (IOException e) {
                e.printStackTrace();
                return "0";
            }
        }
        @JavascriptInterface
        public void toast(String s){
            Toast.makeText(service,s,Toast.LENGTH_SHORT).show();
        }
        @JavascriptInterface
        public void openApp(String pkg){
            try{
                Intent intent = getPackageManager().getLaunchIntentForPackage(pkg);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }catch (Exception e){}
        }
        @JavascriptInterface
        public void openBrowser(String url){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
        @JavascriptInterface
        public String getCustomizeJS(){
            try {
                return new File(getFilesDir(),"my.js").toURI().toURL().toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "";
            }
        }
        @JavascriptInterface
        public String getAppList(){
            JSONArray array = new JSONArray();
            try {
                PackageManager manager = getPackageManager();
                List<PackageInfo> packageInfos = manager.getInstalledPackages(0);
                for (PackageInfo info : packageInfos) {
                    if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        JSONObject object = new JSONObject();
                        object.put("name", manager.getApplicationLabel(info.applicationInfo).toString());
                        object.put("package",info.packageName);
                        array.put(object);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return array.toString();
        }
    }
    @Override
    public Engine onCreateEngine() {
        return new WebEngine(this);
    }
    class WallpaperView extends WebView{
        String TAG = "Wallpaper View";
        private ValueCallback<String> valueCallback = new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.d("js callback",value);
            }
        };
        public WallpaperView(WebEngine engine){
            super(engine.service);
            getSettings().setJavaScriptEnabled(true);
            getSettings().setDomStorageEnabled(true);
            setWebChromeClient(new WebChromeClient() {
                @Override
                public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                    Log.d("Wallpaper Console:", consoleMessage.message());
                    return super.onConsoleMessage(consoleMessage);
                }

                @Nullable
                @Override
                public Bitmap getDefaultVideoPoster() {
                    return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
                }
            });
            setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    return false;
                }
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    inject();
                }
            });
            addJavascriptInterface(engine,"androidWallpaperInterface");
            setLayerType(View.LAYER_TYPE_HARDWARE,null);
            setHorizontalScrollBarEnabled(false);
            setVerticalScrollBarEnabled(false);
        }
        public void inject(){
            execJS("javascript:function loadCustomizeJS(){\n" +
                    "    let js = document.createElement(\"script\");\n" +
                    "    let src = window.androidWallpaperInterface.getCustomizeJS();\n" +
                    "    js.src=src;\n" +
                    "    document.body.appendChild(js);\n" +
                    "}");
            execJS("loadCustomizeJS()");
            execJS("javascript:function InjectJS(){\n" +
                    "\tlet p = document.createElement(\"script\");\n" +
                    "\tp.innerHTML=\"var animationFrame;\\n\" +\n" +
                    "            \"function frame() {\\n\" +\n" +
                    "            \"  animationFrame = requestAnimationFrame(frame);\\n\" +\n" +
                    "            \"  window.androidWallpaperInterface.drawFrame();\\n\" +\n" +
                    "            \"}\\n\" +\n" +
                    "            \"function pauseWallpaper() {\\n\" +\n" +
                    "            \"  cancelAnimationFrame(animationFrame);\\n\" +\n" +
                    "            \"} \\n\" +\n" +
                    "            \"function resumeWallpaper() {\\n\" +\n" +
                    "            \"  frame();\\n\" +\n" +
                    "            \"}\\n\" +\n" +
                    "            \"frame();\";\n" +
                    "\tdocument.body.appendChild(p);\n" +
                    "\tframe();\n" +
                    "}");
            execJS("InjectJS()");
            execJS("javascript:function loadProfile(){\n" +
                    "\tlet p = JSON.parse(window.androidWallpaperInterface.getProperties());\n" +
                    "\tif(window.wallpaperPropertyListener!=undefined)\n" +
                    "\t\twindow.wallpaperPropertyListener.applyUserProperties(p);\n" +
                    "}");
            execJS("try{\n" +
                    "\tloadProfile();\n" +
                    "}catch(error){\n" +
                    "\tconsole.log(error);\n" +
                    "}");
        }
        public void execJS(String js){
            evaluateJavascript(js,valueCallback);
        }
    }
}
