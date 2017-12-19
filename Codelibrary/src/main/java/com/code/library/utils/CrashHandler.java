package com.code.library.utils;

import android.content.Context;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

public class CrashHandler implements UncaughtExceptionHandler {
    private Context context;

    public CrashHandler(Context context) {
        this.context = context;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (LogUtils.isDEBUG()) {
            String CrashTime = TimeUtils.getTime();// 时间
            String ErrorInfo = getErrorInfo(ex);// 错误信息
            if (LogUtils.isShowMoreInfo()) {
                // 获取设备信息
                String PackageName = context.getPackageName();// 包名
                String VersionName = VersionUtils.getVersionName(context);// 版本号
                int VersionCode = VersionUtils.getVersionCode(context);// 识别码
                String DeviceImei = DeviceInfoUtils.getImei(context);
                String DeviceName = DeviceInfoUtils.getDeviceName();
                String DeviceModel = DeviceInfoUtils.getDeviceModel();// 设备型号
                String Manufacturer = DeviceInfoUtils.getManufacturer();// 设备制造商
                String SDK = DeviceInfoUtils.getSDK();
                String OSName = DeviceInfoUtils.getOSName();
                String OSVersion = DeviceInfoUtils.getOsVersion();
                String ScreenInfo = DeviceInfoUtils.getScreenWidth(context)
                        + "*" + DeviceInfoUtils.getScreenHeight(context)
                        + "   density:"
                        + DeviceInfoUtils.getScreenDensity(context);// 屏幕信息
                boolean isRoot = DeviceInfoUtils.isRoot();
                boolean isTablet = DeviceInfoUtils.isPad(context);
                String MemoryInfo = DeviceInfoUtils.getAvailMemory(context)
                        + " / " + DeviceInfoUtils.getTotalMemory(context);
                String PhoneFlash = DeviceInfoUtils.getCapacity(context);
                String SDFlash = DeviceInfoUtils.getSDcardCapacity(context);
                String CPU_Type = DeviceInfoUtils.getCpuType();
                String CPU_ABI = DeviceInfoUtils.getCPU_ABI();
                boolean isNetWork = NetworkUtil.isAvailable(context);
                String NetWorkType = NetworkUtil.getNetType(context);

                String logString = "============ CrashTime: " + CrashTime
                        + " ============\nPackageName===>" + PackageName
                        + "\nVersionName===>" + VersionName
                        + "\nVersionCode===>" + VersionCode
                        + "\n----------DeviceInfos----------\nDeviceImei===>"
                        + DeviceImei + "\nDeviceName===>" + DeviceName
                        + "\nDeviceModel===>" + DeviceModel
                        + "\nManufacturer===>" + Manufacturer + "\nSDK===>"
                        + SDK + "\nOSName===>" + OSName + "\nOSVersion===>"
                        + OSVersion + "\nScreenInfo===>" + ScreenInfo
                        + "\nisRoot===>" + isRoot + "\nisTablet===>" + isTablet
                        + "\nisNetWork===>" + isNetWork + "\nNetWorkType===>"
                        + NetWorkType + "\nMemoryInfo===> " + MemoryInfo
                        + "\nPhoneFlash===> " + PhoneFlash + "\nSDFlash===> "
                        + SDFlash + "\nCPU_Type===>" + CPU_Type
                        + "\nCPU_ABI===>" + CPU_ABI
                        + "\n----------ErrorInfos----------\n" + ErrorInfo;
                LogUtils.e(logString);
            } else {
                String logString = "============ CrashTime: " + CrashTime
                        + " ============\n" + ErrorInfo;
                LogUtils.e(logString);
            }
        }
        ActivityManager.getInstance().clean();
        System.exit(0);
    }

    private static String getErrorInfo(Throwable arg1) {
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        arg1.printStackTrace(pw);
        pw.close();
        String error = writer.toString();
        return error;
    }

}
