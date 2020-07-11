javascript:function loadProfile(){
    let p = window.androidWallpaperInterface.getProperties();
    let a = JSON.parse(p);
    //let p = JSON.parse(window.androidWallpaperInterface.getProperties());
	window.wallpaperPropertyListener.applyUserProperties(a);
}
javascript:function loadCustomizeJS(){
    let js = document.createElement("script");
    let src = window.androidWallpaperInterface.getCustomizeJS();
    console.log(src);
    js.src=src;
    document.body.appendChild(js);
}
