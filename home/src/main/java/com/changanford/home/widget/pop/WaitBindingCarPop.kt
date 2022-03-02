package com.changanford.home.widget.pop

import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.changanford.common.manger.RouterManger.startARouter
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.MConstant
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

class WaitBindingCarPop(val fragment: Fragment, val viewModel: HomeV2ViewModel, private val dataBean: BindCarBean) : BasePopupWindow(fragment) {
    val viewDataBinding: LayoutWaitBindingCarBinding = DataBindingUtil.bind(createPopupById(R.layout.layout_wait_binding_car))!!
    init {
        contentView=viewDataBinding.root
        initView()
    }
    private fun initView(){
        viewDataBinding.apply {
            model=dataBean
            btnSubmit.clicks().throttleFirst(500, TimeUnit.MILLISECONDS).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if(MConstant.token.isEmpty()){
                        startARouter(ARouterMyPath.SignUI)
                    }else{
                        viewModel.doGetIntegral()
                        btnIsUse(false)
                    }
                }, {})
        }
//        viewModel.responseBeanLiveData.observe(fragment){
//            if(it?.isSuccess==true){//表示领取成功则立即跳转到积分明细
//                JumpUtils.instans?.jump(30)
//                this.dismiss()
//            }else btnIsUse(true)
//        }
    }
    private fun btnIsUse(isUse:Boolean){
        viewDataBinding.btnSubmit.apply {
            isEnabled=isUse
            setText(if(isUse)R.string.str_immediatelyToReceive else R.string.str_isToReceive)
            setBackgroundResource(if(isUse)R.drawable.shape_00095b_20dp else R.drawable.shape_dd_20dp)
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