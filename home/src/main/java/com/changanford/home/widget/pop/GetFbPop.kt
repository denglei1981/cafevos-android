package com.changanford.home.widget.pop

import android.annotation.SuppressLint
import android.content.Context
import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.changanford.common.manger.RouterManger.startARouter
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.home.R
import com.changanford.home.bean.FBBean
import com.changanford.home.databinding.PopGetfbBinding
import com.changanford.home.request.HomeV2ViewModel
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.TranslationConfig
import java.util.concurrent.TimeUnit

class GetFbPop(
    val context: Context,
    val viewModel: HomeV2ViewModel,
    private val dataBean: FBBean,
    private val lifecycleOwner: LifecycleOwner
) : BasePopupWindow(context) {
    private val viewDataBinding: PopGetfbBinding =
        DataBindingUtil.bind(createPopupById(R.layout.pop_getfb))!!

    init {
        contentView = viewDataBinding.root
        initView()
    }

    @SuppressLint("CheckResult")
    private fun initView() {
        viewDataBinding.apply {
            model = dataBean
            btnSubmit.clicks().throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (MConstant.token.isEmpty()) {
                        startARouter(ARouterMyPath.SignUI)
                    } else {
                        viewModel.doGetIntegral()
                        btnIsUse(false)
                    }
                }, {})
            imgClose.setOnClickListener {
                dismiss()
            }
        }
        viewModel.responseBeanLiveData.observe(lifecycleOwner) {
            if (it?.isSuccess == true) {//表示领取成功则立即跳转到积分明细
                JumpUtils.instans?.jump(30)
                this.dismiss()
            } else btnIsUse(true)
        }
    }

    private fun btnIsUse(isUse: Boolean) {
        viewDataBinding.btnSubmit.apply {
            isEnabled = isUse
            setText(if (isUse) R.string.str_immediatelyToReceive else R.string.str_isToReceive)
            setBackgroundResource(if (isUse) R.drawable.shape_00095b_20dp else R.drawable.shape_dd_20dp)
        }
    }

    override fun onBackPressed(): Boolean {
        return false
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
}