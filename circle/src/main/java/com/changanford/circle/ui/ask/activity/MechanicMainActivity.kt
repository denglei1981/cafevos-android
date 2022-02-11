package com.changanford.circle.ui.ask.activity


import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.databinding.ActivityMechainicMainBinding
import com.changanford.circle.interf.UploadPicCallback
import com.changanford.circle.ui.ask.request.MechanicMainViewModel
import com.changanford.circle.ui.ask.request.QuestionViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.DialogBottomBean
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.ui.dialog.LoadDialog
import com.changanford.common.util.MineUtils
import com.changanford.common.util.PictureUtil
import com.changanford.common.util.PictureUtils
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.common.widget.SelectDialog
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener


// 技师个人主页。
@Route(path = ARouterCirclePath.MechanicMainActivity)
class MechanicMainActivity : BaseActivity<ActivityMechainicMainBinding, MechanicMainViewModel>() {
    override fun initView() {
        StatusBarUtil.setStatusBarMarginTop(binding.layoutTitle.conTitle, this)
        binding.layoutTitle.tvTitle.text = "个人信息"
        binding.layoutTitle.barTvOther.text = "保存"
        binding.layoutTitle.barTvOther.visibility = View.VISIBLE

        binding.ivHeader.setOnClickListener {
            selectIcon()

        }

    }

    override fun initData() {

        dialog = LoadDialog(this@MechanicMainActivity)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setLoadingText("图片上传中..")
        val technicianId = intent.getStringExtra("value")
        if (technicianId != null) {
            viewModel.getTechniciaPersonalInfo(technicianId)
        }
    }

    /**
     * 点击头像
     */
    private fun selectIcon() {
        SelectDialog(
            this,
            R.style.transparentFrameWindowStyle,
            MineUtils.listPhoto,
            "",
            1,
            SelectDialog.SelectDialogListener() { view: View, i: Int, dialogBottomBean: DialogBottomBean ->
                when (i) {
                    0 -> takePhoto()
                    1 -> pic()
                }
            }
        ).show()
    }

    /**
     * 拍照
     */
    private fun takePhoto() {
        PictureUtils.opencarcme(
            this@MechanicMainActivity,
            object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: List<LocalMedia>) {
                    // 结果回调
                    if (result.isNotEmpty()) {
                        for (media in result) {
                            val path: String = PictureUtil.getFinallyPath(media)
//                        loadCircleFilePath(path, binding.editIcon)
                            headIconPath = path
                            saveHeadIcon()
                        }
                    }
                }

                override fun onCancel() {
                    // 取消
                }
            })
    }

    /**
     * 选择图片
     */
    private fun pic() {
        PictureUtils.openGarlly(this@MechanicMainActivity, object :
            OnResultCallbackListener<LocalMedia> {
            override fun onResult(result: List<LocalMedia>) {
                for (media in result) {
                    var path: String? = ""
                    path = PictureUtil.getFinallyPath(media)
//                    loadCircleFilePath(path, binding.editIcon)
                    headIconPath = path
                    saveHeadIcon()
                }
            }

            override fun onCancel() {}
        })
    }

    var headIconPath: String = ""//头像地址
    var headIconUrl: String = ""//头像Http地址
    lateinit var dialog: LoadDialog
    private fun saveHeadIcon() {
        //保存
        if (headIconPath.isNotEmpty()) {
            dialog.show()
            viewModel.uploadFile(this, arrayListOf(headIconPath), object : UploadPicCallback {
                override fun onUploadSuccess(files: ArrayList<String>) {
                    println(files)
                    dialog.dismiss()
                    if (files.size > 0) headIconUrl = files[0]
                    val map = HashMap<String, String>()
                    map["avatar"] = headIconUrl
                    saveUserInfo(true, map)
                }

                override fun onUploadFailed(errCode: String) {
                    dialog.dismiss()
                }

                override fun onuploadFileprogress(progress: Long) {
                }
            })
        }
    }

    // TODO
    fun saveUserInfo(isShowDialog: Boolean, map: HashMap<String, String>) {


    }


}