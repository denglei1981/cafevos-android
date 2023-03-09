package com.changanford.circle.widget.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.changanford.circle.R
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.CircleStarRoleDto
import com.changanford.circle.bean.GetApplyManageBean
import com.changanford.circle.databinding.DialogApplicationCircleManagementBinding
import com.changanford.circle.ext.loadImage
import com.changanford.circle.ext.loadImageNoOther
import com.changanford.circle.utils.AnimScaleInUtil
import com.changanford.circle.utils.HideKeyboardUtil
import com.changanford.circle.utils.setDialogParams
import com.changanford.common.MyApp
import com.changanford.common.basic.adapter.BaseAdapterOneLayout
import com.changanford.common.bean.OcrBean
import com.changanford.common.helper.OSSHelper
import com.changanford.common.net.*
import com.changanford.common.util.AppUtils
import com.changanford.common.util.PictureUtil
import com.changanford.common.util.launchWithCatch
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener

/**
 *Author lcw
 *Time on 2021/9/22
 * type 0申请 1申请中 2申请失败
 *Purpose 申请圈子管理
 */
class ApplicationCircleManagementDialog(
    private val mContext: Context,
    private var type: Int,
    private var name: String?,
    private val mLifecycleOwner: LifecycleOwner,
    private val bean: GetApplyManageBean? = null,
    themeResId: Int = R.style.StyleCommonDialog
) : Dialog(mContext, themeResId) {

    private var picPath = ""
    private var cardUrl = ""

    private var binding: DialogApplicationCircleManagementBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.dialog_application_circle_management, null, false
    )

    init {
        setContentView(binding.root)
        setDialogParams(context, this, Gravity.BOTTOM)
        initView()
        initListener()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        binding.tvTitle.text = "申请$name"
        when (type) {
            0 -> {
                showType1()
            }
            1 -> {
                showType2()
            }
            2 -> {
                showType3()
            }
        }
    }

    private fun initListener() {
        binding.run {
            ivClose.setOnClickListener {
                dismiss()
            }
            rlCard.setOnClickListener {
                PictureUtil.openGalleryOnePic(mContext as Activity,
                    object : OnResultCallbackListener<LocalMedia> {
                        override fun onResult(result: List<LocalMedia>) {
                            val activity = mContext
                            for (media in result) {
                                var path: String? = ""
                                path = PictureUtil.getFinallyPath(media)
                                picPath = path

                                OSSHelper.init(activity).getOss(activity, path, object :
                                    OSSHelper.OSSListener {
                                    override fun upLoadInfo(info: CommonResponse<OcrBean>) {
                                        binding.ivCard.visibility = View.GONE
                                        binding.ivUpCard.loadImageNoOther(picPath)
                                        binding.etName.setText(info.data?.name)
                                        binding.etCardNum.setText(info.data?.num)
                                        cardUrl = info.data?.picUrl.toString()
                                    }

                                })
                            }
                        }

                        override fun onCancel() {

                        }

                    })

            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showType1() {
        binding.run {
            llContent.visibility = View.VISIBLE
            etReason.addTextChangedListener {
                tvInputSize.text = "${it?.length} / 100"
            }

            btnApply.text = "立即申请"
            btnApply.setOnClickListener {
                applyManager()
            }
        }
    }

    private fun showType2() {
        binding.run {
            llContent.visibility = View.GONE
            tvReasonContent.visibility = View.VISIBLE
            tvType.visibility = View.VISIBLE
            tvTypeContent.visibility = View.VISIBLE
            binding.ivCard.visibility = View.GONE
            tvTypeContent.text = "审核中"
            btnApply.text = "取消申请"

            rlCard.isEnabled = false
            etName.isEnabled = false
            etCardNum.isEnabled = false


            ivUpCard.loadImage(bean?.cardImg)
            etName.setText(bean?.name)
            etCardNum.setText(bean?.cardNum)
            tvReasonContent.text = bean?.applyReason

            btnApply.setOnClickListener {
                cancelApplyManager()
            }
        }
    }

    private fun showType3() {
        binding.run {
            llContent.visibility = View.GONE
            tvReasonContent.visibility = View.VISIBLE
            tvType.visibility = View.VISIBLE
            tvTypeContent.visibility = View.VISIBLE
            tvFailContent.visibility = View.VISIBLE
            binding.ivCard.visibility = View.GONE
            tvTypeContent.text = "审核失败"
            btnApply.text = "修改"

            rlCard.isEnabled = false
            etName.isEnabled = false
            etCardNum.isEnabled = false

            cardUrl = bean?.cardImg.toString()
            ivUpCard.loadImage(bean?.cardImg)
            etName.setText(bean?.name)
            tvFailContent.text = bean?.reason
            etCardNum.setText(bean?.cardNum)
            tvReasonContent.text = bean?.applyReason

            btnApply.setOnClickListener {
                showTypeChange()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showTypeChange() {
        binding.run {

            llContent.visibility = View.VISIBLE
            tvReasonContent.visibility = View.GONE
            tvType.visibility = View.GONE
            tvTypeContent.visibility = View.GONE
            tvFailContent.visibility = View.GONE
            binding.ivCard.visibility = View.GONE

            etReason.addTextChangedListener {
                tvInputSize.text = "${it?.length} / 100"
            }

            rlCard.isEnabled = true
            etName.isEnabled = true
            etCardNum.isEnabled = true

            btnApply.setOnClickListener {
                applyManager()
            }
        }
    }

    private fun applyManager() {
        binding.run {
            val name = etName.text.toString()
            val cardNum = etCardNum.text.toString()
            val reason = etReason.text.toString()
            if (cardUrl.isEmpty()) {
                "请上传身份证".toast()
                return
            }
            if (name.isEmpty()) {
                "请输入姓名".toast()
                return
            }
            if (cardNum.isEmpty()) {
                "请输入身份证".toast()
                return
            }
            if (reason.isEmpty()) {
                "请输入申请理由".toast()
                return
            }
            mLifecycleOwner.launchWithCatch {
                val hashMap = HashMap<String, Any>()
                val rKey = getRandomKey()
                bean?.let {
                    hashMap["circleId"] = bean.circleId
                    hashMap["circleStarRoleId"] = bean.circleStarRoleId
                    hashMap["name"] = name
                    hashMap["cardNum"] = cardNum
                    hashMap["cardImg"] = cardUrl
                    hashMap["applyReason"] = reason
                }
                ApiClient.createApi<CircleNetWork>()
                    .applyManager(hashMap.header(rKey), hashMap.body(rKey)).also {
                        it.msg.toast()
                        if (it.code == 0) {
                            dismiss()
                        } else {
                            it.msg.toast()
                        }
                    }
            }
        }

    }

    private fun cancelApplyManager() {
        mLifecycleOwner.launchWithCatch {
            val hashMap = HashMap<String, Any>()
            val rKey = getRandomKey()
            bean?.let {
                hashMap["circleId"] = bean.circleId
                hashMap["circleStarRoleId"] = bean.circleStarRoleId
            }
            ApiClient.createApi<CircleNetWork>()
                .cancelApplyManager(hashMap.header(rKey), hashMap.body(rKey)).also {
                    it.msg.toast()
                    if (it.code == 0) {
                        dismiss()
                    } else {
                        it.msg.toast()
                    }
                }
        }
    }

    override fun dismiss() {
        super.dismiss()
        HideKeyboardUtil.hideKeyboard(binding.llView.windowToken)
    }
}