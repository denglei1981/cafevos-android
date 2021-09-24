package com.changanford.evos

import android.os.Build
import android.util.Log
import android.view.Menu
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.BaseApplication
import com.changanford.common.ui.dialog.UpdateAlertDialog
import com.changanford.common.ui.dialog.UpdatingAlertDialog
import com.changanford.common.util.APKDownload
import com.changanford.common.util.DeviceUtils
import com.changanford.common.util.DownloadProgress
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey.BUS_HIDE_BOTTOM_TAB
import com.changanford.common.util.room.Db
import com.changanford.common.utilext.toastShow
import com.changanford.common.viewmodel.UpdateViewModel
import com.changanford.evos.databinding.ActivityMainBinding
import com.changanford.evos.utils.BottomNavigationUtils
import com.changanford.evos.view.SpecialAnimaTab
import kotlinx.coroutines.launch
import me.majiajie.pagerbottomtabstrip.NavigationController
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var updateViewModel:UpdateViewModel
    lateinit var navController:NavController

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onSupportNavigateUp(): Boolean {
        //旧
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
        //新
        return currentNavController?.value?.navigateUp() ?: false

    }
    private fun initBottomNavigation() {
        val navigationController: NavigationController = binding.homeBottomNavi.custom()
            .addItem(newItem(R.mipmap.icon_homeu, R.mipmap.icon_home_b, R.mipmap.icon_home_c, "发现", -5f))
            .addItem(newItem(R.mipmap.icon_circleu, R.mipmap.icon_circle_b, R.mipmap.icon_circle_c, "社区", -7f))
            .addItem(newItem(R.mipmap.icon_caru, R.mipmap.icon_car_b, R.mipmap.icon_car_c, "订车", -30f))
            .addItem(newItem(R.mipmap.icon_shopu, R.mipmap.icon_shop_b, R.mipmap.icon_shop_c, "商城", -3f))
            .addItem(newItem(R.mipmap.icon_myu, R.mipmap.icon_my_b, R.mipmap.icon_my_c, "我的", 8f, -10f))
            .build()
        BottomNavigationUtils.setupWithNavController(PAGE_IDS, navigationController, navController)
    }

    override fun initView() {

        setSupportActionBar(binding.toolbar)
        //旧
        navController = findNavController(R.id.nav_host_fragment_content_main)
        initBottomNavigation()
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        binding.homeBottomNavi.setupWithNavController(navController)
//        binding.homeBottomNavi.setOnNavigationItemReselectedListener {
//            "do nothing".toast()
//        }
        //新
        setupBottomNavigationBar()


        LiveDataBus.get().with(BUS_HIDE_BOTTOM_TAB).observe(this, {
            if (it as Boolean) {
//                val badge = binding.homeBottomNavi.getOrCreateBadge(R.id.homeFragment)
//                badge.isVisible = true
//                badge.number += 1
//                val badge2 = binding.homeBottomNavi.getOrCreateBadge(R.id.circleFragment)
//                badge2.isVisible = true
            }
        })

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
        updateViewModel._updateInfo.observe(this,{info ->
            info?.let {
                if (info.versionNumber?.toInt() ?: 0 <= DeviceUtils.getVersionCode(this)) {
                    Log.e("---------->", info.versionNumber?:"")
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

    private fun setupBottomNavigationBar() {
        /*val navGraphIds = listOf(
            R.navigation.nav1,
            R.navigation.nav2,
            R.navigation.nav3,
            R.navigation.nav4,
            R.navigation.nav5
        )
        val controller = binding.homeBottomNavi.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_fragment_content_main,
            intent = intent
        )
        controller.observe(this, { navController ->
            val appBarConfiguration = AppBarConfiguration(navGraphIds.toSet())
            NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration)
            setSupportActionBar(binding.toolbar)
        })
        currentNavController = controller
        binding.homeBottomNavi.itemIconTintList = null*/
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
}

