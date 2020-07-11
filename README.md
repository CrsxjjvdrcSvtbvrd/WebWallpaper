# WebWallpaper
在安卓上使用WallpaperEngine的web壁纸

如何使用：

###### 1：新建AndroidStudio项目

添加assets文件夹并新建文件夹a，将settings目录下的文件放在a中。

assets中新建文件夹b，将WallpaperEngine的壁纸文件放在b中。

###### 2：创建类MainService继承com.wodtian.webwallpaper.WallpaperService

###### 3：创建类MainActivity继承WallpaperSetting(参考MainActivity)

```java
static {
	WebWallpaper.PATH_SETTING="a";//assets中settings的目录
	WebWallpaper.PATH_WALLPAPER="b";//assets中壁纸文件的目录
}
```

```java
//壁纸服务.class
@Override
public ComponentName getService() {
    return new ComponentName(this,MainService.class);
}
//程序包名
@Override
public String getPackageName() {
    return "com.wodtian.webwallpaper";
}
```

###### 4：运行

![截图](/screenshot/settings.jpg)