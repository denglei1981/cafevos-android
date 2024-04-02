package com.changanford.common.widget.pop

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import com.changanford.common.R
import com.changanford.common.adapter.CircleMainMenuAdapter
import com.changanford.common.basic.adapter.OnRecyclerViewItemClickListener
import com.changanford.common.bean.CircleMainMenuBean
import com.changanford.common.databinding.PopCircleMainMenuBinding
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.SPUtils
import com.changanford.common.util.gio.updateMainGio
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.Direction
import razerdp.util.animation.TranslationConfig

/**
 *Author lcw
 *Time on 2021/9/24
 *Purpose
 */
class CircleMainMenuPop(private val context: Context, private val listener: CheckPostType) :
    BasePopupWindow(context) {

    private var binding: PopCircleMainMenuBinding =
        DataBindingUtil.bind(createPopupById(R.layout.pop_circle_main_menu))!!

    init {
        contentView = binding.root
        popupGravity = Gravity.BOTTOM or Gravity.END
    }

    private val adapter by lazy {
        CircleMainMenuAdapter(context)
    }

    override fun onCreateShowAnimation(): Animation {
        return AnimationHelper.asAnimation()
            .withTranslation(
                TranslationConfig()
                    .from(Direction.TOP)
            )
            .toShow()
    }

    override fun onCreateDismissAnimation(): Animation {
        return AnimationHelper.asAnimation()
            .withTranslation(TranslationConfig.TO_TOP)
            .toDismiss()
    }

    fun initShopData() {
        val list = arrayListOf(
            CircleMainMenuBean(R.mipmap.ic_shop_menu_coupon, "优惠券"),
            CircleMainMenuBean(R.mipmap.ic_shop_menu_order, "我的订单"),
            CircleMainMenuBean(R.mipmap.ic_shop_menu_line, "在线客服"),
            CircleMainMenuBean(R.mipmap.ic_shop_menu_cart, "购物车")
        )

        adapter.setItems(list)
        binding.ryManagement.adapter = adapter
        adapter.setOnItemClickListener(object : OnRecyclerViewItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                when (position) {
                    0 -> {
                        JumpUtils.instans?.jump(118)
                    }

                    1 -> {
                        JumpUtils.instans?.jump(52)
                    }

                    2 -> {
                        JumpUtils.instans?.jump(11)
                    }

                    3 -> {
                        JumpUtils.instans?.jump(119)
                    }
                }
                dismiss()
            }

        })
    }

    fun initData() {
        val param = SPUtils.getParam(context, "identityType", "").toString()
        val list = arrayListOf(
            CircleMainMenuBean(R.mipmap.circle_post_long_bar, "发文章"),
            CircleMainMenuBean(R.mipmap.circle_post_pic, "发动态"),
//            CircleMainMenuBean(R.mipmap.circle_post_video, "视频"),
            CircleMainMenuBean(R.mipmap.circle_post_question, "提问"),
            CircleMainMenuBean(R.mipmap.ic_home_scan_menu, "扫一扫"),
//            CircleMainMenuBean(R.mipmap.circle_post_question,"发活动"),
//            CircleMainMenuBean(R.mipmap.circle_post_question,"发投票")
        )
//        if(param!="TECHNICIAN"){
//
//        }
        adapter.setItems(list)
        binding.ryManagement.adapter = adapter

        adapter.setOnItemClickListener(object : OnRecyclerViewItemClickListener {
            override fun onItemClick(view: View?, position: Int) {

                when (position) {
                    0 -> {
                        listener.checkLongBar()
                        updateMainGio("发文章页", "发文章页")
                    }

                    1 -> {
                        listener.checkPic()
                        updateMainGio("发动态页", "发动态页")
                    }

                    2 -> {
//                        listener.checkVideo()
                        listener.checkQuestion()
                        updateMainGio("提问页", "提问页")
                    }

                    3 -> {
//                        listener.checkQuestion()
                        updateMainGio("扫一扫页", "扫一扫页")
                        JumpUtils.instans?.jump(61)
                    }
//                    4 -> {
//                    }
//                    5->{
//                        val bundle = Bundle()
//                        bundle.putString(IntentKey.CREATE_NOTICE_CIRCLE_ID, "1045")
//                        startARouter(ARouterCirclePath.ActivityFabuToupiao,bundle)
//                    }
                }
                dismiss()
            }

        })
    }

    interface CheckPostType {
        fun checkLongBar()
        fun checkPic()
        fun checkVideo()
        fun checkQuestion()
    }
}