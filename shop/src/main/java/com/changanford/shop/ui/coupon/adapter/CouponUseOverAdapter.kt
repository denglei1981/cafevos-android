package com.changanford.shop.ui.coupon.adapter

import android.view.View
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.CouponsItemBean
import com.changanford.common.util.TimeUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.widget.loadmore.TheHellLoadMoreView
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemCanUseCouponBinding
import com.changanford.shop.databinding.ItemCanUseOverCouponBinding
import com.changanford.shop.utils.DateTimeUtil

class CouponUseOverAdapter() :
    BaseQuickAdapter<CouponsItemBean, BaseDataBindingHolder<ItemCanUseOverCouponBinding>>(R.layout.item_can_use_over_coupon),
    LoadMoreModule {
    init {
        addChildClickViewIds(R.id.iv_extends, R.id.tv_use_now)
        loadMoreModule.loadMoreView = TheHellLoadMoreView()
    }

    override fun convert(
        holder: BaseDataBindingHolder<ItemCanUseOverCouponBinding>,
        item: CouponsItemBean
    ) {
        holder.dataBinding?.apply {


            tvMoney.text = item.getShowMoney()
            tvExtendsTips.text = item.getBottomTips()
            tvGoodsTitle.text = item.couponName
            tvTimeStart.text = TimeUtils.MillisToStr(item.validityBeginTime).plus("起\n").plus(TimeUtils.MillisToStr(item.validityEndTime))

            tvTips.text = item.getTips()
            ivExtends.setOnClickListener {
                if (!gExtends.isVisible) {
                    gExtends.visibility = View.VISIBLE
                } else {
                    gExtends.visibility = View.GONE
                }

            }
        }
    }

}