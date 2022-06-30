package com.changanford.common.ui

import android.app.Activity
import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.changanford.common.R
import com.changanford.common.bean.WaitReceiveBean
import com.changanford.common.databinding.PopWaitReceiveBinding
import com.changanford.common.net.*
import com.changanford.common.util.launchWithCatch
import com.changanford.common.utilext.toast
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.TranslationConfig

/**
 *Author lcw
 *Time on 2022/6/30
 *Purpose
 */
class WaitReceiveBindingPop(
    val fragment: Activity,
    val lifecycleOwner: LifecycleOwner,
    val bean: WaitReceiveBean,
    private val receiveSuccessInterface: ReceiveSuccessInterface
) : BasePopupWindow(fragment) {

    val viewDataBinding: PopWaitReceiveBinding? =
        DataBindingUtil.bind(createPopupById(R.layout.pop_wait_receive))

    init {
        contentView = viewDataBinding?.root
        initView()

    }

    private fun initView() {
        viewDataBinding?.let {
            it.run {
                tvNum.text = bean.integral.toString()
                ivClose.setOnClickListener { dismiss() }
                ivBtn.setOnClickListener { receiveGift() }
            }
        }
    }

    private fun receiveGift() {
        viewDataBinding?.tvReceive?.text = "正在领取..."
        lifecycleOwner.launchWithCatch {
            val body = HashMap<String, Any>()
            val randomKey = getRandomKey()
            body["recordId"] = bean.id
            ApiClient.createApi<NetWorkApi>()
                .receiveGift(body.header(randomKey), body.body(randomKey))
                .onSuccess {
                    receiveSuccessInterface.receiveSuccess()
                    dismiss()
                    "领取成功".toast()
                }.onWithMsgFailure {
                    it?.toast()
                    viewDataBinding?.tvReceive?.text = "立即领取"
                }
        }
    }

    //动画
    override fun onCreateShowAnimation(): Animation? {
        return AnimationHelper.asAnimation()
            .withTranslation(TranslationConfig.FROM_BOTTOM)
            .toShow()
    }

    override fun onCreateDismissAnimation(): Animation? {
        return AnimationHelper.asAnimation()
            .withTranslation(TranslationConfig.TO_BOTTOM)
            .toDismiss()
    }

    interface ReceiveSuccessInterface {
        fun receiveSuccess()
    }
}