package com.changanford.my.ui

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.bean.DialogBottomBean
import com.changanford.common.bean.FeedbackTagsItem
import com.changanford.common.net.onFailure
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithCodeFailure
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.ui.ConfirmPop
import com.changanford.common.ui.dialog.LoadDialog
import com.changanford.common.util.MConstant
import com.changanford.common.util.MineUtils
import com.changanford.common.util.PictureUtil
import com.changanford.common.util.PictureUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey.MINE_SUBMIT_FEEDBACK_SUCCESS
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.toast
import com.changanford.common.widget.SelectDialog
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.adapter.MineCommAdapter
import com.changanford.my.databinding.UiEditFeedbackBinding
import com.changanford.my.interf.UploadPicCallback
import com.changanford.my.viewmodel.SignViewModel
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import java.lang.Exception

/**
 *  文件名：MineEditFeedbackUI
 *  创建者: zcy
 *  创建日期：2020/5/11 19:31
 *  描述: 添加反馈
 *  修改描述：TODO
 *
 */
@Route(path = ARouterMyPath.MineEditFeedbackUI)
class MineEditFeedbackUI : BaseMineUI<UiEditFeedbackBinding, SignViewModel>() {

    private var isBack = false

    private var labelAdapter = MineCommAdapter.FeedbackLabelAdapter(R.layout.item_feedback_label)

    private var picAdapter = MineCommAdapter.UniUserAdapter(R.layout.item_uni_user)

    var datas = ArrayList<LocalMedia>()
    lateinit var dialog: LoadDialog

    var body = HashMap<String, Any>()


    override fun initView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        isBack = intent.getBooleanExtra("isBack", false)
        dialog = LoadDialog(this@MineEditFeedbackUI)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setLoadingText("图片上传中..")


        /**
         * 不需要常见问题
         */
//        binding.mineToolbar.toolbarSave.text = "常见问题"
//        binding.mineToolbar.toolbarSave.setOnClickListener {
//            startARouter(ARouterMyPath.MineFeedbackUI)
//        }

        binding.mineToolbar.toolbarTitle.text = "意见反馈"
        binding.mineToolbar.toolbar.setNavigationOnClickListener {
            back()
        }

        binding.inputMobile.setText(MConstant.mine_phone)

        binding.save.setOnClickListener {
            if (labelAdapter.checkedPosition == -1) {
                ToastUtils.showLongToast("请选择意见标签",this)
                return@setOnClickListener
            }
            if (binding.feedbackInput.text.toString().isEmpty()) {
                ToastUtils.showLongToast("请输入问题描述",this)
                return@setOnClickListener
            }
            if (binding.feedbackInput.text.toString().length < 5) {
                ToastUtils.showLongToast("请输入5-300字问题详细描述",this)
                return@setOnClickListener
            }
            if (binding.inputName.text.isNullOrEmpty()) {
                ToastUtils.showLongToast("请输入正确的姓名",this)
                return@setOnClickListener
            }
            if (!MineUtils.isMobileNO(binding.inputMobile.text.toString())) {
                ToastUtils.showLongToast("请输入正确的电话",this)
                return@setOnClickListener
            }

            if (datas.size >= 1) {
                dialog.show()
                viewModel.uploadFileWithWH(datas, object : UploadPicCallback {
                    override fun onUploadSuccess(files: ArrayList<String>) {
                        dialog.dismiss()
                        var paths: String = ""
                        files.forEach {
                            paths = paths + "${it},"
                        }
                        body["feedbackImgUrl"] = paths
                        submit()
                    }

                    override fun onUploadFailed(errCode: String) {
                        dialog.dismiss()
                    }

                    override fun onuploadFileprogress(progress: Long) {
                    }
                })
            } else {
                body.remove("feedbackImgUrl")
                submit()
            }
        }

        binding.feedbackRv.layoutManager = GridLayoutManager(this, 3)
        binding.feedbackRv.adapter = labelAdapter


        binding.feedbackPicRv.layoutManager = GridLayoutManager(this, 4)
        binding.feedbackPicRv.adapter = picAdapter
        picAdapter.addIcons(datas)

        picAdapter.setIconCallback(object : MineCommAdapter.IconOnclick {

            override fun callback(localMedia: LocalMedia?) {
                if (localMedia == null) {
                    selectIcon()
                } else {
                    datas.remove(localMedia)
                    picAdapter.addIcons(datas)
                }
            }
        })
    }

    override fun back() {
        LiveDataBus.get().with(MINE_SUBMIT_FEEDBACK_SUCCESS, Boolean::class.java)
            .postValue(false)
        super.back()
    }

    var lables = ArrayList<FeedbackTagsItem>()

    override fun initData() {
        var tag = -1
        intent?.getStringExtra("value")?.let {
            try {
                var json = com.alibaba.fastjson.JSONObject.parseObject(it)
                tag = json.getString("tagId").toInt()
                var content = json.getString("content")
                binding.feedbackInput.setText(content)
            }catch ( e:Exception){
                e.printStackTrace()
            }
        }
        viewModel.getFeedbackTags()
        viewModel._lables.observe(this,{
            it?.let {
                it.forEachIndexed { index, feedbackTagsItem ->
                    if (feedbackTagsItem.tagId == tag){
                        labelAdapter.checkedPosition = index
                        labelAdapter.canChange = false
                    }
                }
                lables.addAll(it)
                labelAdapter.addData(it)
            }

        })
    }

    /**
     * 提交意见
     */
    fun submit() {


        var lable = lables.get(labelAdapter.checkedPosition)

        body["tagId"] = lable.tagId.toString()
        body["tagName"] = lable.tagName
        body["feedbackContent"] = binding.feedbackInput.text.toString()
        body["userPhone"] = binding.inputMobile.text.toString()
        var name = binding.inputName.text.toString()
        body["userName"] = name


        viewModel.addFeedback(
            body){
            it.onSuccess {
                ToastUtils.showLongToast("提交成功",this)
                if (isBack) {
                    LiveDataBus.get().with(MINE_SUBMIT_FEEDBACK_SUCCESS, Boolean::class.java)
                        .postValue(true)
                } else {
                    startARouter(ARouterMyPath.MineFeedbackListUI)
                }
                finish()
            }.onWithCodeFailure {
                if (it == 108) {
                    var pop = ConfirmPop(this@MineEditFeedbackUI)
                    pop.title.text = "谢谢您的反馈！"
                    pop.title.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                    pop.title.setTextColor(Color.parseColor("#1C1E20"))
                    pop.title.visibility = View.VISIBLE
                    pop.contentText.text = "您之前已反馈过该问题，无需再次提交，如有疑问可在之前的反馈记录中进行再次反馈。"
                    pop.cancelBtn.text = "重新编辑"
                    pop.submitBtn.text = "返回列表"
                    pop.submitBtn.setOnClickListener {
                        finish()
                    }
                    pop.showPopupWindow()
                }
            }.onWithMsgFailure {
                it?.toast()
            }
        }
    }


    /**
     * 选择照片
     */
    fun selectIcon() {
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
     * 选择图片
     */
    fun pic() {
        PictureUtils.openGarlly(this@MineEditFeedbackUI, 9 - datas.size, object :
            OnResultCallbackListener<LocalMedia> {
            override fun onResult(result: List<LocalMedia>) {
                datas.addAll(result)
                picAdapter.addIcons(datas)

            }

            override fun onCancel() {

            }
        })
    }

    /**
     * 拍照
     */
    fun takePhoto() {

        PictureUtils.opencarcme(
            this@MineEditFeedbackUI,
            object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: List<LocalMedia>) {
                    datas.add(result.get(0))
                    picAdapter.addIcons(datas)

                }

                override fun onCancel() {
                    // 取消
                }
            })
    }


}
