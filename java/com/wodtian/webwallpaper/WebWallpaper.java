package com.wodtian.webwallpaper;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class WebWallpaper {
    public static String PATH_SETTING = "";
    public static String PATH_WALLPAPER = "";
    public static String FILE_WALLPAPER="";
    public static String getWallpaperFile(String s){
        return PATH_WALLPAPER
                +(PATH_SETTING.endsWith("/")?"":"/")
                +s;
    }
    public static String getWallpaperFileURL(String s){
        return "file:////android_asset/"+
                (PATH_WALLPAPER.startsWith("/")?PATH_WALLPAPER.substring(1):PATH_WALLPAPER)
                +(PATH_SETTING.endsWith("/")?"":"/")
                +s;
    }
    public static String getSettingURL(){
        return "file:////android_asset/"+
                (PATH_SETTING.startsWith("/")?PATH_SETTING.substring(1):PATH_SETTING)
                +(PATH_SETTING.endsWith("/")?"":"/")
                +"index.html";
    }
    public static String getWallpaperURL(){
        return "file:////android_asset/"+
                (PATH_WALLPAPER.startsWith("/")?PATH_WALLPAPER.substring(1):PATH_WALLPAPER)
                +(PATH_SETTING.endsWith("/")?"":"/")
                +"project.json";
    }
    public static String getAssetsURL(String s){
        return "file:////android_asset/"+
                (s.startsWith("/")?s.substring(1):s);
    }
    public static String openAssets(Context context,String file) throws IOException {
        InputStream inputStream = context.getAssets().open(file);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        inputStream.close();
        return new String(bytes);
    }
    public static String readInputStream(InputStream inputStream) throws IOException{
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        inputStream.close();
        return new String(bytes);
    }
    public static void writeOutputStream(OutputStream outputStream,String s) throws IOException{
        outputStream.write(s.getBytes());
        outputStream.close();
    }
    public static String readFile(File file) throws IOException{
        if(!file.exists())
            file.createNewFile();
        return readInputStream(new FileInputStream(file));
    }
    public static void writeFile(File file,String s) throws IOException{
        if(!file.exists())
            file.createNewFile();
        writeOutputStream(new FileOutputStream(file),s);
    }
}
