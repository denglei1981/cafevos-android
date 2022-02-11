package com.changanford.circle.ui.ask.activity


import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.databinding.ActivityMechainicMainBinding
import com.changanford.circle.interf.UploadPicCallback
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
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.common.utilext.logE
import com.changanford.common.utilext.toastShow
import com.changanford.common.widget.SelectDialog
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener


// 技师个人主页。
@Route(path = ARouterCirclePath.MechanicMainActivity)
class MechanicMainActivity : BaseActivity<ActivityMechainicMainBinding, MechanicMainViewModel>() {

    var circleAskScreenDialog: CircleAskScreenDialog?=null


    override fun initView() {
        StatusBarUtil.setStatusBarMarginTop(binding.layoutTitle.conTitle, this)
        binding.layoutTitle.tvTitle.text = "个人信息"
        binding.layoutTitle.barTvOther.text = "保存"
        binding.layoutTitle.barTvOther.visibility = View.VISIBLE
        binding.layoutTitle.barTvOther.setTextColor(ContextCompat.getColor(this,R.color.color_00095B))

        binding.ivHeader.setOnClickListener {
            selectIcon()
        }
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
    private var params = hashMapOf<String, String>()
    private fun save() {
           // 保存

        viewModel.upTechniciaInfo(false,params)
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
                    val map = HashMap<String, String>()
                    map["avatar"] = headIconUrl
                    map["introduction"]=technicianData?.introduction!!
                    map["nickName"]=technicianData?.nickName!!
                    map["questionTypeCodes"]= arrayListOf<String>("1","2").toString()
                    saveUserInfo( map)
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
    fun saveUserInfo(map: HashMap<String, String>) {
        viewModel.upTechniciaInfo(true,map)
    }

    var questionTypeList = arrayListOf<QuestionData>()
    var technicianData:TechnicianData?=null
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
    }

    private fun showInfo(technicianData: TechnicianData) {
        this.technicianData=technicianData
        GlideUtils.loadBD(technicianData.avater, binding.ivHeader)
        binding.etInfo.setText(technicianData.introduction)
        binding.layoutNickName.tvRememberTime.setText(technicianData.nickName)
        var goodAtStri = ""
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
    var  questionTypes = mutableListOf<String>()
    fun showScreenDialog(){
        if(circleAskScreenDialog==null){
            circleAskScreenDialog= CircleAskScreenDialog(this,this,object :
                AskCallback {
                override fun onResult(result: ResultData) {
                    when(result.resultCode){
                        ResultData.OK->{
                            val  questionData=  result.data  as List<QuestionData>
                            questionTypes.clear()
                            var goodAdStr=""
                            questionData.forEach {
                                questionTypes.add(it.dictValue)
                                goodAdStr=goodAdStr.plus(it.dictLabel+"/")
                            }
                            if(!TextUtils.isEmpty(goodAdStr)){
                                val take = goodAdStr.take(goodAdStr.length - 1)
                                binding.layoutGoodAt.tvRememberTime.text = take
                                params["questionTypeCodes"]=questionTypes.toString()
                            }

                        }
                    }
                }
            },true)
        }
        circleAskScreenDialog?.show()
    }

}