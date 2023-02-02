package com.changanford.evos

import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.compose.DesignElements.map
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.CircleFragmentV2
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.BaseApplication
import com.changanford.common.buried.BuriedUtil
import com.changanford.common.constant.HawkKey
import com.changanford.common.manger.UserManger
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.ui.dialog.UpdateAlertDialog
import com.changanford.common.ui.dialog.UpdatingAlertDialog
import com.changanford.common.util.*
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.bus.LiveDataBusKey.LIVE_OPEN_TWO_LEVEL
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.util.room.Db
import com.changanford.common.util.room.UserDatabase
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.common.utilext.toast
import com.changanford.common.utilext.toastShow
import com.changanford.common.viewmodel.UpdateViewModel
import com.changanford.common.widget.pop.HomeGuidePop
import com.changanford.evos.databinding.ActivityMainBinding
import com.changanford.evos.utils.BottomNavigationUtils
import com.changanford.evos.utils.CustomNavigator
import com.changanford.evos.utils.NetworkStateReceiver
import com.changanford.evos.view.SpecialAnimaTab
import com.changanford.home.HomeV2Fragment
import com.changanford.shop.ShopFragment
import com.growingio.android.sdk.autotrack.GrowingAutotracker
import com.luck.picture.lib.tools.ToastUtils
import com.orhanobut.hawk.Hawk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.majiajie.pagerbottomtabstrip.NavigationController
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem
import razerdp.basepopup.BasePopupWindow


@Route(path = ARouterHomePath.MainActivity)
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    private lateinit var updateViewModel: UpdateViewModel
    lateinit var navController: NavController

    var jumpIndex: String = ""

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
                    12f
                )
            )
            .addItem(
                newItem(
                    R.mipmap.icon_circleu,
                    R.mipmap.icon_circle_b,
                    R.mipmap.icon_circle_c,
                    "社区",
                    12f
                )
            )
            .addItem(
                newItem(
                    R.mipmap.icon_caru,
                    R.mipmap.icon_car_b,
                    R.mipmap.icon_car_c,
                    "爱车",
                    1f
                )
            )
//        R.mipmap.icon_car_b,
//        R.mipmap.icon_car_c,
            .addItem(
                newItem(
                    R.mipmap.icon_shopu,
                    R.mipmap.icon_shop_b,
                    R.mipmap.icon_shop_c,
                    "商城",
                    13f
                )
            )
            .addItem(
                newItem(
                    R.mipmap.icon_myu,
                    R.mipmap.icon_my_b,
                    R.mipmap.icon_my_c,
                    "我的",
                    18f,
                    -10f
                )
            )
            .build()
        BottomNavigationUtils.setupWithNavController(PAGE_IDS, navigationController, navController)


        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.carFragment -> {
                    GioPageConstant.mainTabName = "爱车页"
                    // 埋点
                    StatusBarUtil.setStatusBarColor(this, R.color.transparent)
                    if (!isJumpMenu) {
                        BuriedUtil.instant?.mainButtomMenu("爱车")
                    }
                    isJumpMenu = false
                }
                R.id.myFragment -> {
                    GioPageConstant.mainTabName = "我的页"
                    // 埋点
                    StatusBarUtil.setStatusBarColor(this, R.color.transparent)
                    if (!isJumpMenu) {
                        BuriedUtil.instant?.mainButtomMenu("我的")
                    }
                    isJumpMenu = false
                }
                R.id.circleFragment -> {// 社区
                    GioPageConstant.mainTabName = "社区页"
                    // 埋点
                    StatusBarUtil.setStatusBarColor(this, R.color.white)
                    if (!isJumpMenu) {
                        BuriedUtil.instant?.mainButtomMenu("社区")
                    }
                    isJumpMenu = false
                    val circleFragmentV2 = getFragment(CircleFragmentV2::class.java)
                    circleFragmentV2?.let { it ->
                        val circleFragment = it as CircleFragmentV2
                        if (!TextUtils.isEmpty(jumpIndex)) {
                            circleFragment.setCurrentItem(jumpIndex)
                            jumpIndex = ""
                        }
                    }
                }

                R.id.shopFragment -> {
                    GioPageConstant.mainTabName = "商城页"
                    // 埋点
                    StatusBarUtil.setStatusBarColor(this, R.color.transparent)
                    if (!isJumpMenu) {
                        BuriedUtil.instant?.mainButtomMenu("商城")
                    }
                    isJumpMenu = false
                    getFragment(ShopFragment::class.java)?.let { it ->
                        val shopFragment = it as ShopFragment
                        if (!TextUtils.isEmpty(jumpIndex)) {
                            //jumpIndex 为tagName
                            shopFragment.setCurrentItem(jumpIndex)
                            jumpIndex = ""
                        }
                    }
                }
                R.id.homeFragment -> {
                    GioPageConstant.mainTabName = "发现页"
                    // 埋点
                    val currentFragment = getFragment(HomeV2Fragment::class.java)
                    currentFragment?.let { it ->
                        val homeV2Fragment = it as HomeV2Fragment
                        if (!TextUtils.isEmpty(jumpIndex)) {
                            homeV2Fragment.setCurrentItem(jumpIndex)
                            jumpIndex = ""
                        }
                    }
                    if (!isJumpMenu) {
                        BuriedUtil.instant?.mainButtomMenu("发现")
                    }
                    isJumpMenu = false
                }
                else -> {
                    StatusBarUtil.setStatusBarColor(this, R.color.white)
                }
            }
            StatusBarUtil.setLightStatusBar(this, destination.id != R.id.carFragment)
            GIOUtils.homePageView()
        }
    }

    override fun initView() {
        if (MConstant.app_mourning_mode == 1) {
            BlackWhiteMode(window = window)
        }
        getDeviceWidth()
//        StatusBarUtil.setTranslucentForImageViewInFragment(this@MainActivity, null)
        updateViewModel = createViewModel(UpdateViewModel::class.java)
        updateViewModel.getUpdateInfo()
        viewModel.requestDownLogin()

        getNavigator()
        initBottomNavigation()
//        if (Hawk.get(HawkKey.GUIDE_HOME,false) == false) {
//            HomeGuidePop(this).run {
//                //无透明背景
//                setBackgroundColor(Color.TRANSPARENT)
//                //背景模糊false
//                setBlurBackgroundEnable(false)
//                showPopupWindow(binding.homeBottomNavi)
//                onDismissListener = object : BasePopupWindow.OnDismissListener() {
//                    override fun onDismiss() {
//                        Hawk.put(HawkKey.GUIDE_HOME, true)
//                    }
//
//                }
//
//            }
//        }

//        LiveDataBus.get().with(BUS_HIDE_BOTTOM_TAB).observe(this, {
//            if (it as Boolean) {
//            }
//        })
        GIOUtils.homePageView()
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
        handleViewIntent(intent)
        viewModel.getUserData()
        if (MConstant.token.isNotEmpty()) {
            viewModel.getUserInfo()
        }
        viewModel.user.observe(this, Observer {
            lifecycleScope.launch {
                Db.myDb.saveData("name", it[0].name)
            }
        })
//        var count = 0
        updateViewModel._updateInfo?.observe(this, Observer { info ->
            if (info == null) {
//                "${count++}".toast()
                lifecycleScope.launch(Dispatchers.IO) {
                    delay(3000)
                    updateViewModel.getUpdateInfo()
                }
                return@Observer
            }
            info?.let {
                if (info.versionNumber?.toInt() ?: 0 <= DeviceUtils.getVersionCode(this)) {
                    return@Observer
                }
                var dialog = UpdateAlertDialog(this)
                dialog.builder().setPositiveButton("立即更新") {
                    toastShow("正在下载")
                    if (!MConstant.isDownloading) {
                        val updatingAlertDialog = UpdatingAlertDialog(this)
                        val apkDownload = APKDownload()
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
        LiveDataBus.get().with(LiveDataBusKey.HOME_UPDATE).observe(this) {
            updateViewModel.getUpdateInfo()
        }
        registerConnChange()
        viewModel.getQuestionTagInfo()
    }

    private lateinit var currentNavController: LiveData<NavController>

    /**
     * 连点退出
     */
    private var exitTime: Long = 0
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                ToastUtils.s(BaseApplication.INSTANT, "再按一次退出福域")
                exitTime = System.currentTimeMillis()
            } else {
                val intent = Intent()
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
        LiveDataBus.get()
            .with(LiveDataBusKey.USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this) {
                if (UserManger.UserLoginStatus.USER_LOGIN_SUCCESS == it) {
                    viewModel.getUserInfo()
                } else if (UserManger.UserLoginStatus.USER_LOGIN_OUT == it) {
                    initGioUserId()
                }
            }
        LiveDataBus.get().with(MConstant.REFRESH_USER_INFO, Boolean::class.java).observe(this) {
            if (it) {
                viewModel.getUserInfo()
            }
        }
        LiveDataBus.get().with(LiveDataBusKey.COOKIE_DB, Boolean::class.java)
            .observe(this, Observer {
                if (it) {
                    lifecycleScope.launch {
//                        MConstant.pubKey = Db.myDb.getData("pubKey")?.storeValue ?: ""
//                        MConstant.imgcdn = Db.myDb.getData("imgCdn")?.storeValue ?: ""
                        Db.myDb.getData("pubKey")?.storeValue?.apply {
                            MConstant.pubKey = this
                        }
                        Db.myDb.getData("imgCdn")?.storeValue?.apply {
                            MConstant.imgcdn =
                                if (TextUtils.isEmpty(this)) MConstant.defaultImgCdn else this
                        }
                    }
                }
            })
        LiveDataBus.get().with(LiveDataBusKey.SHOULD_SHOW_MY_MSG_DOT).observe(this, Observer {
            if (it as Boolean) {//true 显示
                ((binding.homeBottomNavi.getChildAt(0) as ViewGroup).getChildAt(4) as SpecialAnimaTab).setmsgVisible()
            } else {
                ((binding.homeBottomNavi.getChildAt(0) as ViewGroup).getChildAt(4) as SpecialAnimaTab).setmsgGone()
            }
        })
        viewModel.userInfo.observe(this) {
            initGioUserId()
            GIOUtils.setLoginUserAttributes(it)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleViewIntent(it) }
    }

    /**
     * 处理外部浏览
     */
    var isJumpMenu: Boolean = false // 是否要点击 的埋点标志。
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
                val jumpValue = it.getInt("jumpValue")
                try {
                    jumpIndex = it.getString("value", "")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                isJumpMenu = true
                if (jumpValue > 0)
                    when (jumpValue) {
                        1 -> {
                            navController.navigate(R.id.homeFragment)
                        }
                        2 -> {
                            setHomBottomNavi(View.VISIBLE)
                            StatusBarUtil.setStatusBarColor(this, R.color.white)
                            navController.navigate(R.id.circleFragment)
                        }
                        3 -> {
                            setHomBottomNavi(View.VISIBLE)
                            navController.navigate(R.id.carFragment)
                            StatusBarUtil.setStatusBarColor(this, R.color.transparent)

                        }
                        4 -> {
                            setHomBottomNavi(View.VISIBLE)
                            StatusBarUtil.setStatusBarColor(this, R.color.transparent)
                            navController.navigate(R.id.shopFragment)
                        }
                        5 -> {
                            setHomBottomNavi(View.VISIBLE)
                            StatusBarUtil.setStatusBarColor(this, R.color.transparent)
                            navController.navigate(R.id.myFragment)
                        }
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

    override fun onDestroy() {
        super.onDestroy()
        unRegisterConnChange()
    }

    var networkStateReceiver = NetworkStateReceiver()
    private fun registerConnChange() {
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkStateReceiver, intentFilter)
    }

    private fun unRegisterConnChange() {
        unregisterReceiver(networkStateReceiver)
    }

    // 获取当前fragment。
    private fun getFragment(clazz: Class<*>?): Fragment? {
        val fragments = supportFragmentManager.fragments
        if (fragments != null && fragments.size > 0) {
            val navHostFragment = fragments[0] as NavHostFragment
            val childfragments = navHostFragment.childFragmentManager.fragments
            if (childfragments != null && childfragments.size > 0) {
                for (j in childfragments.indices) {
                    val fragment = childfragments[j]
                    if (fragment.javaClass.isAssignableFrom(clazz)) {
//                        Log.i(
//                            "evis",
//                            "getFragment1: $fragment"
//                        )
                        return fragment
                    }
                }
            }
        }
        return null
    }

    private fun initGioUserId() {
        if (MConstant.userId.isEmpty()) {
            GrowingAutotracker.get().cleanLoginUserId()
        } else {
            if (MConstant.userId.isNotEmpty()) {
                GrowingAutotracker.get().setLoginUserId(viewModel.userInfo.value?.cmcOpenid)
            }
        }
    }

    private fun getDeviceWidth() {
        runOnUiThread {
            MConstant.deviceWidth = DisplayUtil.getScreenWidth(this)
        }
    }
}

