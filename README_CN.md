# CustomCamera

这是一个Android自定义相机的项目，现在它有基本的拍照功能， 前后置摄像头，闪光灯模式，延迟拍摄，正方形取景框，加水印效果，接下来我会研究点击区域聚焦，Camera2 相关功能

你可以下载apk文件夹， 然后安装apk demo

**如果你想自己定义界面的话可以详细查看CameraActivity和更改它的布局文件activity_camera.xml.**

具体可以查看我的博客  
[Android 自定义Camera(一)](http://blog.csdn.net/coderyue/article/details/50927177)  
[Android 自定义Camera(二)](http://blog.csdn.net/coderyue/article/details/50966918)

**[DownLoad Apk](https://github.com/jinguangyue/Android-CustomCamera/blob/master/apk/CustomCamera.apk?raw=true)**


**简单说明**

在我的MainActivity里可以这样调用
```
CameraUtil.getInstance().camera(MainActivity.this);
```

然后在onActivityResult里面获取返回的图片, 你也可以新建你的Activity这样调用
```
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if(resultCode != AppConstant.RESULT_CODE.RESULT_OK){
        return;
    }

    if(requestCode == AppConstant.REQUEST_CODE.CAMERA){
        String img_path = data.getStringExtra(AppConstant.KEY.IMG_PATH);
        int picWidth = data.getIntExtra(AppConstant.KEY.PIC_WIDTH, 0);
        int picHeight = data.getIntExtra(AppConstant.KEY.PIC_HEIGHT, 0);
        
        img.setLayoutParams(new RelativeLayout.LayoutParams(picWidth, picHeight));
        img.setImageURI(Uri.parse(img_path));
    }
}
```

#注意: 如果不需要裁剪正方形视频 可以将ffmpeg工程去掉 减少apk体积