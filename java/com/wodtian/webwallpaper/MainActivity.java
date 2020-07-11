package com.wodtian.webwallpaper;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends WallpaperSetting {

    static {
        WebWallpaper.PATH_SETTING="setting";
        WebWallpaper.PATH_WALLPAPER="a";
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