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
import com.changanford.circle.R
import com.changanford.circle.databinding.DialogApplicationCircleManagementBinding
import com.changanford.circle.ext.loadImage
import com.changanford.circle.ext.loadImageNoOther
import com.changanford.circle.utils.HideKeyboardUtil
import com.changanford.circle.utils.setDialogParams
import com.changanford.common.basic.adapter.BaseAdapterOneLayout
import com.changanford.common.util.PictureUtil
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener

/**
 *Author lcw
 *Time on 2021/9/22
 * type 1申请 2申请中 3申请失败
 *Purpose 申请圈子管理
 */
class ApplicationCircleManagementDialog(
    private val mContext: Context,
    private val type: Int,
    themeResId: Int = R.style.StyleCommonDialog
) : Dialog(mContext, themeResId) {

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

    private fun initView() {
        showType1()
    }

    private fun initListener() {
        binding.run {
            ivClose.setOnClickListener {
                dismiss()
            }
            btnApply.setOnClickListener {
                showType2()
            }
            ivCard.setOnClickListener {
                PictureUtil.openGalleryOnePic(mContext as Activity,
                    object : OnResultCallbackListener<LocalMedia> {
                        override fun onResult(result: MutableList<LocalMedia>?) {
                            val data = result?.get(0)
                            val path = data?.path
                            ivCard.visibility = View.GONE
                            ivUpCard.loadImageNoOther(path)
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
        }
    }

    private fun showType2() {
        binding.run {
            llContent.visibility = View.GONE
            tvReasonContent.visibility = View.VISIBLE
            tvType.visibility = View.VISIBLE
            tvTypeContent.visibility = View.VISIBLE
            tvTypeContent.text = "审核中"
        }
    }

    private fun showType3() {
        binding.run {
            llContent.visibility = View.GONE
            tvReasonContent.visibility = View.VISIBLE
            tvType.visibility = View.VISIBLE
            tvTypeContent.visibility = View.VISIBLE
            tvFailContent.visibility = View.VISIBLE
            tvTypeContent.text = "审核失败"
            tvFailContent.text = "人满了"
        }
    }

    override fun dismiss() {
        super.dismiss()
        HideKeyboardUtil.hideKeyboard(binding.llView.windowToken)
    }
}