package com.changanford.common.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.changanford.common.MyApp;
import com.changanford.common.utilext.ToastUtilsKt;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.UUID;


public class DeviceUtils {
    public static int getNavigationBarHeight() {
        int height = 0;
        try{
            Resources resources = MyApp.mContext.getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height","dimen", "android");
            height = resources.getDimensionPixelSize(resourceId);
        }catch (Exception e){
            height = 0;
        }
        Log.v("dbw", "Navi height:" + height);
        return height;
    }
    public static String getMetaData(Context context, String key) {
        String value = null;
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            value = appInfo.metaData.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (value == null) value = "";
        return value;
    }

    /**
     * 获取应用程序的IMEI号
     */
    public static String getIMEI(Context context) {
        if (MConstant.INSTANCE.isPopAgreement()){
            return "";
        }
        String imei = "";
        String[] permissions = {Manifest.permission.READ_PHONE_STATE};
        int check = ContextCompat.checkSelfPermission(context, permissions[0]);
        if (check == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imei = telephonyManager.getDeviceId();
        } else {
            ToastUtilsKt.toast("请开启获取手机信息权限");
        }
        return imei;
    }

    /**
     * 获取应用程序的IMSI号
     */
    public static String getIMSI(Context context) {
        if (MConstant.INSTANCE.isPopAgreement()){
            return "";
        }
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = mTelephonyMgr.getSubscriberId();

        return imsi;
    }

    /**
     * 获取应用程序的UUID号
     */
    @SuppressLint("MissingPermission")
    public static String getUUID() {
        if (MConstant.INSTANCE.isPopAgreement()){
            return "";
        }
        final TelephonyManager tm = (TelephonyManager) MyApp.mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String tmDevice = "";
        final String androidId;
        try {
            tmDevice = "" + tm.getDeviceId();
        } catch (Exception e) {
        }
        androidId = "" + Settings.Secure.getString(MyApp.mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32));
        String uniqueId = deviceUuid.toString();
        return uniqueId;
    }

    /**
     * 获取设备品牌
     */
    public static String getDeviceBRAND() {
        String brand = Build.BRAND;
        return brand;
    }


    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     *
     * @param context Context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    /**
     * 获取设备的型号
     * 获取手机的型号 设备名称。如：SM-N9100（三星Note4）
     */
    public static String getDeviceModel() {
        String model;
        model = Build.MODEL;
        return model;
    }

    /**
     * 设备的唯一标识
     */
    public static String getDeviceFINGERPRINT() {
        String model = Build.FINGERPRINT;
        return model;
    }

    /**
     * 获取设备的系统版本号
     */
    public static int getDeviceSDK() {
        int sdk = Build.VERSION.SDK_INT;
        return sdk;
    }

    /**
     * 获取系统版本字符串。如4.1.2 或7.1.2等
     *
     * @return
     */
    public static String getDeviceVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取包名
     */
    public static String getPackageName(Context context) {
        PackageInfo info;
        String packageNames = null;//当前版本的包名
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            packageNames = info.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageNames;
    }


    /**
     * 获取当前应用程序的版本名称
     */
    public static String getversionName() {
        PackageInfo info;
        String versionName = null;//当前应用的版本名称
        try {
            info = MyApp.mContext.getPackageManager().getPackageInfo(MyApp.mContext.getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 获取当前应用程序的版本号
     */
    public static int getVersionCode(Context context) {
        PackageInfo info;
        int versionCode = 0;//当前版本的版本号
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取手机厂商
     *
     * @return
     */
    public static String getManuFacture() {
        String carrier = Build.MANUFACTURER;
        return carrier;
    }

    /**
     * 获取Mac地址
     *
     * @return
     */
    public static String getMac() {
        String macSerial = "null";
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return macSerial;
    }

    /**
     * 获取ip地址
     *
     * @return
     */
    public static String getHostIP() {

        String hostIp = "";
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            Log.i("yao", "SocketException");
            e.printStackTrace();
        }
        return hostIp;
    }

    @SuppressLint("HardwareIds")
    public static String getAndroidID() {
        String id = Settings.Secure.getString(
                MyApp.mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
        return id == null ? "" : id;
    }


}
