package com.wodtian.webwallpaper;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import java.util.Random;

public class MainActivity extends WallpaperSetting {

    static {
        WebWallpaper.PATH_SETTING="setting";
        //Random random = new Random();
        //WebWallpaper.PATH_WALLPAPER="wallpaper"+random.nextInt(3);
        WebWallpaper.PATH_WALLPAPER="wallpaper4";
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public ComponentName getService() {
        return new ComponentName(MainActivity.this,MainService.class);
    }

    @Override
    public String getPackageName() {
        return "com.wodtian.webwallpaper";
    }
}