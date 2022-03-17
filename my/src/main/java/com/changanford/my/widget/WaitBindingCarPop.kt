package com.changanford.my.widget

import android.app.Activity
import android.view.Gravity
import android.view.animation.Animation
import android.widget.RadioGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.changanford.common.bean.BindCarBean
import com.changanford.common.manger.RouterManger.startARouter
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.toast
import com.changanford.my.R
import com.changanford.my.databinding.LayoutWaitBindingCarBinding
import com.changanford.my.viewmodel.CarAuthViewModel
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.TranslationConfig
import java.util.concurrent.TimeUnit

class WaitBindingCarPop(
    val fragment: Activity,
    val lifecycleOwner: LifecycleOwner,
    val viewModel: CarAuthViewModel,
    private val dataBean: BindCarBean
) : BasePopupWindow(fragment) {
    val viewDataBinding: LayoutWaitBindingCarBinding =
        DataBindingUtil.bind(createPopupById(R.layout.layout_wait_binding_car))!!
    val isMyCarTips = "确认后将自动将爱车与您账户绑定"
    val isNotMyCarTips = "确认后车辆将不与您的账户绑定，如需绑定请进行车主认证"
    var isConfirm: Int = 1

    var vin: String = ""

    init {
        contentView = viewDataBinding.root
        initView()
    }

    private fun initView() {
        viewDataBinding.apply {
            model = dataBean
            vin = dataBean.vin
            btnSubmit.clicks().throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (MConstant.token.isEmpty()) {
                        startARouter(ARouterMyPath.SignUI)
                    } else {// 提交
                        if (isConfirm < 0) {
                            dismiss()
                        } else {
                            viewModel.confirmBindCar(isConfirm, vin = dataBean.vin)
                        }

                    }
                }, {})
            checkbox.isChecked = true
            checkboxNot.isChecked = false
            radioGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
                override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                    when (checkedId) {
                        checkbox.id -> {
                            tvSureTips.text = isMyCarTips
                            isConfirm = 1
                            tvSureTips.gravity = Gravity.CENTER
                        }
                        checkboxNot.id -> {
                            tvSureTips.text = isNotMyCarTips
                            isConfirm = 0
                            tvSureTips.gravity = Gravity.START
                        }
                        checkboxAgain.id -> {
                            isConfirm = -1
                            tvSureTips.text = ""
                            tvSureTips.gravity = Gravity.CENTER
                        }
                    }
                }
            })
            GlideUtils.loadBD(dataBean.modelUrl, ivCar)
        }
        viewModel.confirmCarLiveData.observe(lifecycleOwner) {
            if (isConfirm == 1) {
                LiveDataBus.get().with(LiveDataBusKey.AGGREE_CAR).postValue(1)
                "车辆绑定成功".toast()
            } else {
                "已确认".toast()
            }
            if (vin == viewModel.vinStr) {
                this.dismiss()
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
}