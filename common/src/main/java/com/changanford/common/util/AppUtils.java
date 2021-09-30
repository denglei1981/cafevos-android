package com.changanford.common.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

//import com.alibaba.sdk.android.push.CloudPushService;
//import com.alibaba.sdk.android.push.CommonCallback;
//import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.changanford.common.basic.BaseApplication;
import com.gyf.immersionbar.ImmersionBar;
import com.luck.picture.lib.entity.LocalMedia;
//import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * Created by ${zy} on 2018/8/6.
 */

public class AppUtils {
    public static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }


    /**
     * 检测是否安装支付宝
     *
     * @param context
     * @return
     */
    public static boolean checkAliPayInstalled(Context context) {

        Uri uri = Uri.parse("alipays://platformapi/startApp");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(context.getPackageManager());
        return componentName != null;
    }

    /**
     * 判断 用户是否安装微信客户端
     */
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断 用户是否安装QQ客户端
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equalsIgnoreCase("com.tencent.qqlite") || pn.equalsIgnoreCase("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * sina
     * 判断是否安装新浪微博
     */
    public static boolean isSinaInstalled(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.sina.weibo")) {
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * gaode
     * 判断是否安装了高德
     */
    public static boolean isgaodeInstalled(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.autonavi.minimap")) {
                    return true;
                }
            }
        }

        return false;
    }


    public static void relaunchApp() {
        PackageManager packageManager = BaseApplication.INSTANT.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(BaseApplication.INSTANT.getPackageName());
        if (intent == null) return;
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        BaseApplication.INSTANT.startActivity(mainIntent);
        System.exit(0);
    }

    /*
    普通安装
     */
    public static void installApp(final String filePath) {
        installApp(getFileByPath(filePath));
    }

    /**
     * Install the app.
     * <p>Target APIs greater than 25 must hold
     * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}</p>
     *
     * @param file The file.
     */
    public static void installApp(final File file) {
        if (!isFileExists(file)) return;
        BaseApplication.INSTANT.startActivity(getInstallAppIntent(file, true));
    }

    private static Intent getInstallAppIntent(final File file, final boolean isNewTask) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data;
        String type = "application/vnd.android.package-archive";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            data = Uri.fromFile(file);
        } else {
            String authority = BaseApplication.INSTANT.getPackageName() + ".versionProvider";
            data = FileProvider.getUriForFile(BaseApplication.INSTANT, authority, file);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        BaseApplication.INSTANT.grantUriPermission(BaseApplication.INSTANT.getPackageName(), data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(data, type);
        return isNewTask ? intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) : intent;
    }


    /**
     * 静默安装
     *
     * @param file
     * @param params
     * @return
     */
//    public static boolean installAppSilent(final File file,
//                                           final String params
//    ) {
//        if (!isFileExists(file)) return false;
//        String filePath = '"' + file.getAbsolutePath() + '"';
//        String command = "LD_LIBRARY_PATH=/vendor/lib*:/system/lib* pm install " +
//                (params == null ? "" : params + " ")
//                + filePath;
//        ShellUtils.CommandResult commandResult = ShellUtils.execCmd(command, isDeviceRooted());
//        if (commandResult.successMsg != null
//                && commandResult.successMsg.toLowerCase().contains("success")) {
//            return true;
//        } else {
//            Log.e("AppUtils", "installAppSilent successMsg: " + commandResult.successMsg +
//                    ", errorMsg: " + commandResult.errorMsg);
//            return false;
//        }
//    }
    private static boolean isFileExists(final File file) {
        return file != null && file.exists();
    }

    private static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isDeviceRooted() {
        String su = "su";
        String[] locations = {"/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/",
                "/system/bin/failsafe/", "/data/local/xbin/", "/data/local/bin/", "/data/local/",
                "/system/sbin/", "/usr/bin/", "/vendor/bin/"};
        for (String location : locations) {
            if (new File(location + su).exists()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置状态栏高度
     *
     * @param view
     * @param activity
     */
    public static void setStatusBarHeight(View view, Activity activity) {
        int height = ImmersionBar.getStatusBarHeight(activity);
        view.getLayoutParams().height = height;
    }

    /**
     * 设置状态栏marginTop
     *
     * @param view
     * @param activity
     */
    public static void setStatusBarMarginTop(View view, Activity activity) {
        int height = ImmersionBar.getStatusBarHeight(activity);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) params;
            marginParams.topMargin = marginParams.topMargin + height + 10;
        }
    }

    /**
     * 设置状态栏marginTop
     *
     * @param view
     * @param activity
     */
    public static void setStatusBarPaddingTop(View view, Activity activity) {
        int height = ImmersionBar.getStatusBarHeight(activity);
        view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + height, view.getPaddingRight(), view.getPaddingBottom());
    }


    /**
     * 获取最终地址
     */
    public static String getFinallyPath(LocalMedia media) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return media.getAndroidQToPath();
        } else {
            String path;
            if (media.isCut() && !media.isCompressed()) {
                // 裁剪过
                path = media.getCutPath();
            } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                path = media.getCompressPath();
            } else {
                // 原图
                path = media.getPath();
            }
            return path;
        }

    }
//
//    /**
//     * 登录成功时调用
//     *
//     * @param userid
//     */
//    public static void binduserid(String userid) {
//        CloudPushService pushService = PushServiceFactory.getCloudPushService();
//        if (!SPUtils.isonline()){
//            userid = "dev"+userid;
//        }
//        pushService.bindAccount(userid, new CommonCallback() {
//            @Override
//            public void onSuccess(String s) {
//
//            }
//
//            @Override
//            public void onFailed(String s, String s1) {
//
//            }
//        });
//    }
//
//    /**
//     * 退出登录时调用
//     *
//     * @param userid
//     */
//    public static void Unbinduserid() {
//        CloudPushService pushService = PushServiceFactory.getCloudPushService();
//        pushService.unbindAccount(new CommonCallback() {
//            @Override
//            public void onSuccess(String s) {
//
//            }
//
//            @Override
//            public void onFailed(String s, String s1) {
//
//            }
//        });
//    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isIgnoringBatteryOptimizations() {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) BaseApplication.curActivity.getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(getPackageName());
        }
        return isIgnoring;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void requestIgnoreBatteryOptimizations() {
        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            BaseApplication.curActivity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ignoreSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("TLS");
            // trustAllCerts信任所有的证书
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
        }
    }

    public static String getPackageName() {
        return "com.changan.uni";
    }
}
