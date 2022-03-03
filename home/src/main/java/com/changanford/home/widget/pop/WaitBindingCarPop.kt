package com.changanford.home.widget.pop

import android.view.Gravity
import android.view.animation.Animation
import android.widget.RadioGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.changanford.common.manger.RouterManger.startARouter
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.MConstant
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.toast
import com.changanford.home.R
import com.changanford.home.bean.BindCarBean
import com.changanford.home.databinding.LayoutWaitBindingCarBinding
import com.changanford.home.request.HomeV2ViewModel
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.TranslationConfig
import java.util.concurrent.TimeUnit

class WaitBindingCarPop(
    val fragment: Fragment,
    val viewModel: HomeV2ViewModel,
    private val dataBean: BindCarBean
) : BasePopupWindow(fragment) {
    val viewDataBinding: LayoutWaitBindingCarBinding =
        DataBindingUtil.bind(createPopupById(R.layout.layout_wait_binding_car))!!
    val isMyCarTips = "确认后将自动将爱车与您账户绑定"
    val isNotMyCarTips = "确认后车辆将不与您的账户绑定，如需绑定请进行车主认证"
    var isConfirm: Int = 1

    var vin:String=""
    init {
        contentView = viewDataBinding.root
        initView()
    }

    private fun initView() {
        viewDataBinding.apply {
            model = dataBean
            vin=dataBean.vin
            btnSubmit.clicks().throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (MConstant.token.isEmpty()) {
                        startARouter(ARouterMyPath.SignUI)
                    } else {// 提交
                        viewModel.confirmBindCar(isConfirm, vin = dataBean.vin)
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
                            tvSureTips.gravity=Gravity.CENTER
                        }
                        checkboxNot.id -> {
                            tvSureTips.text = isNotMyCarTips
                            isConfirm = 0
                            tvSureTips.gravity=Gravity.START
                        }
                    }
                }
            })
            GlideUtils.loadBD(dataBean.modelUrl, ivCar)
        }
        viewModel.confirmCarLiveData.observe(fragment) {
            if(isConfirm==1){
                "车辆绑定成功".toast()
            }else{
                "已确认".toast()
            }
            if(vin==viewModel.vinStr){
                this.dismiss()
            }
        }

    }

    private fun btnIsUse(isUse: Boolean) {
        viewDataBinding.btnSubmit.apply {
            isEnabled = isUse
            setText(if (isUse) R.string.str_immediatelyToReceive else R.string.str_isToReceive)
            setBackgroundResource(if (isUse) R.drawable.shape_00095b_20dp else R.drawable.shape_dd_20dp)
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