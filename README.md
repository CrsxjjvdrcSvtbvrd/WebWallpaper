# WebWallpaper
在安卓上使用WallpaperEngine的web壁纸，在平板设备上效果更佳。

如何使用：

###### 1：新建AndroidStudio项目

添加assets文件夹并新建文件夹a，将settings目录下的文件放在a中。

assets中新建文件夹b，将WallpaperEngine的壁纸文件放在b中。

```
文件结构
assets---
		|
		----a
		|	----index.html
		|	----......
		----b
			----project.json
			----......
```



###### 2：创建类MainService继承com.wodtian.webwallpaper.WallpaperService

在manifest中添加：

```xml
<service android:name=".MainService"
    android:permission="android.permission.BIND_WALLPAPER"
    android:enabled="true">
    <intent-filter>
        <action android:name="android.service.wallpaper.WallpaperService"/>
    </intent-filter>
    <meta-data
        android:name="android.service.wallpaper"
        android:resource="@xml/wallpaper"/>
</service>
```

xml/wallpaper:

```xml
<?xml version="1.0" encoding="utf-8"?>
<wallpaper xmlns:android="http://schemas.android.com/apk/res/android"
    android:thumbnail="@mipmap/ic_launcher">
</wallpaper>
```



###### 3：创建类MainActivity继承WallpaperSetting(参考MainActivity)

```java
static {
	WebWallpaper.PATH_SETTING="a";//assets中settings的目录
	WebWallpaper.PATH_WALLPAPER="b";//assets中壁纸文件的目录
}
```

```java
//继承需要实现的方法
//返回壁纸服务.class
@Override
public ComponentName getService() {
    return new ComponentName(this,MainService.class);
}
//返回程序包名，用来跳转到设置-应用。
@Override
public String getPackageName() {
    return "com.wodtian.webwallpaper";
}
```

###### 4：运行

![截图](/screenshot/settings.jpg)

###### 5：已知问题：

运行效率低（WebView硬伤）

有些壁纸不能使用

关闭壁纸需要跳转到系统设置如何手动点击强行停止

##### 参考

[1]: https://gist.github.com/iangilman/71650d46384a2d4ae6387f2d4087cc37	"Android WallpaperService with WebView"

