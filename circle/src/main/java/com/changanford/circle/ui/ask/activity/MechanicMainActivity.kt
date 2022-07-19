package com.changanford.circle.ui.ask.activity


import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.changanford.circle.R
import com.changanford.circle.databinding.ActivityMechainicMainBinding
import com.changanford.circle.interf.UploadPicCallback
import com.changanford.circle.ui.ask.pop.AnswerGoodAtDialog
import com.changanford.circle.ui.ask.pop.CircleAskScreenDialog
import com.changanford.circle.ui.ask.request.MechanicMainViewModel
import com.changanford.circle.ui.ask.request.QuestionViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.DialogBottomBean
import com.changanford.common.bean.QuestionData
import com.changanford.common.bean.ResultData
import com.changanford.common.bean.TechnicianData
import com.changanford.common.listener.AskCallback
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.ui.dialog.LoadDialog
import com.changanford.common.util.MineUtils
import com.changanford.common.util.PictureUtil
import com.changanford.common.util.PictureUtils
import com.changanford.common.utilext.*
import com.changanford.common.widget.SelectDialog
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import java.io.File


// 技师个人主页。
@Route(path = ARouterCirclePath.MechanicMainActivity)
class MechanicMainActivity : BaseActivity<ActivityMechainicMainBinding, MechanicMainViewModel>() {

    var circleAskScreenDialog: AnswerGoodAtDialog? = null


    override fun initView() {
        StatusBarUtil.setStatusBarMarginTop(binding.layoutTitle.conTitle, this)
        binding.layoutTitle.tvTitle.text = "个人信息"
        binding.layoutTitle.barTvOther.text = "保存"
        binding.layoutTitle.barTvOther.visibility = View.VISIBLE
        binding.layoutTitle.barTvOther.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.color_00095B
            )
        )
        binding.layoutTitle.vLine.background=ContextCompat.getDrawable(this,R.color.transparent)

//        binding.ivHeader.setOnClickListener {
//            selectIcon()
//        }
        binding.layoutTitle.ivBack.setOnClickListener {
            onBackPressed()
        }


        binding.layoutGoodAt.tvRememberTime.setOnClickListener {
            showScreenDialog()
        }

        binding.layoutTitle.barTvOther.setOnClickListener {
            save()

        }
    }

    private var params = hashMapOf<String, Any>()
    private fun save() {
        // 保存

        val info = binding.etInfo.text.toString().trim()

        val nickName = binding.layoutNickName.tvRememberTime.text.toString().trim()

        val goodAtStr=binding.layoutGoodAt.tvRememberTime.text

        if (TextUtils.isEmpty(info)) {
            "请填写个人资料".toast()
            return
        }
        if(TextUtils.isEmpty(nickName)){
            "请填写昵称".toast()
            return
        }
        if(TextUtils.isEmpty(goodAtStr)){
            "请选择擅长类型".toast()
            return
        }

        params["nickName"]=nickName
        params["introduction"]= info
        if(!TextUtils.isEmpty(headIconUrl)){
            params["avater"]=headIconUrl
        }



        viewModel.upTechniciaInfo(false, params)
    }

    var technicianId = ""
    override fun initData() {

        dialog = LoadDialog(this@MechanicMainActivity)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setLoadingText("图片上传中..")
        technicianId = intent.getStringExtra("value").toString()
        if (technicianId.isNotEmpty()) {
            viewModel.getQuestionType()
//            viewModel.getTechniciaPersonalInfo(technicianId)

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
                    headIconUrl.toString().logE()
//                    GlideUtils.loadBD(headIconPath,binding.ivHeader)
                    headIconPath.logE()
                    runOnUiThread {
//                        GlideUtils.loadBD(headIconPath,binding.ivHeader)
                        Glide.with(this@MechanicMainActivity).load( File(headIconPath)).into(binding.ivHeader)
                    }
//                    Glide.with(this@MechanicMainActivity).load( File(headIconPath)).into(binding.ivHeader)
                }
                override fun onUploadFailed(errCode: String) {
                    dialog.dismiss()
                }
                override fun onuploadFileprogress(progress: Long) {
                }
            })
        }
    }


    var questionTypeList = arrayListOf<QuestionData>()
    var technicianData: TechnicianData? = null
    override fun observe() {
        super.observe()
        viewModel.technicianLiveData.observe(this, Observer {
            showInfo(it)
        })
        viewModel.questTypeList.observe(this, Observer {

            questionTypeList = it
            if (technicianId.isNotEmpty()) {
                viewModel.getTechniciaPersonalInfo(technicianId)
            }
        })
        viewModel.changTechLiveData.observe(this, Observer {
             this.finish()
        })
    }

    private fun showInfo(technicianData: TechnicianData) {
        this.technicianData = technicianData
        GlideUtils.loadBD(technicianData.avater, binding.ivHeader)
        binding.etInfo.setText(technicianData.introduction)
        binding.layoutNickName.tvRememberTime.setText(technicianData.nickName)
        var goodAtStri = ""
        params["questionTypeCodes"] = technicianData.questionTypeCodes
        params["avater"]=technicianData.avater
        technicianData.questionTypeCodes.forEach { td ->
            questionTypeList.forEach { qd ->
                if (qd.dictValue == td) {
                    goodAtStri = goodAtStri.plus(qd.dictLabel).plus("/")
                }
            }
        }
        if (technicianData.questionTypeCodes.isNotEmpty()) {//去掉最后一个/
            val take = goodAtStri.take(goodAtStri.length - 1)
            binding.layoutGoodAt.tvRememberTime.text = take
        } else {
            binding.layoutGoodAt.tvRememberTime.text = "请选择擅长类型"
        }
    }

    var questionTypes = mutableListOf<String>()
    fun showScreenDialog() {
        if (circleAskScreenDialog == null) {
            circleAskScreenDialog = AnswerGoodAtDialog(this, this, object :
                AskCallback {
                override fun onResult(result: ResultData) {
                    when (result.resultCode) {
                        ResultData.OK -> {
                            val questionData = result.data as List<QuestionData>
                            questionTypes.clear()
                            var goodAdStr = ""
                            questionData.forEach {
                                questionTypes.add(it.dictValue)
                                goodAdStr = goodAdStr.plus(it.dictLabel + "/")
                            }
                            if (!TextUtils.isEmpty(goodAdStr)) {
                                val take = goodAdStr.take(goodAdStr.length - 1)
                                binding.layoutGoodAt.tvRememberTime.text = take
                                params["questionTypeCodes"] = questionTypes
                            }

                        }
                    }
                }
            }, true)
        }
        circleAskScreenDialog?.show()
    }

}