package com.changanford.my.ui

import android.app.AppOpsManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.R
import com.changanford.common.basic.BaseApplication
import com.changanford.common.constant.HawkKey
import com.changanford.common.manger.RouterManger
import com.changanford.common.manger.UserManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.ui.dialog.AlertThreeFilletDialog
import com.changanford.common.ui.dialog.LoadDialog
import com.changanford.common.util.ConfirmTwoBtnPop
import com.changanford.common.util.DeviceUtils
import com.changanford.common.util.FastClickUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey.USER_LOGIN_STATUS
import com.changanford.common.util.gio.updateMainGio
import com.changanford.common.utilext.CleanDataUtils
import com.changanford.common.wutil.WCommonUtil
import com.changanford.my.BaseMineUI
import com.changanford.my.databinding.UiSeetingBinding
import com.changanford.my.viewmodel.SignViewModel
import com.orhanobut.hawk.Hawk
import java.lang.reflect.InvocationTargetException
import kotlin.concurrent.thread

/**
 *  文件名：SettingUI
 *  创建者: zcy
 *  创建日期：2021/9/9 13:40
 */
@Route(path = ARouterMyPath.MineSettingUI)
class SettingUI : BaseMineUI<UiSeetingBinding, SignViewModel>() {

    override fun initView() {
        updateMainGio("设置页", "设置页")
        binding.setToolbar.toolbarTitle.text = "设置"
        binding.setToolbar.toolbar.setNavigationOnClickListener {
            back()
        }
        binding.btnLoginOut.isEnabled = UserManger.isLogin()
        binding.btnLoginOut.visibility = if (UserManger.isLogin()) View.VISIBLE else View.GONE

        binding.btnLoginOut.setOnClickListener {
            var confirmPop = ConfirmTwoBtnPop(this)
            confirmPop.contentText.text = "确认退出登录？"
            confirmPop.btnConfirm.setOnClickListener {
                confirmPop.dismiss()
                viewModel.loginOut()
            }
            confirmPop.btnCancel.setOnClickListener {
                confirmPop.dismiss()
            }
            confirmPop.showPopupWindow()
        }

        binding.setSafe.setOnClickListener {
            RouterManger.needLogin(true).startARouter(ARouterMyPath.AccountSafeUI)
        }
        binding.setbg.setOnClickListener {
            if (MConstant.isCanQeck && FastClickUtils.fastRepeatClick()) {
                startARouter(ARouterMyPath.BateActivity)
            }
        }
        binding.setFord.setOnClickListener {
            startARouter(ARouterMyPath.AboutUI)
        }
        binding.swRecommend.apply {
            setOnCheckedChangeListener { _, isChecked ->
                Hawk.put(HawkKey.SETTING_RECOMMEND, isChecked)
            }
        }
        binding.swRecommend.isChecked = Hawk.get(HawkKey.SETTING_RECOMMEND, true)
        //推送通知
        binding.setNotice.setOnClickListener {
            var dilaog = AlertThreeFilletDialog(BaseApplication.curActivity).builder()
            dilaog.setTitle("温馨提示")
                .setMsg("是否前往设置修改消息推送权限？")
                .setCancelable(true)
                .setNegativeButton(
                    "取消", R.color.actionsheet_blue
                ) {
                    dilaog.dismiss()
                }
                .setPositiveButton("去设置", R.color.actionsheet_blue) {
                    WCommonUtil.openNotificationSetting(this)

                }.show()
        }
        binding.setVersion.text = "版本${DeviceUtils.getversionName()}"

        var cache = CleanDataUtils.getTotalCacheSize(this)
        if (!cache.contains("0.00")) {
            binding.setCacheSize.text = CleanDataUtils.getTotalCacheSize(this)
            binding.setCacheSize.setOnClickListener {
                var d = LoadDialog(this)
                d.setLoadingText("正在清除缓存...")
                d.show()
                thread {
                    CleanDataUtils.clearAllCache(this);
                    runOnUiThread {
                        d.dismiss()
                        var cache = CleanDataUtils.getTotalCacheSize(this)
                        if (cache.contains("0.00")) {
                            binding.setCacheSize.text = ""
                        }
                    }
                }
                MConstant.isDownLoginBgSuccess = false
            }
        }
        LiveDataBus.get().with(USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this, Observer {
                finish()
            })

        LiveDataBus.get().with(USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this, Observer {
                //退出登录
                it?.let {
                    if (it == UserManger.UserLoginStatus.USER_LOGIN_OUT) {
                        finish()
                    }
                }
            })

    }

    override fun initData() {
        //
    }

    override fun onResume() {
        super.onResume()
        binding.setMsgenable.text =
            if (NotificationUtil.isNotificationEnabled(this)) "已开启" else "已关闭"
    }
}

object NotificationUtil {
    /**
     * 打开手机设置页面
     * @param context Context
     */
    fun setNotification(context: Context) {
        val enabled = isNotificationEnabled(context)
        if (!enabled) {
            val localIntent = Intent()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                localIntent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                localIntent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                localIntent.putExtra("app_uid", context.applicationInfo.uid)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                localIntent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                localIntent.putExtra("app_package", context.packageName)
                localIntent.putExtra("app_uid", context.applicationInfo.uid)
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                localIntent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                localIntent.addCategory(Intent.CATEGORY_DEFAULT)
                localIntent.data = Uri.parse("package:" + context.packageName)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                localIntent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                localIntent.data = Uri.fromParts("package", context.packageName, null)
            } else {
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                if (Build.VERSION.SDK_INT >= 9) {
                    localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                    localIntent.data = Uri.fromParts("package", context.packageName, null)
                } else if (Build.VERSION.SDK_INT <= 8) {
                    localIntent.action = Intent.ACTION_VIEW
                    localIntent.setClassName(
                        "com.android.settings",
                        "com.android.setting.InstalledAppDetails"
                    )
                    localIntent.putExtra(
                        "com.android.settings.ApplicationPkgName",
                        context.packageName
                    )
                }
            }
            context.startActivity(localIntent)
        }
    }

    /**
     * 判断当前app在手机中是否开启了允许消息推送
     * @param mContext Context
     * @return Boolean
     */
    fun isNotificationEnabled(mContext: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mNotificationManager =
                mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.areNotificationsEnabled()
        } else if (Build.VERSION.SDK_INT >= 24) {
            val mNotificationManager =
                mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.areNotificationsEnabled()
        } else if (Build.VERSION.SDK_INT >= 19) {
            val CHECK_OP_NO_THROW = "checkOpNoThrow"
            val OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION"
            val appOps = mContext.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val appInfo = mContext.applicationInfo
            val pkg = mContext.applicationContext.packageName
            val uid = appInfo.uid
            try {
                val appOpsClass = Class.forName(AppOpsManager::class.java.name)
                val checkOpNoThrowMethod = appOpsClass.getMethod(
                    CHECK_OP_NO_THROW, Integer.TYPE,
                    Integer.TYPE, String::class.java
                )
                val opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION)
                val value = opPostNotificationValue[Int::class.java] as Int
                (checkOpNoThrowMethod.invoke(appOps, value, uid, pkg) as Int
                        == AppOpsManager.MODE_ALLOWED)
            } catch (e: ClassNotFoundException) {
                true
            } catch (e: NoSuchMethodException) {
                true
            } catch (e: NoSuchFieldException) {
                true
            } catch (e: InvocationTargetException) {
                true
            } catch (e: IllegalAccessException) {
                true
            } catch (e: RuntimeException) {
                true
            }
        } else {
            true
        }
    }
}
