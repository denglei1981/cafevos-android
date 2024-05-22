package com.changanford.shop.ui.coupon.adapter

import android.view.View
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.CouponsItemBean
import com.changanford.common.util.MUtils
import com.changanford.common.util.TimeUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.widget.loadmore.TheHellLoadMoreView
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemCanUseCouponBinding
import com.changanford.shop.ui.coupon.UseCouponsActivity

class CouponCanUseAdapter() :
    BaseQuickAdapter<CouponsItemBean, BaseDataBindingHolder<ItemCanUseCouponBinding>>(R.layout.item_can_use_coupon),
    LoadMoreModule {
    init {
        addChildClickViewIds(R.id.iv_extends, R.id.tv_use_now)
        loadMoreModule.loadMoreView = TheHellLoadMoreView()
    }

    override fun convert(
        holder: BaseDataBindingHolder<ItemCanUseCouponBinding>,
        item: CouponsItemBean
    ) {
        holder.dataBinding?.apply {
            MUtils.setTopMargin(root, 15, holder.layoutPosition)
            GlideUtils.loadBD(item.img, imgGoodsCover)
            tvUseNow.text = "去使用"
            GlideUtils.loadBD(item.markImg, ivVipTips)
            ivVipTips.isVisible = !item.markImg.isNullOrEmpty()
            tvMoney.text = item.getShowMoney()
            tvExtendsTips.text = item.getBottomTips()
            tvGoodsTitle.text = item.couponName
            tvTimeStart.text = TimeUtils.MillisToStrO(item.validityBeginTime).plus("起")
            tvTimeEnd.text = TimeUtils.MillisToStrO(item.validityEndTime).plus("止")
            tvTips.text = item.getTips()
            ivExtends.setOnClickListener {
                if (!gExtends.isVisible) {
                    gExtends.visibility = View.VISIBLE
                } else {
                    gExtends.visibility = View.GONE
                }
            }
            tvUseNow.setOnClickListener {
                UseCouponsActivity.start(item)
            }
        }
    }

}