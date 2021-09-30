# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
###如果开启了混淆，并且调用了BVP的setScrollDuration方法，则必须添加以下混淆配置：
  -keep class androidx.recyclerview.widget.**{*;}
    -keep class androidx.viewpager2.widget.**{*;}
    ###如果开启了混淆，并且调用了BVP的setScrollDuration方法，则必须添加以下混淆配置：


    ### 加载状态。
    -dontwarn com.kingja.loadsir.**
    -keep class com.kingja.loadsir.** {*;}
    ### 加载状态。