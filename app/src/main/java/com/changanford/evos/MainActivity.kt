package com.changanford.evos

import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.BaseApplication

import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.ui.dialog.UpdateAlertDialog
import com.changanford.common.ui.dialog.UpdatingAlertDialog
import com.changanford.common.util.*
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.bus.LiveDataBusKey.BUS_HIDE_BOTTOM_TAB
import com.changanford.common.util.bus.LiveDataBusKey.LIVE_OPEN_TWO_LEVEL
import com.changanford.common.util.location.LocationUtils
import com.changanford.common.util.permission.PermissionUtil
import com.changanford.common.util.room.Db
import com.changanford.common.utilext.logD
import com.changanford.common.utilext.toastShow
import com.changanford.common.viewmodel.UpdateViewModel
import com.changanford.evos.databinding.ActivityMainBinding
import com.changanford.evos.utils.BottomNavigationUtils
import com.changanford.evos.utils.CustomNavigator
import com.changanford.evos.view.SpecialAnimaTab
import com.luck.picture.lib.tools.ToastUtils
import kotlinx.coroutines.launch
import me.majiajie.pagerbottomtabstrip.NavigationController
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem

@Route(path = ARouterHomePath.MainActivity)
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    private lateinit var updateViewModel: UpdateViewModel
    lateinit var navController: NavController

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false

    }

    private fun initBottomNavigation() {
        val navigationController: NavigationController = binding.homeBottomNavi.custom()
            .addItem(
                newItem(
                    R.mipmap.icon_homeu,
                    R.mipmap.icon_home_b,
                    R.mipmap.icon_home_c,
                    "发现",
                    -5f
                )
            )
            .addItem(
                newItem(
                    R.mipmap.icon_circleu,
                    R.mipmap.icon_circle_b,
                    R.mipmap.icon_circle_c,
                    "社区",
                    -7f
                )
            )
            .addItem(
                newItem(
                    R.mipmap.icon_caru,
                    R.mipmap.icon_car_b,
                    R.mipmap.icon_car_c,
                    "爱车",
                    -20f
                )
            )
            .addItem(
                newItem(
                    R.mipmap.icon_shopu,
                    R.mipmap.icon_shop_b,
                    R.mipmap.icon_shop_c,
                    "商城",
                    -3f
                )
            )
            .addItem(
                newItem(
                    R.mipmap.icon_myu,
                    R.mipmap.icon_my_b,
                    R.mipmap.icon_my_c,
                    "我的",
                    8f,
                    -10f
                )
            )
            .build()
        BottomNavigationUtils.setupWithNavController(PAGE_IDS, navigationController, navController)
    }

    override fun initView() {
//        StatusBarUtil.setTranslucentForImageViewInFragment(this@MainActivity, null)
        if (SPUtils.getParam(this, "isPopAgreement", true) as Boolean) {
            showAppPrivacy(this) {
                checkPermission()
            }
        }

        getNavigator()
        initBottomNavigation()
//        LiveDataBus.get().with(BUS_HIDE_BOTTOM_TAB).observe(this, {
//            if (it as Boolean) {
//            }
//        })
    }

    private fun getNavigator() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)!!
        val navigator = CustomNavigator(
            this,
            navHostFragment.childFragmentManager,
            R.id.nav_host_fragment_content_main
        )// 生成自定义Navigator对象
        navController.navigatorProvider.addNavigator("custom_fragment", navigator) // 添加 key, value
        navController.setGraph(R.navigation.nav_graph)  // 要在 CustomNavigator 类被加载之后添加graph，不然找不到 custom_fragment节点
    }

    override fun initData() {
        viewModel.getUserData()
        viewModel.user.observe(this, Observer {
            lifecycleScope.launch {
                Db.myDb.saveData("name", it[0].name)
            }
        })
        updateViewModel = createViewModel(UpdateViewModel::class.java)
//        updateViewModel.getUpdateInfo()
        updateViewModel._updateInfo.observe(this, { info ->
            info?.let {
                if (info.versionNumber?.toInt() ?: 0 <= DeviceUtils.getVersionCode(this)) {
                    Log.e("---------->", info.versionNumber ?: "")
                    Log.e("---------->", DeviceUtils.getVersionCode(this).toString())
                    return@observe
                }
                var dialog = UpdateAlertDialog(this)
                dialog.builder().setPositiveButton("立即更新") {
                    toastShow("正在下载")
                    if (!MConstant.isDownloading) {
                        var updatingAlertDialog = UpdatingAlertDialog(this)
                        var apkDownload = APKDownload()
                        updatingAlertDialog.builder().setPositiveButton("取消下载") {
                            apkDownload.cancel()
                            if (info.isForceUpdate == 1) {
                                finish()
                            } else {
                                updatingAlertDialog.dismiss()
                            }
                        }.setTitle("新版本正在更新，请稍等").setCancelable(info.isForceUpdate != 1).show()
                        apkDownload.download(info.downloadUrl ?: "", object : DownloadProgress {
                            override fun sendProgress(progress: Int) {
                                updatingAlertDialog.updateProgress(progress)
                                if (progress == 100) {
                                    updatingAlertDialog.setPositiveButton("下载完成") {
                                        apkDownload.installAPK()
                                        if (info.isForceUpdate == 1) {
                                        } else {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//解决因为去点击权限后返回弹框消失的问题
                                                if (BaseApplication.INSTANT.packageManager.canRequestPackageInstalls()) {
                                                    updatingAlertDialog.dismiss()
                                                }
                                            } else {
                                                updatingAlertDialog.dismiss()
                                            }
                                        }
                                    }
                                }
                            }

                        })
                    }
                    if (info.isForceUpdate == 0) {
                        dialog.dismiss()
                    }
                }.setNegativeButton("暂不更新") {
                    if (info.isForceUpdate == 1) {
                        finish()
                    }
                    dialog.dismiss()

                }.setTitle(info.versionName ?: "更新")
                    .setMsg(info.versionContent ?: "体验全新功能")
                    .setCancelable(false).show()
                info.downloadUrl?.let {
                    MConstant.newApk = true
                    MConstant.newApkUrl = it
                }
            }
        })
    }

    private lateinit var currentNavController: LiveData<NavController>

    /**
     * 连点退出
     */
    private var exitTime: Long = 0
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                ToastUtils.s(BaseApplication.INSTANT, "再按一次退出引力域")
                exitTime = System.currentTimeMillis()
            } else {
                var intent = Intent()
                intent.action = Intent.ACTION_MAIN;
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK; //如果是服务里调用，必须加入new task标识
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }
        }

        return false
    }

    /**
     * 正常tab
     */
    private fun newItem(
        drawable: Int,
        checkedDrawable: Int,
        yuanshu: Int,
        text: String,
        xfloat: Float = 0f,
        yfloat: Float = 0f
    ): BaseTabItem? {
        val mainTab = SpecialAnimaTab(this)
        if (text != "我的") {
            mainTab.setmsgGone()
        }
        mainTab.setTextDefaultColor(resources.getColor(R.color.tab_nomarl))
        mainTab.setTextCheckedColor(resources.getColor(R.color.black))
        mainTab.setIvyuanshu(yuanshu)
        mainTab.setYfloat(yfloat)
        mainTab.setXfloat(xfloat)
        mainTab.initialize(drawable, checkedDrawable, text)
        return mainTab
    }

    private var PAGE_IDS = intArrayOf(
        R.id.homeFragment,
        R.id.circleFragment,
        R.id.carFragment,
        R.id.shopFragment,
        R.id.myFragment
    )

    private fun checkPermission() {
        //请求应用需要的所有权限
        val checkPermissionFirst: Boolean = PermissionUtil.run {
            ALBUM_READ && ALBUM_WRITE && CAMERA && LOCATION
        }
        if (!checkPermissionFirst) {
            PermissionUtil.applyPermissions(this)
        }
        LiveDataBus.get().withs<Boolean>(CircleLiveBusKey.LOCATION_RESULT).observe(this, {
            LocationUtils.init()
        })
        LocationUtils.mLongitude.observe(this, {
            "$it".logD()
        })
    }

    // 设置底部导航显示或者隐藏
    private fun setHomBottomNavi(visibleState: Int) {
        binding.homeBottomNavi.visibility = visibleState
    }

    override fun observe() {
        super.observe()
        LiveDataBus.get().with(LIVE_OPEN_TWO_LEVEL, Boolean::class.java).observe(this, Observer {
            if (it) {
                setHomBottomNavi(View.GONE)
            } else {
                setHomBottomNavi(View.VISIBLE)
            }
        })
        LiveDataBus.get().with(LiveDataBusKey.COOKIE_DB, Boolean::class.java).observe(this, {
            if (it) {
                lifecycleScope.launch {
                    MConstant.pubKey = Db.myDb.getData("pubKey")?.storeValue ?: ""
                    MConstant.imgcdn = Db.myDb.getData("imgCdn")?.storeValue ?: ""
                }
            }
        })
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleViewIntent(it) }
    }

    /**
     * 处理外部浏览
     */
    private fun handleViewIntent(intent: Intent) {
        if (Intent.ACTION_VIEW == intent.action) {
            val uri = intent.data
            if (uri != null) {
                try {
                    val type = uri.getQueryParameter("jumpDataType")!!.toInt()
                    val value = uri.getQueryParameter("jumpDataValue")
                    JumpUtils.instans!!.jump(type, value)
                } catch (e: Exception) {
                    e.printStackTrace()
                    JumpUtils.instans!!.jump(0, "")
                }
            }
        } else {
            intent.extras?.let {
                var jumpValue = it.getInt("jumpValue")
                if (jumpValue > 0)
                    when (jumpValue) {
                        1 -> navController?.navigate(R.id.homeFragment)
                        2 -> navController?.navigate(R.id.circleFragment)
                        3 -> navController?.navigate(R.id.carFragment)
                        4 -> navController?.navigate(R.id.shopFragment)
                        5 -> navController?.navigate(R.id.myFragment)
                    }
                try {
                    val jumpDataType = it.getString("jumpDataType")?.toInt()
                    val jumpDataValue = it.getString("jumpDataValue")
                    JumpUtils.instans!!.jump(
                        Integer.valueOf(jumpDataType ?: 99),
                        jumpDataValue
                    )
                } catch (e: Exception) {
                }
            }
        }
    }
}

