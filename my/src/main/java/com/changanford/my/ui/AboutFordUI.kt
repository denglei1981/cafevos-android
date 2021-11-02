package com.changanford.my.ui

import android.os.Bundle
import androidx.core.view.isVisible
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.ui.dialog.UpdatingAlertDialog
import com.changanford.common.util.*
import com.changanford.common.utilext.toast
import com.changanford.common.utilext.toastShow
import com.changanford.common.viewmodel.UpdateViewModel
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.UiAboutBinding

/**
 *  文件名：AboutFordUI
 *  创建者: zcy
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.AboutUI)
class AboutFordUI : BaseMineUI<UiAboutBinding, EmptyViewModel>() {


    override fun initView() {
        binding.mineToolbar.toolbarTitle.text = "关于"
        binding.mineToolbar.toolbar.setNavigationOnClickListener {
            back()
        }

        binding.title4.setOnClickListener {
            JumpUtils.instans?.jump(1, MConstant.H5_USER_AGREEMENT)
        }

        binding.title3.setOnClickListener {
            JumpUtils.instans?.jump(1, MConstant.H5_REGISTER_AGREEMENT)
        }
        binding.version.text = "版本号 ${DeviceUtils.getversionName()}"
        binding.include.update.setOnClickListener {
            if (MConstant.newApk) {
                toastShow("正在下载")
                if (!MConstant.isDownloading) {
                    var updatingAlertDialog = UpdatingAlertDialog(this)
                    var apkDownload = APKDownload()
                    updatingAlertDialog.builder().setPositiveButton("取消下载") {
                        apkDownload.cancel()
                        updatingAlertDialog.dismiss()
                    }.setTitle("新版本正在更新，请稍等").setCancelable(true).show()
                    apkDownload.download(MConstant.newApkUrl ?: "", object : DownloadProgress {
                        override fun sendProgress(progress: Int) {
                            updatingAlertDialog.updateProgress(progress)
                            if (progress == 100) {
                                updatingAlertDialog.setPositiveButton("下载完成") {
                                    apkDownload.installAPK()
                                    updatingAlertDialog.dismiss()
                                }
                            }
                        }
                    })

                }
            }else{
                "没有更新".toast()
            }
        }
        if (MConstant.newApk) {
            binding.include.messageStatus.isVisible = true
        }
    }
    private lateinit var updateViewModel: UpdateViewModel

    override fun initData() {
        updateViewModel = createViewModel(UpdateViewModel::class.java)
        updateViewModel._updateInfo.observe(this, { info ->
            info?.downloadUrl?.let {
                binding.include.messageStatus.isVisible = true
                MConstant.newApk = true
                MConstant.newApkUrl = it
            }
        })
        updateViewModel.getUpdateInfo()
    }

}