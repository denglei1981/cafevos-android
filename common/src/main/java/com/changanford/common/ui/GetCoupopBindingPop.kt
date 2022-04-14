package com.changanford.common.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.changanford.common.R
import com.changanford.common.adapter.CouponItemAdapter
import com.changanford.common.bean.CouponsItemBean
import com.changanford.common.databinding.PopGetCouponBinding
import com.changanford.common.net.*
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.launchWithCatch
import com.changanford.common.utilext.toast
import com.changanford.common.wutil.ScreenUtils
import com.google.gson.Gson
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.TranslationConfig

@SuppressLint("SetTextI18n")
class GetCoupopBindingPop(
    val fragment: Activity,
    val lifecycleOwner: LifecycleOwner,
    private val dataBean: MutableList<CouponsItemBean>?=null
) : BasePopupWindow(fragment) {
    val viewDataBinding: PopGetCouponBinding =
        DataBindingUtil.bind(createPopupById(R.layout.pop_get_coupon))!!

    val couponItemAdapter: CouponItemAdapter by lazy {
        CouponItemAdapter()

    }

    init {
        contentView = viewDataBinding.root
        initView()

    }


    private fun initView() {
        setMaxHeight(ScreenUtils.getScreenHeight(context)/4*3)
        viewDataBinding.apply {
            rvCoupon.adapter = couponItemAdapter
            dataBean?.let {
                couponItemAdapter.data = dataBean
                tvTitle.text = "恭喜您获得${dataBean.size}张优惠券"
            }
            if(dataBean==null){
                getCouponList(true)
            }
            ivClose.setOnClickListener {
                dismiss()
            }
            couponItemAdapter.setOnItemClickListener { adapter, view, position ->
                // 领取优惠券
                val couPonItem = couponItemAdapter.getItem(position)
                when (couPonItem.state) {
                    "PENDING" -> { // 待领取
                        val list = arrayListOf<String>()
                        list.add(couPonItem.couponSendId)
                        getCoupon(list,couponItemAdapter.data.size)
                    }
                    "TO_USE" -> { // 已领取
                        // 跳转到立即使用的界面
//                        UseCouponsActivity.start(couPonItem)
                        val  gson=Gson()
                        val cou=gson.toJson(couPonItem)
                        JumpUtils.instans?.jump(128,cou)
                    }
                    else -> { // 用不了了

                    }
                }
            }
            tvGet.setOnClickListener {
                // 全部一起领取了
                val listItem = couponItemAdapter.data
                val list = arrayListOf<String>()
                listItem.forEach { l ->
                    if (l.state == "PENDING") {
                        list.add(l.couponSendId)
                    }
                }
                getCoupon(list, 0)
            }


        }


    }

    fun getCoupon(list: ArrayList<String>, count:Int=0) {
        lifecycleOwner.launchWithCatch {
            val body = HashMap<String, Any>()
            val randomKey = getRandomKey()
            body["couponSendIds"] = list
            ApiClient.createApi<NetWorkApi>()
                .receiveCoupons(body.header(randomKey), body.body(randomKey))
                .onSuccess {
                    // 领取成功,刷新下数据
                        if(count<=1){
                            this.dismiss()
                        }else{
                            getCouponList()
                        }
                }.onWithMsgFailure {
                    it?.toast()
                }
        }
    }

    fun getCouponList(isSelfData:Boolean=false) {
        lifecycleOwner.launchWithCatch {
            val body = HashMap<String, Any>()
            val randomKey = getRandomKey()
            body["popup"]="NO"
            ApiClient.createApi<NetWorkApi>()
                .receiveList(body.header(randomKey), body.body(randomKey))
                .onSuccess {
                    // 领取成功
                    if(isSelfData){
                        if (it != null) {
                            viewDataBinding.tvTitle.text = "恭喜您获得${it.size}张优惠券"
                        }
                    }
                    couponItemAdapter.setNewData(it)
                }.onWithMsgFailure {
                    it?.toast()
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